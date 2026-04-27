package com.pravin.kafka.repository;

import com.pravin.kafka.dto.OutboxStatus;
import com.pravin.kafka.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findTop100ByStatusAndNextRetryTimeBefore(OutboxStatus status, Instant nextRetryTime);

    List<OutboxEvent> findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus outboxStatus);

    @Query(value = """
                SELECT * FROM outbox_event
                WHERE status = 'PENDING'
                AND (next_retry_time IS NULL OR next_retry_time <= NOW())
                ORDER BY created_at
                LIMIT 100
                FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<OutboxEvent> fetchBatch();
}
