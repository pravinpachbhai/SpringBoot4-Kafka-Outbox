package com.pravin.kafka.controller;

import com.pravin.kafka.dto.ProductPrice;
import com.pravin.kafka.entity.Product;
import com.pravin.kafka.service.ProductServiceOutBox;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/price")
@Validated
public class PriceControllerOutBox {

    private static final Logger log = LoggerFactory.getLogger(PriceControllerOutBox.class);
    private final ProductServiceOutBox productService;

    public PriceControllerOutBox(ProductServiceOutBox productService) {
        this.productService = productService;
    }

    @PatchMapping("/{productId}/price")
    public ResponseEntity<String> updatePrice(@PathVariable Long productId,
                                              @Valid @RequestBody ProductPrice productPrice) {
        log.info("Updating price for productId={} with data={}", productId, productPrice);
        productService.updateProductPrice(productPrice);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Price update queued for processing");
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<Product>> getProducts() {
        log.info("Get all products");
        return ResponseEntity.ok(productService.getAllProducts());
    }

}
