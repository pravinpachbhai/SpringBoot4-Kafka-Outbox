package com.pravin.kafka.service;

import com.pravin.kafka.entity.KafkaFailedMessage;
import com.pravin.kafka.entity.OutboxEvent;
import com.pravin.kafka.repository.OutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxService {
    private final OutboxRepository outboxRepository;

    public OutboxService(OutboxRepository outboxRepository){
        this.outboxRepository= outboxRepository;
    }

    @Transactional
    public OutboxEvent save(OutboxEvent outboxEvent){
        return outboxRepository.save(outboxEvent);
    }
}
