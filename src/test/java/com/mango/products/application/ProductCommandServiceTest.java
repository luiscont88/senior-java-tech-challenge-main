package com.mango.products.application;

import com.mango.products.application.service.ProductCommandService;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCommandServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ProductDtoMapper productDtoMapper;

    @Mock
    private PriceDtoMapper priceDtoMapper;

    @InjectMocks
    private ProductCommandService commandService;

    private UUID productId;
    private Product product;
    private ProductRequest productRequest;
    private ProductResponse productResponse;
    private PriceRequest priceRequest;
    private Price price;
    private PriceResponse priceResponse;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        product = new Product("Test Product", "Description", new HashSet<>());
        productRequest = ProductRequest.builder().name("Test Product").description("Description").build();
        productResponse = ProductResponse.builder().id(productId).name("Test Product").description("Description").prices(Collections.emptyList()).build();

        priceRequest = PriceRequest.builder().value(BigDecimal.valueOf(100.00)).initDate(LocalDate.of(2024, 1, 1)).endDate(LocalDate.of(2024, 1, 31)).build();
        price = new Price(1L, BigDecimal.valueOf(100.00), LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31), product);
        priceResponse = PriceResponse.builder().id(1L).value(BigDecimal.valueOf(100.00)).initDate(LocalDate.of(2024, 1, 1)).endDate(LocalDate.of(2024, 1, 31)).build();
    }

    @Test
    @DisplayName("Debe crear un producto correctamente")
    void createProduct_success() {
        when(productRepository.findByName(productRequest.getName())).thenReturn(Optional.empty());
        when(productDtoMapper.toDomain(productRequest)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productDtoMapper.toResponse(product)).thenReturn(productResponse);

        ProductResponse result = commandService.createProduct(productRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("Test Product");
        verify(productRepository).findByName(productRequest.getName());
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el nombre del producto ya existe")
    void createProduct_duplicateName_throwsException() {
        when(productRepository.findByName(productRequest.getName())).thenReturn(Optional.of(product));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            commandService.createProduct(productRequest);
        });

        assertThat(thrown.getMessage()).contains("Ya existe un producto con el nombre:");
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe agregar un precio a un producto correctamente")
    void addPriceToProduct_success() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(priceDtoMapper.toDomain(priceRequest)).thenReturn(price);
        when(priceRepository.countOverlappingPrices(any(), any(), any(), any())).thenReturn(0L);
        when(productRepository.save(product)).thenReturn(product);
        when(priceDtoMapper.toResponse(price)).thenReturn(priceResponse);

        PriceResponse result = commandService.addPriceToProduct(productId, priceRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getValue()).isEqualTo(BigDecimal.valueOf(100.00));
        verify(productRepository).findById(productId);
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el producto no existe")
    void addPriceToProduct_productNotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ProductNotFoundException thrown = assertThrows(ProductNotFoundException.class, () -> {
            commandService.addPriceToProduct(productId, priceRequest);
        });

        assertThat(thrown.getMessage()).contains("Producto con ID " + productId + " no encontrado.");
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el precio se solapa")
    void addPriceToProduct_overlapping() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(priceDtoMapper.toDomain(priceRequest)).thenReturn(price);
        when(priceRepository.countOverlappingPrices(any(), any(), any(), any())).thenReturn(1L);

        PriceConflictException thrown = assertThrows(PriceConflictException.class, () -> {
            commandService.addPriceToProduct(productId, priceRequest);
        });

        assertThat(thrown.getMessage()).contains("El rango de fechas del precio se solapa");
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el rango de fechas es inválido")
    void addPriceToProduct_invalidDateRange() {
        PriceRequest invalidRequest = PriceRequest.builder()
                .value(BigDecimal.valueOf(100))
                .initDate(LocalDate.of(2024, 2, 1))
                .endDate(LocalDate.of(2024, 1, 1))
                .build();

        Price invalidPrice = new Price(1L, BigDecimal.valueOf(100), LocalDate.of(2024, 1, 31), LocalDate.of(2024, 1, 1), product);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(priceDtoMapper.toDomain(invalidRequest)).thenReturn(invalidPrice);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            commandService.addPriceToProduct(productId, invalidRequest);
        });

        assertThat(thrown.getMessage()).contains("La fecha inicial");
        verify(productRepository, never()).save(any());
    }
}
