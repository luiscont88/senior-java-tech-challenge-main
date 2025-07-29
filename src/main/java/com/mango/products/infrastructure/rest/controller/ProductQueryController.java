package com.mango.products.infrastructure.rest.controller;

import com.mango.products.domain.port.in.ProductQueryUseCase;
import com.mango.products.infrastructure.rest.dto.CurrentPriceResponse;
import com.mango.products.infrastructure.rest.dto.ProductResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductQueryController {

    private final ProductQueryUseCase queryService;

    public ProductQueryController(ProductQueryUseCase queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/{productId}/prices")
    public ResponseEntity<CurrentPriceResponse> getCurrentPrice(
            @PathVariable UUID productId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(queryService.getCurrentPrice(productId, date));
    }

    @GetMapping("/{productId}/prices/history")
    public ResponseEntity<ProductResponse> getProductPrices(@PathVariable UUID productId) {
        return ResponseEntity.ok(queryService.getProductPriceHistory(productId));
    }

}
