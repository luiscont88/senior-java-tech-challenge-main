package com.mango.products.domain.port.out;

import com.mango.products.domain.model.Price;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PriceRepository {
    Optional<Price> findCurrentPriceByProductIdAndDate(UUID productId, LocalDate targetDate);

    long countOverlappingPrices(UUID productId, String newInitDate, String newEndDate, LocalDate maxDate);

    List<Price> findByProductIdOrderByInitDateAsc(UUID productId);
}