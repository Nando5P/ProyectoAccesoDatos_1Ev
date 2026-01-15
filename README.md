# 🏺 Proyecto: Sistema de Gestión para "Trenzados Marinos" (v2.0)

Este repositorio contiene el proyecto final de gestión desarrollado para la tienda de bisutería artesanal **“Trenzados Marinos”**. Esta aplicación ha sido diseñada y programada como un **proyecto educativo** para la asignatura de **Acceso a Datos** del ciclo de **FP Superior de Desarrollo de Aplicaciones Multiplataforma (DAM)**.

**Autor:** `Fernando Parga`  
**Centro Educativo:** `IES Fernando Wirtz`

---

## 🏬 Contexto del Negocio
**Trenzados Marinos** se dedica a la creación y venta de productos artesanales únicos (pulseras, collares y llaveros). El sistema v2.0 nace de la necesidad de profesionalizar la gestión de inventario y ventas, sustituyendo registros manuales por una **API REST profesional** capaz de garantizar la integridad y trazabilidad de cada operación.

---

## 🎯 Objetivo Académico
El objetivo primordial de este proyecto ha sido dominar el ecosistema de **Spring Boot** para la gestión avanzada de persistencia. Se ha hecho especial énfasis en:
* El desacoplamiento de capas mediante **DTOs**.
* La gestión automatizada de la base de datos con **Hibernate (JPA)**.
* El control transaccional de operaciones críticas para evitar inconsistencias en el stock.

---

## 🏗️ Arquitectura del Software
La aplicación se rige por una arquitectura profesional por capas, asegurando que cada componente tenga una responsabilidad única:



* **Controllers**: Gestionan las peticiones HTTP y exponen los recursos de la API.
* **DTOs (Data Transfer Objects)**: Protegen la integridad del modelo interno, transfiriendo solo la información necesaria al cliente.
* **Services**: Capa de lógica de negocio donde se aplica la transaccionalidad (`@Transactional`).
* **Repositories**: Interfaces de **Spring Data JPA** que eliminan la necesidad de escribir SQL manual.
* **Models (Entities)**: Clases Java que representan fielmente las tablas de MySQL mediante anotaciones JPA.

---

## 💾 Modelo de Datos (MySQL)
La base de datos relacional `tienda` se ha diseñado siguiendo el esquema técnico solicitado, optimizando los tipos de datos para su escalabilidad:



1.  **clientes**: Almacena datos de contacto (ID, nombre, dirección).
2.  **productos**: Controla el catálogo y el stock disponible.
3.  **ventas**: Registro de cabecera con fecha (`DATETIME`) y vinculación al cliente.
4.  **detalle_ventas**: Tabla relacional clave que utiliza identificadores de tipo **BIGINT** para gestionar de forma masiva los artículos vinculados a cada venta.

---

## 🔄 Evolución de la Persistencia
Siguiendo la hoja de ruta de la asignatura de **Acceso a Datos**, el proyecto ha evolucionado a través de los siguientes paradigmas:

1.  ~~**Fase 1 (Archivos)**: Almacenamiento básico en ficheros de texto (CSV).~~
2.  ~~**Fase 2 (JDBC)**: Gestión manual de MySQL mediante conectores y consultas SQL crudas.~~
3.  **Fase 3 (Spring Boot & Hibernate) 🚀**: Versión actual. Implementación de un ORM, inyección de dependencias y respuestas estandarizadas.

---

## 🛠️ Funcionalidades Destacadas
* **Manejo Global de Excepciones**: Implementación de un `GlobalExceptionHandler` que intercepta errores y devuelve respuestas amigables en formato JSON.
* **Integridad de Stock**: El sistema verifica existencias antes de confirmar una venta. Si falla algún paso, se realiza un **Rollback** automático.
* **Documentación OpenAPI**: Integración de **Swagger** para una exploración interactiva de la API.

---

## 📖 Cómo ejecutar el proyecto
1.  **Preparar la BD**: Crear una base de datos en MySQL llamada `tienda`.
2.  **Configurar credenciales**: Actualizar `src/main/resources/application.properties` con tu usuario y contraseña de MySQL.
3.  **Ejecutar**: Lanzar desde IntelliJ IDEA o mediante consola con `mvn spring-boot:run`.
4.  **Explorar la API**:
    🔗 **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

> **Nota Técnica:** Este proyecto ha sido documentado exhaustivamente mediante **Javadoc** para facilitar su mantenimiento y comprensión por otros desarrolladores.
