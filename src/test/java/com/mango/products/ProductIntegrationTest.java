package com.mango.products;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mango.products.infrastructure.adapter.persistence.entity.PriceEntity;
import com.mango.products.infrastructure.adapter.persistence.entity.ProductEntity;
import com.mango.products.infrastructure.adapter.persistence.jpa.JpaProductRepository;
import com.mango.products.infrastructure.rest.dto.PriceRequest;
import com.mango.products.infrastructure.rest.dto.ProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class ProductIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("test-db")
            .withUsername("user")
            .withPassword("password");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JpaProductRepository jpaProductRepository;

    private UUID productId;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @BeforeEach
    void setUp() {
        jpaProductRepository.deleteAll();
        ProductEntity product = ProductEntity.builder()
                .name("Test Product")
                .description("Description for test product")
                .build();
        jpaProductRepository.save(product);
        productId = product.getId();
    }

    @Test
    @DisplayName("POST /products - Debe crear un nuevo producto")
    void createProduct_success() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .name("New Product")
                .description("New Description")
                .build();

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.description").value("New Description"));
    }

    @Test
    @DisplayName("POST /products - Nombre duplicado")
    void createProduct_duplicateName_returnsBadRequest() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .description("Another Description")
                .build();

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(containsString("Ya existe un producto con el nombre:")));
    }

    @Test
    @DisplayName("POST /products/{id}/prices - Agrega precio")
    void addPriceToProduct_success() throws Exception {
        PriceRequest request = PriceRequest.builder()
                .value(BigDecimal.valueOf(50.00))
                .initDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .build();

        mockMvc.perform(post("/products/{id}/prices", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("POST /products/{id}/prices - Producto no encontrado")
    void addPriceToProduct_productNotFound_returnsNotFound() throws Exception {
        UUID nonExistentProductId = UUID.randomUUID();
        PriceRequest request = PriceRequest.builder()
                .value(BigDecimal.valueOf(50.00))
                .initDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .build();

        mockMvc.perform(post("/products/{id}/prices", nonExistentProductId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value(containsString("Producto con ID " + nonExistentProductId)));
    }

    @Test
    @DisplayName("GET /products/{id}/prices?date=... - Precio actual")
    void getCurrentPrice_success() throws Exception {
        addPricesForTest();

        mockMvc.perform(get("/products/{id}/prices", productId)
                        .param("date", "2024-04-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(120.00));
    }

    @Test
    @DisplayName("GET /products/{id}/prices/history - Historial de precios")
    void getProductPriceHistory_success() throws Exception {
        addPricesForTest();

        mockMvc.perform(get("/products/{id}/prices/history", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.prices", hasSize(2)));
    }

    private void addPricesForTest() {
        ProductEntity product = jpaProductRepository.findById(productId).get();

        // 👇 Fuerza la carga de priceEntities antes de modificarla
        product.getPriceEntities().size();

        PriceEntity price1 = PriceEntity.builder()
                .value(BigDecimal.valueOf(120.00))
                .initDate(LocalDate.of(2024, 3, 1))
                .endDate(LocalDate.of(2024, 6, 30))
                .productEntity(product)
                .build();

        PriceEntity price2 = PriceEntity.builder()
                .value(BigDecimal.valueOf(150.00))
                .initDate(LocalDate.of(2024, 7, 1))
                .endDate(LocalDate.of(9999, 12, 31))
                .productEntity(product)
                .build();

        product.addPrice(price1);
        product.addPrice(price2);

        jpaProductRepository.save(product);
    }

}
