package com.josealatorre.inventory.service.impl;

import com.josealatorre.inventory.exception.ProductNotFoundException;
import com.josealatorre.inventory.model.Product;
import com.josealatorre.inventory.repository.ProductRepository;
import com.josealatorre.inventory.service.NotificationService;
import com.josealatorre.inventory.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final NotificationService notificationService;

    public ProductServiceImpl(ProductRepository repository, NotificationService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    @Override
    public Product save(Product product) {
        return repository.save(product);
    }

    @Override
    public Product findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado"));
    }

    @Override
    public List<Product> findAll() {
        return repository.findAll();
    }

    @Override
    public Product updateStock(Long id, Integer stock) {
        Product product = findById(id);
        product.setStock(stock);
        Product saved = repository.save(product);

        if (saved.isBelowMinimum()) {
            notificationService.alertLowStock(saved);
        }

        return saved;
    }

    @Override
    public void delete(Long id) {
        Product product = findById(id);
        repository.delete(product);
    }
}