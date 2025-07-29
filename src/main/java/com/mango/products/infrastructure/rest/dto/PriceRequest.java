package com.mango.products.infrastructure.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
@Builder
@Jacksonized
public class PriceRequest {
    @NotNull(message = "El valor del precio no puede ser nulo.")
    @DecimalMin(value = "0.01", message = "El valor del precio debe ser mayor que cero.")
    BigDecimal value;

    @NotNull(message = "La fecha de inicio no puede ser nula.")
    LocalDate initDate;

    LocalDate endDate;
}