# Inventory API — Gestión de Inventario con Alertas de Stock Mínimo

API REST desarrollada en **Java 21 + Spring Boot 3.5.6** que administra el
inventario de productos (altas, consultas, actualización de stock,
eliminación) y aplica una regla de negocio crítica: **si el stock de un
producto cae por debajo de su mínimo permitido, el sistema dispara una
alerta automática**.

Este proyecto fue desarrollado como práctica de **TDD**, **principios
SOLID (SRP y DIP)**, **pruebas unitarias con Mockito** y **Integración
Continua con GitHub Actions**.

---

## 1. Modelo de dominio

| Clase | Responsabilidad |
|---|---|
| `Product` | Entidad de dominio/persistencia (JPA). Contiene `id`, `name`, `stock`, `minStock` y la regla de negocio `isBelowMinimum()`, que determina si el stock actual está por debajo del mínimo permitido. |
| `NotificationService` (interfaz) | Abstracción del servicio de alertas. No conoce productos ni reglas de inventario, solo expone `alertLowStock(Product)`. |
| `ConsoleNotificationService` | Implementación concreta de `NotificationService`. Hoy registra la alerta en el log; puede sustituirse por email/Slack/SMS sin tocar el resto del sistema. |
| `ProductRepository` | Acceso a datos (Spring Data JPA), aislado de la lógica de negocio. |
| `ProductService` / `ProductServiceImpl` | Lógica de negocio: valida, actualiza stock y decide cuándo notificar. |
| `ProductController` | Capa HTTP (`/products`), traduce peticiones REST a llamadas del servicio. |

> Nota: no existe una clase `Inventario` separada de `Product`; el
> concepto de inventario está representado por la colección de
> `Product` gestionada a través de `ProductRepository` /
> `ProductService`. Cada `Product` lleva su propio `minStock`, que es
> donde vive la regla de negocio.

---

## 2. Cómo se aplicó TDD

El cálculo de stock y la validación de alertas se probaron **antes** de
implementarse, siguiendo el ciclo rojo → verde:

1. **RED** — Se escribieron primero:
   - `ProductTest`: pruebas de `isBelowMinimum()` con stock por debajo,
     igual y por encima del mínimo.
   - `ProductServiceImplTest`: pruebas con Mockito que verifican si
     `NotificationService.alertLowStock(...)` es invocado tras
     `updateStock(...)`.
   En este punto el proyecto **no compilaba** (`minStock` y
   `NotificationService` aún no existían) — evidencia de que las
   pruebas se escribieron antes del código de producción.

2. **GREEN** — Se implementó lo mínimo necesario para que esas pruebas
   pasaran: el campo `minStock` y `isBelowMinimum()` en `Product`, la
   interfaz `NotificationService`, su implementación
   `ConsoleNotificationService`, y la invocación de la alerta dentro de
   `ProductServiceImpl.updateStock()`.

El historial de commits del repositorio refleja este orden
(`test: ... (RED)` seguido de `feat: ... (GREEN)`).

---

## 3. Cómo se aplicó SOLID (SRP y DIP)

**SRP (Single Responsibility Principle):**
- `ProductRepository` solo persiste datos.
- `ProductServiceImpl` solo contiene reglas de negocio (actualizar
  stock, decidir si hay que alertar).
- `ConsoleNotificationService` solo notifica; no sabe nada de
  repositorios ni de cómo se calcula el stock mínimo.
- `ProductController` solo traduce HTTP ↔ servicio.

Cada clase cambia por una única razón: si cambia el motor de
persistencia, solo se toca el repositorio; si cambia el canal de
alertas (de log a email, por ejemplo), solo se toca la implementación
de notificación.

