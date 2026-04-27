package com.pravin.kafka.component;
import com.pravin.kafka.dto.OutboxStatus;
import com.pravin.kafka.entity.OutboxEvent;
import com.pravin.kafka.repository.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@DependsOn("broker")
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPublisher(OutboxRepository outboxRepository,
                           KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    //Runs every 30 seconds regardless of execution time
    @Scheduled(fixedDelay = 30000)
    public void publish() {
        List<OutboxEvent> events = outboxRepository.fetchBatch();

        if (events.isEmpty()) {
            return;
        }

        log.info("Publishing {} outbox events", events.size());

        for (OutboxEvent event : events) {
            process(event);
        }
    }

    public void process(OutboxEvent event) {
        try {
            String payload = event.getPayload();

            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(event.getTopic(), event.getMessageKey(), payload);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    event.setStatus(OutboxStatus.SENT);
                    event.setProcessedAt(Instant.now());
                    log.info("Kafka ACK eventId={}", event.getEventId());
                } else {
                    event.setStatus(OutboxStatus.FAILED);
                    event.setLastError(ex.getMessage());
                    log.error("Kafka FAILED eventId={}", event.getEventId(), ex);
                }
                outboxRepository.save(event);
            });

            log.info("Published eventId={} topic={}",
                    event.getEventId(),
                    event.getTopic());

        } catch (Exception e) {
            log.error("Failed eventId={}", event.getEventId(), e);

            if (event.getRetryCount() >= 5) {
                event.setStatus(OutboxStatus.DLQ);
            } else {
                event.setStatus(OutboxStatus.FAILED);
                event.setRetryCount(event.getRetryCount() + 1);
                event.setNextRetryTime(
                        Instant.now().plusSeconds((long) Math.min(300,
                                Math.pow(2, event.getRetryCount()) * 5))
                );
            }
            event.setProcessedAt(Instant.now());
            event.setLastError(e.getMessage());
            outboxRepository.save(event);
        }
    }
}