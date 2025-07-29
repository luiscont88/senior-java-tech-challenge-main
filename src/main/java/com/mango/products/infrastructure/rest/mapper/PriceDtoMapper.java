package com.mango.products.infrastructure.rest.mapper;

import com.mango.products.domain.model.Price;
import com.mango.products.infrastructure.rest.dto.CurrentPriceResponse;
import com.mango.products.infrastructure.rest.dto.PriceRequest;
import com.mango.products.infrastructure.rest.dto.PriceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PriceDtoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    Price toDomain(PriceRequest dto);

    PriceResponse toResponse(Price domain);

    CurrentPriceResponse toCurrentPriceResponse(Price domain);

}
