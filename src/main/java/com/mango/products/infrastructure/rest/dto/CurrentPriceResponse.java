package com.mango.products.infrastructure.rest.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
@Builder
@Jacksonized
public class CurrentPriceResponse {
    BigDecimal value;
    LocalDate initDate;
    LocalDate endDate;
}