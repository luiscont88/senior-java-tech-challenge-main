package com.mango.products.infrastructure.adapter.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "prices", indexes = {
        @Index(name = "idx_price_product_id_dates", columnList = "product_id, init_date, end_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceEntity {

    public static final LocalDate MAX_LOCAL_DATE = LocalDate.of(9999, 12, 31); // Fecha muy lejana para representar 'infinito'
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "value", nullable = false, precision = 10, scale = 2)
    private BigDecimal value; // BigDecimal para precisión monetaria (10 dígitos en total, 2 decimales)
    @Column(name = "init_date", nullable = false, columnDefinition = "DATE")
    private LocalDate initDate;
    @Column(name = "end_date", columnDefinition = "DATE")
    private LocalDate endDate;
    @ManyToOne(fetch = FetchType.LAZY) // Relación Many-to-One con Product
    @JoinColumn(name = "product_id", nullable = false) // Columna de la clave foránea en la tabla 'prices'
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProductEntity productEntity;

}