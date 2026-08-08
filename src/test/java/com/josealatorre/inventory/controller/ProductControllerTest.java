package com.josealatorre.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josealatorre.inventory.model.Product;
import com.josealatorre.inventory.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void shouldCreateProduct() throws Exception {

        Product product = new Product(null, "Laptop", 10, 3);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.stock").value(10));

    }

    @Test
    void shouldReturnProductById() throws Exception {

        Product saved = repository.save(
                new Product(null, "Mouse", 20, 5));

        mockMvc.perform(get("/products/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mouse"))
                .andExpect(jsonPath("$.stock").value(20));

    }

    @Test
    void shouldDeleteProduct() throws Exception {

        Product saved = repository.save(
                new Product(null, "Keyboard", 15, 5));

        mockMvc.perform(delete("/products/" + saved.getId()))
                .andExpect(status().isNoContent());

    }

    @Test
    void shouldReflectLowStockWhenUpdatedBelowMinimum() throws Exception {

        // minStock = 10
        Product saved = repository.save(
                new Product(null, "Monitor", 20, 10));

        // Actualizamos el stock por debajo del mínimo a través del endpoint real
        mockMvc.perform(put("/products/" + saved.getId() + "/stock")
                        .param("stock", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(4))
                .andExpect(jsonPath("$.minStock").value(10));

        // Confirmamos en la capa de persistencia real que quedó por debajo del mínimo
        Product persisted = repository.findById(saved.getId()).orElseThrow();
        assertTrue(persisted.isBelowMinimum());
    }

}