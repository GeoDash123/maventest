package com.josealatorre.inventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer stock;

    /**
     * Cantidad mínima permitida en inventario antes de considerarse
     * stock bajo. Regla de negocio: si stock < minStock, se debe
     * disparar una alerta (ver ProductServiceImpl.updateStock).
     */
    private Integer minStock;

    /**
     * Regla de negocio central: determina si el stock actual del
     * producto está por debajo del mínimo permitido.
     */
    public boolean isBelowMinimum() {
        return stock != null && minStock != null && stock < minStock;
    }
}