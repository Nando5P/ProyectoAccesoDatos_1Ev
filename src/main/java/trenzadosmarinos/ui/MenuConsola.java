package trenzadosmarinos.ui;

import trenzadosmarinos.model.Cliente;
import trenzadosmarinos.model.Producto;
import trenzadosmarinos.service.ClienteService;
import trenzadosmarinos.service.ProductoService;
import trenzadosmarinos.service.VentaService;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class MenuConsola {
    private final ProductoService productoService;
    private final ClienteService clienteService;
    private final VentaService ventaService;
    private final Scanner scanner;

    public MenuConsola(ProductoService productoService, ClienteService clienteService, VentaService ventaService) {
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.ventaService = ventaService;
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        int opcion = -1;
        while (opcion != 0) {
            mostrarMenuPrincipal();
            try {
                opcion = scanner.nextInt();
                scanner.nextLine(); // Consumir newline

                switch (opcion) {
                    case 1: gestionarProductos(); break;
                    case 2: gestionarClientes(); break;
                    case 3: registrarVenta(); break;
                    case 4: consultarVentas(); break;
                    case 0: System.out.println("Saliendo del sistema..."); break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Debe ingresar un número.");
                scanner.nextLine(); // Limpiar buffer
            }
        }
        scanner.close();
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n--- TRENZADOS MARINOS - GESTIÓN ---");
        System.out.println("1. Gestionar Productos");
        System.out.println("2. Gestionar Clientes");
        System.out.println("3. Registrar Venta");
        System.out.println("4. Consultar Historial de Ventas");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }

    // --- Gestión de Productos ---
    private void gestionarProductos() {
        System.out.println("\n-- Gestión de Productos --");
        System.out.println("1. Añadir producto");
        System.out.println("2. Listar productos");
        System.out.println("3. Eliminar producto");
        System.out.print("Opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine();

        switch (opcion) {
            case 1: agregarProducto(); break;
            case 2: listarProductos(); break;
            case 3: eliminarProducto(); break;
            default: System.out.println("Opción no válida.");
        }
    }

    private void agregarProducto() {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Precio: ");
        double precio = scanner.nextDouble();
        System.out.print("Stock inicial: ");
        int stock = scanner.nextInt();
        scanner.nextLine();
        productoService.agregarProducto(nombre, precio, stock);
        System.out.println("Producto añadido.");
    }

    private void listarProductos() {
        System.out.println("\n-- Listado de Productos --");
        productoService.obtenerTodosLosProductos().forEach(System.out::println);
    }

    private void eliminarProducto() {
        listarProductos();
        System.out.print("ID del producto a eliminar: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        productoService.eliminarProducto(id);
        System.out.println("Producto eliminado.");
    }

    // --- Gestión de Clientes ---
    private void gestionarClientes() {
        System.out.println("\n-- Gestión de Clientes --");
        System.out.println("1. Registrar cliente");
        System.out.println("2. Listar clientes");
        System.out.print("Opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine();

        switch (opcion) {
            case 1: registrarCliente(); break;
            case 2: listarClientes(); break;
            default: System.out.println("Opción no válida.");
        }
    }

    private void registrarCliente() {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Dirección: ");
        String direccion = scanner.nextLine();
        clienteService.registrarCliente(nombre, direccion);
        System.out.println("Cliente registrado.");
    }

    private void listarClientes() {
        System.out.println("\n-- Listado de Clientes --");
        clienteService.obtenerTodosLosClientes().forEach(System.out::println);
    }

    // --- Gestión de Ventas ---
    private void registrarVenta() {
        System.out.println("\n-- Registrar Nueva Venta --");

        // 1. Seleccionar Cliente
        listarClientes();
        if (clienteService.obtenerTodosLosClientes().isEmpty()) {
            System.out.println("Debe registrar un cliente primero.");
            return;
        }
        System.out.print("Seleccione el ID del Cliente: ");
        int idCliente = scanner.nextInt();
        scanner.nextLine();

        // 2. Crear carrito
        Map<Integer, Integer> carrito = new HashMap<>();
        String mas = "s";
        while (mas.equalsIgnoreCase("s")) {
            listarProductos();
            System.out.print("Seleccione el ID del Producto: ");
            int idProducto = scanner.nextInt();
            System.out.print("Cantidad: ");
            int cantidad = scanner.nextInt();
            scanner.nextLine();

            carrito.put(idProducto, cantidad);

            System.out.print("¿Añadir más productos? (s/n): ");
            mas = scanner.nextLine();
        }

        // 3. Procesar Venta
        try {
            ventaService.registrarVenta(idCliente, carrito);
        } catch (Exception e) {
            System.err.println("\nERROR AL REGISTRAR VENTA: " + e.getMessage());
        }
    }

    private void consultarVentas() {
        System.out.println("\n-- Historial de Ventas --");
        ventaService.obtenerTodasLasVentas().forEach(venta -> {
            System.out.println(venta);
            // Opcional: imprimir detalles
            // venta.getDetalles().forEach(System.out::println);
        });
    }
}
