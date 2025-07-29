package com.mango.products.infrastructure.rest.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

@Value
@Builder
@Jacksonized
public class ProductResponse {
    UUID id;
    String name;
    String description;
    List<PriceResponse> prices; // Para el historial completo de precios
}