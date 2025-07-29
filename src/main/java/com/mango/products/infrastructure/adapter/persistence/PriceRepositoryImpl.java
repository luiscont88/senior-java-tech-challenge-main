package com.mango.products.infrastructure.adapter.persistence;

import com.mango.products.domain.model.Price;
import com.mango.products.domain.port.out.PriceRepository;
import com.mango.products.infrastructure.adapter.persistence.jpa.JpaPriceRepository;
import com.mango.products.infrastructure.adapter.persistence.mapper.PriceEntityMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PriceRepositoryImpl implements PriceRepository {

    private final JpaPriceRepository jpaPriceRepository;
    private final PriceEntityMapper priceEntityMapper;

    public PriceRepositoryImpl(JpaPriceRepository jpaPriceRepository, PriceEntityMapper priceEntityMapper) {
        this.jpaPriceRepository = jpaPriceRepository;
        this.priceEntityMapper = priceEntityMapper;
    }

    @Override
    public Optional<Price> findCurrentPriceByProductIdAndDate(UUID productId, LocalDate targetDate) {
        return jpaPriceRepository.findCurrentPriceByProductIdAndDate(productId, targetDate)
                .map(priceEntityMapper::toDomain);
    }

    @Override
    public long countOverlappingPrices(UUID productId, String newInitDate, String newEndDate, LocalDate maxDate) {
        return jpaPriceRepository.countOverlappingPrices(productId, newInitDate, newEndDate, maxDate);
    }

    @Override
    public List<Price> findByProductIdOrderByInitDateAsc(UUID productId) {
        return jpaPriceRepository.findByProductEntity_IdOrderByInitDateAsc(productId).stream()
                .map(priceEntityMapper::toDomain)
                .toList();
    }
}