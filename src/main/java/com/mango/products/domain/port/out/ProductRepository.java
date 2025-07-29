package com.mango.products.domain.port.out;

import com.mango.products.domain.model.Product;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Optional<Product> findById(UUID id);

    Optional<Product> findByName(String name);

    boolean existsById(UUID id);

    Product save(Product product);
}