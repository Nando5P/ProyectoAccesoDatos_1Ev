# 🏺 Proyecto: Sistema de Gestión para Tienda de Bisutería

Este repositorio contiene el proyecto de un sistema de gestión diseñado para la tienda de bisutería “Trenzados marinos”.

## 🏬 Descripción de la Tienda

La tienda se dedica a la elaboración y venta de productos artesanales, especializándose en pulseras, collares y llaveros. Los artículos son creados a mano, utilizando materiales de calidad y ofreciendo diseños únicos, lo que permite a los clientes adquirir piezas originales y personalizadas.

## 🎯 Objetivo del Sistema

La aplicación contará con una base de datos que permitirá almacenar de manera organizada la información más relevante para la gestión de la tienda.

## 🛠️ Casos de Uso Principales

Los casos de uso más importantes del sistema serán:

* **Gestionar Productos 💎**: Incluye añadir, eliminar, actualizar y consultar productos.
* **Gestionar Clientes 👥**: Permitirá registrar nuevos clientes, actualizar su información y consultar el historial de compras.
* **Registrar Ventas 🛒**: Se podrán asociar productos a una venta, descontar automáticamente el stock y guardar la operación en el historial del cliente.

## 💾 Base de Datos del Sistema

La base de datos estará compuesta por tres tablas principales:

* **Productos**: Contendrá información de cada producto (ID, nombre, precio, cantidad en stock).
* **Clientes**: Almacenará los datos de los clientes (ID, nombre, dirección).
* **Ventas**: Guardará el registro de cada venta (ID, fecha, cliente asociado y total de la venta).

## 🔄 Persistencia de los Datos

La persistencia de los datos se desarrollará en dos fases:

1.  **Fase 1 (Archivos) 📄**: Almacenamiento en ficheros de texto (CSV o binarios) para cada entidad.
2.  **Fase 2 (MySQL) 🐬**: Migración a una base de datos MySQL utilizando JDBC. El objetivo de esta fase es gestionar los datos de forma más robusta, segura y escalable.
