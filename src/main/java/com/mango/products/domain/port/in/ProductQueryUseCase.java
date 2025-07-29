package com.mango.products.domain.port.in;

import com.mango.products.infrastructure.rest.dto.CurrentPriceResponse;
import com.mango.products.infrastructure.rest.dto.ProductResponse;

import java.time.LocalDate;
import java.util.UUID;

public interface ProductQueryUseCase {
    CurrentPriceResponse getCurrentPrice(UUID productId, LocalDate targetDate);
    ProductResponse getProductPriceHistory(UUID productId);
}
