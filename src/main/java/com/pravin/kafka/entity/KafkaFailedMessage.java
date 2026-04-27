package com.pravin.kafka.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@Entity
@Table(name = "kafka_failed_messages")
public class KafkaFailedMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;

    @Lob
    private String message;

    @Lob
    private String error;

    private LocalDateTime createdAt = LocalDateTime.now();

    public KafkaFailedMessage() {
    }
}