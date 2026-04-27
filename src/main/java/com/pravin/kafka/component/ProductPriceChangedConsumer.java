package com.pravin.kafka.component;

import com.pravin.kafka.dto.ProductPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@DependsOn("broker")
class ProductPriceChangedConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProductPriceChangedConsumer.class);

    // You don't need this class; just for testing I added
    // This event will be consume by another service

    //@KafkaListener(topics = "product-price-changes", groupId = "product")
    public void handle(ProductPrice productPrice) {
        log.info("Processing productCode={}", productPrice.productCode());
    }
}