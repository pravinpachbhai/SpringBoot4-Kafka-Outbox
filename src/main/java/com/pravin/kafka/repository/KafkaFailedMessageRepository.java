package com.pravin.kafka.repository;

import com.pravin.kafka.entity.KafkaFailedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KafkaFailedMessageRepository extends JpaRepository<KafkaFailedMessage, Long> {
}
