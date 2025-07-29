package com.mango.products.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ProductRequest {
    @NotBlank(message = "El nombre del producto no puede estar vacío.")
    // Valida que el campo no sea nulo y no esté en blanco
    @Size(max = 255, message = "El nombre del producto no puede exceder los 255 caracteres.") // Valida la longitud
    String name;

    @Size(max = 1000, message = "La descripción del producto no puede exceder los 1000 caracteres.") // Valida la longitud
    String description; // Puede ser nulo o vacío, por eso no @NotBlank
}