package com.mango.products.infrastructure.adapter.persistence.mapper;

import com.mango.products.domain.model.Product;
import com.mango.products.infrastructure.adapter.persistence.entity.PriceEntity;
import com.mango.products.infrastructure.adapter.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = PriceEntityMapper.class)
public interface ProductEntityMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "prices", target = "priceEntities")
    ProductEntity toEntity(Product domain);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "priceEntities", target = "prices")
    Product toDomain(ProductEntity entity);

    default ProductEntity toEntityWithPrices(Product domain, PriceEntityMapper priceEntityMapper) {
        ProductEntity entity = toEntity(domain);
        if (domain.getPrices() != null) {
            Set<PriceEntity> prices = domain.getPrices().stream()
                    .map(p -> priceEntityMapper.toEntityWithProduct(p, entity))
                    .collect(Collectors.toSet());
            entity.setPriceEntities(prices);
        }
        return entity;
    }

}
