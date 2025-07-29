package com.mango.products.infrastructure.rest.mapper;

import com.mango.products.domain.model.Product;
import com.mango.products.infrastructure.rest.dto.ProductRequest;
import com.mango.products.infrastructure.rest.dto.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = PriceDtoMapper.class)
public interface ProductDtoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "prices", ignore = true)
    Product toDomain(ProductRequest request);

    @Mapping(target = "prices", source = "prices")
    ProductResponse toResponse(Product product);

}
