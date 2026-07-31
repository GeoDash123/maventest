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

        Product product = new Product(null, "Laptop", 10);

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
                new Product(null, "Mouse", 20));

        mockMvc.perform(get("/products/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mouse"))
                .andExpect(jsonPath("$.stock").value(20));

    }

    @Test
    void shouldDeleteProduct() throws Exception {

        Product saved = repository.save(
                new Product(null, "Keyboard", 15));

        mockMvc.perform(delete("/products/" + saved.getId()))
                .andExpect(status().isNoContent());

    }



}