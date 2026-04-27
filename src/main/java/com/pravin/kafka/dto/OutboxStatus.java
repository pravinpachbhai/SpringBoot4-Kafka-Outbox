package com.pravin.kafka.dto;

public enum OutboxStatus {
    PENDING, SENT, FAILED, DLQ
}
