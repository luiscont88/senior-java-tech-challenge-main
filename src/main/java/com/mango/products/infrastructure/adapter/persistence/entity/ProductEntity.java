package com.mango.products.infrastructure.adapter.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true) // No puede ser nulo y debe ser único
    private String name;

    @Column(columnDefinition = "TEXT") // Usa TEXT para descripciones largas
    private String description;

    // Relación One-to-Many con Price.
    // 'mappedBy' indica que la relación es "controlada" por el campo 'product' en la entidad Price.
    // 'CascadeType.ALL' significa que las operaciones (persist, remove, merge) se propagarán a las entidades Price asociadas.
    // 'orphanRemoval = true' asegura que si un Price se desvincula de un Product, sea eliminado.
    // 'FetchType.LAZY' es una buena práctica para colecciones para evitar cargar todos los precios innecesariamente.
    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @Builder.Default
    private Set<PriceEntity> priceEntities = new HashSet<>(); // Usa Set para evitar duplicados y mejorar rendimiento en búsquedas

    // Métodos de conveniencia para añadir/eliminar precios, manteniendo la bidireccionalidad

    public void addPrice(PriceEntity priceEntity) {
        if (priceEntities == null) {
            priceEntities = new HashSet<>();
        }
        priceEntities.add(priceEntity);
        priceEntity.setProductEntity(this);
    }

    public void removePrice(PriceEntity priceEntity) {
        if (priceEntities != null) {
            priceEntities.remove(priceEntity);
            priceEntity.setProductEntity(null);
        }
    }
}