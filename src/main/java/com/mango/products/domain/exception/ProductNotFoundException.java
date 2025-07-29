package com.mango.products.domain.exception;

// Esta es una RuntimeException, lo que significa que no necesitamos declararla o capturarla explícitamente.
// Spring Boot la gestionará y la mapeamos a un código de estado HTTP 404 (Not Found) en el GlobalExceptionHandler.
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}