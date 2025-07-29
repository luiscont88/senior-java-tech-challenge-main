package com.mango.products.domain.exception;

// Esta excepción se usará cuando no se encuentre un precio para una fecha específica,
// y la mapearemos a un código de estado HTTP 404 (Not Found).
public class PriceNotFoundException extends RuntimeException {
    public PriceNotFoundException(String message) {
        super(message);
    }
}