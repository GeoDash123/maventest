package com.josealatorre.inventory.service.impl;

import com.josealatorre.inventory.model.Product;
import com.josealatorre.inventory.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementación concreta de NotificationService.
 * SRP: su única responsabilidad es notificar; no sabe nada de
 * repositorios ni de reglas de inventario.
 */
@Service
public class ConsoleNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(ConsoleNotificationService.class);

    @Override
    public void alertLowStock(Product product) {
        log.warn("ALERTA: el producto '{}' (id={}) tiene stock {} por debajo del mínimo permitido ({})",
                product.getName(), product.getId(), product.getStock(), product.getMinStock());
    }
}
