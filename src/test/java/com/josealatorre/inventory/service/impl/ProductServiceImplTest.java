package com.josealatorre.inventory.service.impl;

import com.josealatorre.inventory.exception.ProductNotFoundException;
import com.josealatorre.inventory.model.Product;
import com.josealatorre.inventory.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImpl service;

    @Test
    void shouldSaveProduct() {

        Product product = new Product(null, "Laptop", 10);

        Product savedProduct = new Product(1L, "Laptop", 10);

        when(repository.save(product)).thenReturn(savedProduct);

        Product result = service.save(product);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getName());
        assertEquals(10, result.getStock());

        verify(repository).save(product);
    }

    @Test
    void shouldFindProductById() {

        Product product = new Product(1L, "Mouse", 20);

        when(repository.findById(1L))
                .thenReturn(Optional.of(product));

        Product result = service.findById(1L);

        assertEquals("Mouse", result.getName());

        verify(repository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {

        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> service.findById(1L));

        verify(repository).findById(1L);
    }

    @Test
    void shouldUpdateStock() {

        Product product = new Product(1L, "Keyboard", 5);

        when(repository.findById(1L))
                .thenReturn(Optional.of(product));

        when(repository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product updated = service.updateStock(1L, 30);

        assertEquals(30, updated.getStock());

        verify(repository).save(product);
    }

    @Test
    void shouldDeleteProduct() {

        Product product = new Product(1L, "Monitor", 2);

        when(repository.findById(1L))
                .thenReturn(Optional.of(product));

        service.delete(1L);

        verify(repository).delete(product);
    }

}

