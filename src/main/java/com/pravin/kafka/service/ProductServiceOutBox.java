package com.pravin.kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pravin.kafka.dto.OutboxStatus;
import com.pravin.kafka.dto.ProductPrice;
import com.pravin.kafka.entity.OutboxEvent;
import com.pravin.kafka.entity.Product;
import com.pravin.kafka.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ProductServiceOutBox {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceOutBox.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProductRepository productRepository;
    private final OutboxService outboxService;

    public ProductServiceOutBox(ProductRepository productRepository, OutboxService outboxService) {
        this.productRepository = productRepository;
        this.outboxService = outboxService;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional
    public void updateProductPrice(ProductPrice productPrice) {
        log.info("Updating price for productCode={} to {}",
                productPrice.productCode(),
                productPrice.price());
        int updated = productRepository.updateProductPrice(productPrice.productCode(), productPrice.price());
        if (updated == 0) {
            throw new IllegalArgumentException("Product not found: " + productPrice.productCode());
        }
        OutboxEvent event = new OutboxEvent();
        event.setAggregateType("Product");
        event.setEventId(productPrice.productCode() + "-" + System.currentTimeMillis());
        event.setMessageKey(productPrice.productCode());
        event.setAggregateId(productPrice.productCode());
        event.setTopic("product-price-changes");
        event.setEventType("PRICE_UPDATED");
        event.setRetryCount(0);
        event.setNextRetryTime(null);
        event.setLastError(null);
        event.setPayload(toJson(productPrice));
        event.setStatus(OutboxStatus.PENDING);
        event.setCreatedAt(Instant.now());
        outboxService.save(event);
        log.info("Outbox event created for productCode={}", productPrice.productCode());
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize ProductPrice", e);
        }
    }
}
