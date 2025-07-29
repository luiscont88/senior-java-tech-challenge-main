package com.mango.products.infrastructure.adapter.persistence.mapper;

import com.mango.products.domain.model.Price;
import com.mango.products.domain.model.Product;
import com.mango.products.infrastructure.adapter.persistence.entity.PriceEntity;
import com.mango.products.infrastructure.adapter.persistence.entity.ProductEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PriceEntityMapper {

    @Mapping(target = "productEntity", ignore = true)
    PriceEntity toEntity(Price domain);

    @Mapping(target = "product", ignore = true)
    Price toDomain(PriceEntity entity);

    // Si necesitas mapear en cascada desde ProductEntity
    default Price toDomainWithProduct(PriceEntity entity, Product product) {
        Price price = toDomain(entity);
        price.setProduct(product);
        return price;
    }

    default PriceEntity toEntityWithProduct(Price price, ProductEntity productEntity) {
        PriceEntity entity = toEntity(price);
        entity.setProductEntity(productEntity);
        return entity;
    }
}
