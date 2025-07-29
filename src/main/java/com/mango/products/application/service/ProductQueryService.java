package com.mango.products.application.service;

import com.mango.products.domain.port.in.ProductQueryUseCase;
import com.mango.products.domain.exception.PriceNotFoundException;
import com.mango.products.domain.exception.ProductNotFoundException;
import com.mango.products.domain.model.Price;
import com.mango.products.domain.model.Product;
import com.mango.products.domain.port.out.PriceRepository;
import com.mango.products.domain.port.out.ProductRepository;
import com.mango.products.infrastructure.rest.dto.CurrentPriceResponse;
import com.mango.products.infrastructure.rest.dto.ProductResponse;
import com.mango.products.infrastructure.rest.mapper.PriceDtoMapper;
import com.mango.products.infrastructure.rest.mapper.ProductDtoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class ProductQueryService implements ProductQueryUseCase {

    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;
    private final ProductDtoMapper productDtoMapper;
    private final PriceDtoMapper priceDtoMapper;

    public ProductQueryService(ProductRepository productRepository,
                               PriceRepository priceRepository,
                               ProductDtoMapper productDtoMapper,
                               PriceDtoMapper priceDtoMapper) {
        this.productRepository = productRepository;
        this.priceRepository = priceRepository;
        this.productDtoMapper = productDtoMapper;
        this.priceDtoMapper = priceDtoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public CurrentPriceResponse getCurrentPrice(UUID productId, LocalDate targetDate) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Producto con ID " + productId + " no encontrado.");
        }

        return priceRepository.findCurrentPriceByProductIdAndDate(productId, targetDate)
                .map(priceDtoMapper::toCurrentPriceResponse)
                .orElseThrow(() ->
                        new PriceNotFoundException("No se encontró un precio para el producto " + productId + " en la fecha " + targetDate + "."));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductPriceHistory(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Producto con ID " + productId + " no encontrado."));

        List<Price> prices = priceRepository.findByProductIdOrderByInitDateAsc(productId);
        product.setPrices(new HashSet<>(prices));

        return productDtoMapper.toResponse(product);
    }

}
