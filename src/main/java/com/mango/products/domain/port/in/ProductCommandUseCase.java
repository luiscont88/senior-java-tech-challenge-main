package com.mango.products.domain.port.in;

import com.mango.products.infrastructure.rest.dto.PriceRequest;
import com.mango.products.infrastructure.rest.dto.PriceResponse;
import com.mango.products.infrastructure.rest.dto.ProductRequest;
import com.mango.products.infrastructure.rest.dto.ProductResponse;

import java.util.UUID;

public interface ProductCommandUseCase {
    ProductResponse createProduct(ProductRequest request);
    PriceResponse addPriceToProduct(UUID productId, PriceRequest request);
}
