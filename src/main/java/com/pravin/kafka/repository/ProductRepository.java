package com.pravin.kafka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.pravin.kafka.entity.Product;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Modifying
    @Query("update Product p set p.price = :price where p.code = :productCode")
    int updateProductPrice(
            @Param("productCode") String productCode,
            @Param("price") BigDecimal price
    );
}