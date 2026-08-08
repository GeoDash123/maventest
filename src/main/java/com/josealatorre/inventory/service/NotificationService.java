package com.josealatorre.inventory.service;

import com.josealatorre.inventory.model.Product;

/**
 * Abstracción del servicio de notificaciones/alertas.
 * Aplica el Principio de Inversión de Dependencias (DIP): la lógica
 * de negocio (ProductServiceImpl) depende de esta interfaz, no de una
 * implementación concreta de notificación (consola, email, Slack, etc).
 */
public interface NotificationService {

    void alertLowStock(Product product);
}
