package com.mango.products.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Price {

    public static final LocalDate MAX_LOCAL_DATE = LocalDate.of(9999, 12, 31);

    private Long id;
    private BigDecimal value;
    private LocalDate initDate;
    private LocalDate endDate;
    private Product product;

    public Price() {
    }

    public Price(Long id, BigDecimal value, LocalDate initDate, LocalDate endDate, Product product) {
        this.id = id;
        this.value = value;
        this.initDate = initDate;
        this.endDate = endDate;
        this.product = product;
    }

    public Price(BigDecimal value, LocalDate initDate, LocalDate endDate, Product product) {
        this(null, value, initDate, endDate, product);
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public LocalDate getInitDate() {
        return initDate;
    }

    public void setInitDate(LocalDate initDate) {
        this.initDate = initDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    // Lógica de negocio

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public boolean isValidDateRange() {
        return endDate == null || initDate.isBefore(endDate);
    }
}
