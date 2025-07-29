package com.mango.products.domain.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Product {

    private UUID id;
    private String name;
    private String description;
    private Set<Price> prices = new HashSet<>();
    public Product() {
    }
    public Product(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Product(String name, String description) {
        this(null, name, description);
    }

    public Product(String name, String description, Set<Price> prices) {
        this(name, description);
        this.prices = prices;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Price> getPrices() {
        return prices;
    }


    public void setPrices(Set<Price> prices) {
        this.prices = prices;
    }

    // Métodos de negocio

    public void addPrice(Price price) {
        if (this.prices == null) {
            this.prices = new HashSet<>();
        }
        this.prices.add(price);
        price.setProduct(this); // mantener bidireccionalidad
    }

    public void removePrice(Price price) {
        if (this.prices != null) {
            this.prices.remove(price);
            price.setProduct(null);
        }
    }

}
