# SpringBoot4-Kafka-Outbox

OutboxPoller
     |
     +----> claim()
     |
     +----> publish()
     |
     +----> complete()
     |
     +----> retry()
	 
-------------------------------------

@Component
@RequiredArgsConstructor
public class OutboxPoller {

    private final OutboxService outboxService;

    @Scheduled(fixedDelay = 1000)
    public void poll() {

        List<OutboxEvent> events =
            outboxService.claim(100);

        for (OutboxEvent event : events) {
            outboxService.publish(event);
        }
    }
}	 

-------------------------------------

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public List<OutboxEvent> claim(int batchSize) {
        return repository.claimBatch(batchSize);
    }

    public void publish(OutboxEvent event) {

        kafkaTemplate
            .send(
                event.getTopic(),
                event.getAggregateId(),
                event.getPayload()
            )
            .whenComplete((result, error) -> {

                if (error == null) {
                    complete(event.getId());
                } else {
                    retry(event.getId(), error);
                }
            });
    }

    @Transactional
    public void complete(UUID eventId) {
        repository.markSent(eventId);
    }

    @Transactional
    public void retry(UUID eventId, Throwable error) {
        repository.markForRetry(eventId, error.getMessage());
    }
}

Claim - The poller atomically changes a batch of events:

UPDATE outbox
SET status = 'PROCESSING',
    locked_at = CURRENT_TIMESTAMP,
    locked_by = :instanceId
WHERE id IN (
    SELECT id
    FROM outbox
    WHERE status = 'NEW'
    ORDER BY created_at
    LIMIT 100
    FOR UPDATE SKIP LOCKED
)


Complete - If Kafka successfully acknowledges the message

UPDATE outbox SET status = 'SENT', sent_at = CURRENT_TIMESTAMP WHERE id = :eventId AND status = 'PROCESSING';


-------------------------------------

| Operation    | DB transaction? | Purpose                                 |
| ------------ | --------------- | --------------------------------------- |
| **Claim**    | Yes, short      | Reserve events for this poller          |
| **Publish**  | No              | Send to Kafka                           |
| **Complete** | Yes, short      | Mark Kafka-successful event as `SENT`   |
| **Retry**    | Yes, short      | Make failed/stale event available again |

-------------------------------------

Stale PROCESSING records

You can have another scheduled job that finds events stuck in PROCESSING.

SELECT * FROM outbox WHERE status = 'PROCESSING' AND locked_at < CURRENT_TIMESTAMP - INTERVAL '5 minutes';

-------------------------------------

What happens when Kafka fails? Don't leave the event stuck in PROCESSING.
Change it back: PROCESSING → NEW OR PROCESSING → RETRY 
Create SQL based on how you want to handle

UPDATE outbox
SET status = 'NEW',
    retry_count = retry_count + 1,
    next_attempt_at = CURRENT_TIMESTAMP + INTERVAL '30 seconds',
    last_error = :error
WHERE id = :eventId;

OR

UPDATE outbox
SET status = 'RETRY',
    retry_count = retry_count + 1,
    next_attempt_at = CURRENT_TIMESTAMP + INTERVAL '30 seconds',
    last_error = :error
WHERE id = :eventId;


-------------------------------------
