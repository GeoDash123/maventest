package com.josealatorre.inventory.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del dominio Product.
 * Escritas ANTES de implementar el campo minStock y la lógica isBelowMinimum()
 * (paso RED del ciclo TDD).
 */
class ProductTest {

    @Test
    void shouldBeBelowMinimumWhenStockIsLessThanMinStock() {
        Product product = new Product(1L, "Laptop", 3, 5);

        assertTrue(product.isBelowMinimum());
    }

    @Test
    void shouldNotBeBelowMinimumWhenStockEqualsMinStock() {
        Product product = new Product(1L, "Laptop", 5, 5);

        assertFalse(product.isBelowMinimum());
    }

    @Test
    void shouldNotBeBelowMinimumWhenStockIsGreaterThanMinStock() {
        Product product = new Product(1L, "Laptop", 10, 5);

        assertFalse(product.isBelowMinimum());
    }
}
