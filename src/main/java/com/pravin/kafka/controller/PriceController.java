package com.pravin.kafka.controller;

import com.pravin.kafka.component.KafkaProducer;
import com.pravin.kafka.dto.ProductPrice;
import com.pravin.kafka.entity.Product;
import com.pravin.kafka.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/price")
@Validated
public class PriceController {

    private static final Logger log = LoggerFactory.getLogger(PriceController.class);
    private final ProductService productService;
    private final KafkaProducer kafkaProducer;
    public PriceController(KafkaProducer kafkaProducer, ProductService productService) {
        this.kafkaProducer = kafkaProducer;
        this.productService = productService;
    }

    @PatchMapping()
     public ResponseEntity<String> updatePrice(@Valid @RequestBody ProductPrice productPrice) {
        kafkaProducer.send("product-price-changes", productPrice.productCode(), productPrice);
        return ResponseEntity.status(HttpStatus.OK).body("Price Updated");
    }

    @GetMapping(value = "/all")
     public ResponseEntity<List<Product>> getProducts() {
        log.info("Get all products");
        return ResponseEntity.ok(productService.getAllProducts());
    }

}
