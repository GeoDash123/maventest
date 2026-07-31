package com.josealatorre.inventory.service;

import com.josealatorre.inventory.model.Product;

import java.util.List;

public interface ProductService {

    Product save(Product product);

    Product findById(Long id);

    List<Product> findAll();

    Product updateStock(Long id, Integer stock);

    void delete(Long id);
}