**DIP (Dependency Inversion Principle):**
- `ProductServiceImpl` **no depende de una implementación concreta de
  notificaciones**, depende de la interfaz `NotificationService`,
  inyectada por constructor:

  ```java
  public ProductServiceImpl(ProductRepository repository,
                             NotificationService notificationService) {
      this.repository = repository;
      this.notificationService = notificationService;
  }
  ```

  Esto permite sustituir `ConsoleNotificationService` por cualquier
  otra implementación (email, Slack, cola de mensajes) sin modificar
  `ProductServiceImpl`, y permite sustituirla por un **mock** en las
  pruebas unitarias sin arrancar un contenedor Spring completo.

---

## 4. Pruebas y Mocks

- **Unitarias con Mockito** (`ProductServiceImplTest`): `ProductRepository`
  y `NotificationService` se mockean con `@Mock`, y se inyectan en
  `ProductServiceImpl` con `@InjectMocks`. Esto aísla completamente la
  lógica de negocio de la base de datos y del canal de notificación
  real. En particular:
  - `shouldTriggerAlertWhenStockFallsBelowMinimum` → verifica con
    `verify(notificationService, times(1)).alertLowStock(...)` que la
    alerta se dispara cuando el stock queda por debajo del mínimo.
  - `shouldNotTriggerAlertWhenStockIsAtOrAboveMinimum` → verifica con
    `verify(notificationService, never()).alertLowStock(...)` que no
    se dispara ninguna alerta si el stock está en o sobre el mínimo.
- **Unitarias de dominio** (`ProductTest`): prueban `isBelowMinimum()`
  de forma aislada, sin Spring ni mocks.
- **De integración** (`ProductControllerTest`): usan
  `@SpringBootTest` + `MockMvc` contra una base H2 real (no mockeada),
  cubriendo el flujo HTTP → controller → service → repository → BD.
  Incluye `shouldReflectLowStockWhenUpdatedBelowMinimum`, que confirma
  la regla de negocio de punta a punta.

---

## 5. Integración Continua (GitHub Actions)

El workflow `.github/workflows/maven.yml` se ejecuta automáticamente en
cada `push` y `pull request` a `main`/`master`:

1. Descarga el código (`actions/checkout`).
2. Configura JDK 21 (`actions/setup-java`).
3. Ejecuta `mvn clean test` (o `mvn -B package`, según la versión del
   workflow), compilando el proyecto y corriendo toda la suite de
   pruebas (unitarias, de dominio y de integración).
4. El pipeline falla si cualquier prueba falla o si el proyecto no
   compila, bloqueando así cambios que rompan la regla de negocio o el
   resto del sistema.

Puedes ver el resultado en la pestaña **Actions** del repositorio en
GitHub.

---

## 6. Cómo ejecutar las pruebas localmente

Requisitos: **JDK 21** y conexión a internet (para que Maven descargue
las dependencias de Spring Boot la primera vez).

```bash
# Clonar el repositorio
git clone <url-del-repositorio>
cd maventest

# Ejecutar toda la suite de pruebas
mvn clean test

# (Opcional) Ejecutar la aplicación localmente
mvn spring-boot:run
```

Salida esperada: `BUILD SUCCESS`, con el resumen de pruebas mostrando
0 fallos, por ejemplo:

```
Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
```

Si usas el wrapper de Maven en lugar de una instalación local:

```bash
./mvnw clean test        # Linux/macOS
mvnw.cmd clean test       # Windows
```

### Ejecutar un solo test o clase

```bash
mvn test -Dtest=ProductServiceImplTest
mvn test -Dtest=ProductServiceImplTest#shouldTriggerAlertWhenStockFallsBelowMinimum
```

---

## 7. Endpoints disponibles

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/products` | Crea un producto |
| `GET` | `/products/{id}` | Busca un producto por id |
| `GET` | `/products` | Lista todos los productos |
| `PUT` | `/products/{id}/stock?stock={valor}` | Actualiza el stock; dispara alerta si queda por debajo de `minStock` |
| `DELETE` | `/products/{id}` | Elimina un producto |
