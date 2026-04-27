package com.pravin.kafka.dto;

import java.math.BigDecimal;

public record ProductPrice(String productCode, BigDecimal price) {}