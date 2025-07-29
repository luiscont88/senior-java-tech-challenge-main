package com.mango.products.domain.exception;

// Esta excepción se usará cuando haya un solapamiento de precios,
// y la mapearemos a un código de estado HTTP 409 (Conflict).
public class PriceConflictException extends RuntimeException {
    public PriceConflictException(String message) {
        super(message);
    }
}