package com.mango.products.infrastructure.adapter.persistence;

import com.mango.products.domain.model.Product;
import com.mango.products.domain.port.out.ProductRepository;
import com.mango.products.infrastructure.adapter.persistence.entity.ProductEntity;
import com.mango.products.infrastructure.adapter.persistence.jpa.JpaProductRepository;
import com.mango.products.infrastructure.adapter.persistence.mapper.PriceEntityMapper;
import com.mango.products.infrastructure.adapter.persistence.mapper.ProductEntityMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;
    private final ProductEntityMapper productEntityMapper;
    private final PriceEntityMapper priceEntityMapper;

    public ProductRepositoryImpl(JpaProductRepository jpaProductRepository,
                                 ProductEntityMapper productEntityMapper,
                                 PriceEntityMapper priceEntityMapper) {
        this.jpaProductRepository = jpaProductRepository;
        this.productEntityMapper = productEntityMapper;
        this.priceEntityMapper = priceEntityMapper;
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaProductRepository.findById(id)
                .map(productEntityMapper::toDomain);
    }

    @Override
    public Optional<Product> findByName(String name) {
        return jpaProductRepository.findByName(name)
                .map(productEntityMapper::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaProductRepository.existsById(id);
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = productEntityMapper.toEntityWithPrices(product, priceEntityMapper);
        ProductEntity saved = jpaProductRepository.save(entity);
        return productEntityMapper.toDomain(saved);
    }
}
