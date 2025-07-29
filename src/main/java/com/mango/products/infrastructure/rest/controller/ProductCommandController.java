package com.mango.products.infrastructure.rest.controller;

import com.mango.products.domain.port.in.ProductCommandUseCase;
import com.mango.products.infrastructure.rest.dto.PriceRequest;
import com.mango.products.infrastructure.rest.dto.PriceResponse;
import com.mango.products.infrastructure.rest.dto.ProductRequest;
import com.mango.products.infrastructure.rest.dto.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductCommandController {

    private final ProductCommandUseCase commandService;

    public ProductCommandController(ProductCommandUseCase commandService) {
        this.commandService = commandService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = commandService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{productId}/prices")
    public ResponseEntity<PriceResponse> addPriceToProduct(
            @PathVariable UUID productId,
            @Valid @RequestBody PriceRequest request) {
        PriceResponse priceResponse = commandService.addPriceToProduct(productId, request);
        return new ResponseEntity<>(priceResponse, HttpStatus.CREATED);
    }

}
