package com.mango.products.application.service;

import com.mango.products.domain.port.in.ProductCommandUseCase;
import com.mango.products.domain.exception.PriceConflictException;
import com.mango.products.domain.exception.ProductNotFoundException;
import com.mango.products.domain.model.Price;
import com.mango.products.domain.model.Product;
import com.mango.products.domain.port.out.PriceRepository;
import com.mango.products.domain.port.out.ProductRepository;
import com.mango.products.infrastructure.rest.dto.PriceRequest;
import com.mango.products.infrastructure.rest.dto.PriceResponse;
import com.mango.products.infrastructure.rest.dto.ProductRequest;
import com.mango.products.infrastructure.rest.dto.ProductResponse;
import com.mango.products.infrastructure.rest.mapper.PriceDtoMapper;
import com.mango.products.infrastructure.rest.mapper.ProductDtoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProductCommandService implements ProductCommandUseCase {

    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;
    private final ProductDtoMapper productDtoMapper;
    private final PriceDtoMapper priceDtoMapper;

    public ProductCommandService(ProductRepository productRepository,
                                 PriceRepository priceRepository,
                                 ProductDtoMapper productDtoMapper,
                                 PriceDtoMapper priceDtoMapper) {
        this.productRepository = productRepository;
        this.priceRepository = priceRepository;
        this.productDtoMapper = productDtoMapper;
        this.priceDtoMapper = priceDtoMapper;
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un producto con el nombre: " + request.getName());
        }

        Product product = productDtoMapper.toDomain(request);
        product = productRepository.save(product);
        return productDtoMapper.toResponse(product);
    }

    @Override
    @Transactional
    public PriceResponse addPriceToProduct(UUID productId, PriceRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Producto con ID " + productId + " no encontrado."));

        Price newPrice = priceDtoMapper.toDomain(request);

        if (!newPrice.isValidDateRange()) {
            throw new IllegalArgumentException("La fecha inicial debe ser anterior a la fecha final si ambas existen.");
        }

        long overlappingPricesCount = priceRepository.countOverlappingPrices(
                productId,
                request.getInitDate().toString(),
                request.getEndDate() != null ? request.getEndDate().toString() : null,
                Price.MAX_LOCAL_DATE);

        if (overlappingPricesCount > 0) {
            throw new PriceConflictException("El rango de fechas del precio se solapa con un precio existente para este producto.");
        }

        product.addPrice(newPrice);
        Product savedProduct = productRepository.save(product);

        Price createdPrice = savedProduct.getPrices().stream()
                .filter(p -> p.getInitDate().equals(newPrice.getInitDate()) &&
                        p.getValue().compareTo(newPrice.getValue()) == 0)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Precio no fue guardado correctamente."));

        return priceDtoMapper.toResponse(createdPrice);
    }

}
