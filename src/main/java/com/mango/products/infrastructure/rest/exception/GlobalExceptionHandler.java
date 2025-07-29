package com.mango.products.infrastructure.rest.exception;

import com.mango.products.domain.exception.PriceConflictException;
import com.mango.products.domain.exception.PriceNotFoundException;
import com.mango.products.domain.exception.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja las excepciones de validación (@Valid en DTOs).
     * Retorna 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja ProductNotFoundException y PriceNotFoundException.
     * Retorna 404 Not Found.
     */
    @ExceptionHandler({ProductNotFoundException.class, PriceNotFoundException.class})
    public ResponseEntity<ProblemDetail> handleNotFoundExceptions(RuntimeException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Recurso No Encontrado");
        problemDetail.setType(URI.create("not-found")); // URL para documentación del error
        problemDetail.setProperty("timestamp", Instant.now());
        return ResponseEntity.of(problemDetail).build();
    }

    /**
     * Maneja PriceConflictException.
     * Retorna 409 Conflict.
     */
    @ExceptionHandler(PriceConflictException.class)
    public ResponseEntity<ProblemDetail> handlePriceConflictException(PriceConflictException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Conflicto de Precios");
        problemDetail.setType(URI.create("price-conflict")); // URL para documentación del error
        problemDetail.setProperty("timestamp", Instant.now());
        return ResponseEntity.of(problemDetail).build();
    }

    /**
     * Maneja IllegalArgumentException (ej. fecha de inicio > fecha de fin, nombre de producto duplicado antes de DB).
     * Retorna 400 Bad Request.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Argumento Inválido");
        problemDetail.setType(URI.create("invalid-argument"));
        problemDetail.setProperty("timestamp", Instant.now());
        return ResponseEntity.of(problemDetail).build();
    }

    /**
     * Manejador genérico para cualquier otra excepción no capturada.
     * Retorna 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error inesperado.");
        problemDetail.setTitle("Error Interno del Servidor");
        problemDetail.setType(URI.create("internal-server-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        return ResponseEntity.of(problemDetail).build();
    }
}