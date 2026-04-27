package com.pravin.kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pravin.kafka.dto.ProductPrice;
import com.pravin.kafka.entity.OutboxEvent;
import com.pravin.kafka.entity.Product;
import com.pravin.kafka.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

}
