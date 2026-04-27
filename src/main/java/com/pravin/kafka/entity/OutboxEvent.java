package com.pravin.kafka.entity;

import com.pravin.kafka.dto.OutboxStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@Entity
@Table(name = "outbox_event")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType;
    private String aggregateId;
    private String topic;
    private String eventId;
    private String eventType;
    private String lastError;
    @Lob
    private String payload;
    private String messageKey;
    private OutboxStatus status; // PENDING, SENT, FAILED

    private int retryCount;

    private Instant nextRetryTime;
    private Instant processedAt;
    private Instant createdAt;

    public OutboxEvent() {

    }

}