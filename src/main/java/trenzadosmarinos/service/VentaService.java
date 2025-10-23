package trenzadosmarinos.service;

import trenzadosmarinos.dao.IClienteDAO;
import trenzadosmarinos.dao.IProductoDAO;
import trenzadosmarinos.dao.IVentaDAO;
import trenzadosmarinos.model.Cliente;
import trenzadosmarinos.model.DetalleVenta;
import trenzadosmarinos.model.Producto;
import trenzadosmarinos.model.Venta;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VentaService {

    private final IVentaDAO ventaDAO;
    private final IProductoDAO productoDAO;
    private final IClienteDAO clienteDAO;

    public VentaService(IVentaDAO ventaDAO, IProductoDAO productoDAO, IClienteDAO clienteDAO) {
        this.ventaDAO = ventaDAO;
        this.productoDAO = productoDAO;
        this.clienteDAO = clienteDAO;
    }

    /**
     * Registra una nueva venta.
     * Caso de Uso 3: "asociar productos a una venta, descontar automáticamente
     * el stock y guardar la operación en el historial del cliente."
     *
     * @param idCliente          ID del cliente que compra.
     * @param productosComprados Un Map donde la Key es ID_Producto y Value es Cantidad.
     * @throws Exception Si el stock no es suficiente o el producto/cliente no existe.
     */
    public void registrarVenta(int idCliente, Map<Integer, Integer> productosComprados) throws Exception {

        // 1. Validar Cliente
        Cliente cliente = clienteDAO.obtenerPorId(idCliente);
        if (cliente == null) {
            throw new Exception("Cliente con ID " + idCliente + " no encontrado.");
        }

        if (productosComprados == null || productosComprados.isEmpty()) {
            throw new Exception("No se puede registrar una venta sin productos.");
        }

        List<DetalleVenta> detalles = new ArrayList<>();
        List<Producto> productosAActualizar = new ArrayList<>(); // Para el stock
        double totalVenta = 0;

        // 2. Validar productos, stock y calcular total (Fase de Pre-validación)
        for (Map.Entry<Integer, Integer> entry : productosComprados.entrySet()) {
            int idProducto = entry.getKey();
            int cantidad = entry.getValue();

            if (cantidad <= 0) {
                throw new Exception("La cantidad para el producto ID " + idProducto + " debe ser positiva.");
            }

            Producto producto = productoDAO.obtenerPorId(idProducto);
            if (producto == null) {
                throw new Exception("Producto con ID " + idProducto + " no encontrado.");
            }
            if (producto.getStock() < cantidad) {
                throw new Exception("Stock insuficiente para '" + producto.getNombre() +
                        "'. Solicitado: " + cantidad + ", Disponible: " + producto.getStock());
            }

            // Guardar el precio al momento de la venta
            double precioEnVenta = producto.getPrecio();
            totalVenta += precioEnVenta * cantidad;

            // Preparar el detalle
            // IDs (0) se asignarán en el DAO
            detalles.add(new DetalleVenta(0, 0, idProducto, cantidad, precioEnVenta));

            // Preparar la actualización de stock
            producto.setStock(producto.getStock() - cantidad);
            productosAActualizar.add(producto);
        }

        // 3. Crear la Venta (Cabecera)
        // ID 0 se asignará en el DAO
        Venta nuevaVenta = new Venta(0, LocalDate.now(), idCliente, totalVenta, detalles);

        // --- INICIO DE OPERACIONES CRÍTICAS ---
        // Si esto fuera una BBDD distribuida, aquí iniciaría una transacción global (JTA).
        // Como no lo es, realizamos las operaciones secuencialmente.

        // 4. Descontar el stock (Actualizar Productos)
        try {
            for (Producto p : productosAActualizar) {
                productoDAO.actualizar(p);
            }
        } catch (Exception e) {
            // Error al actualizar stock. NO continuamos con la venta.
            throw new Exception("Error crítico al actualizar el stock. Venta cancelada. " + e.getMessage());
        }

        // 5. Guardar la Venta y sus Detalles
        // El DAO (especialmente el JDBC) maneja su propia transacción interna
        // para asegurar que la Venta y sus Detalles se guarden atómicamente.
        try {
            ventaDAO.agregar(nuevaVenta);
        } catch (Exception e) {
            // La venta falló, pero el stock YA fue descontado.
            // Esto requiere una estrategia de compensación (re-añadir stock),
            // pero por ahora solo reportamos el error.
            throw new Exception("Error al guardar la venta, PERO el stock ya fue descontado. " + e.getMessage());
        }

        System.out.println("Venta registrada exitosamente con ID: " + nuevaVenta.getId() + " por un total de: " + totalVenta);
    }

    public List<Venta> obtenerTodasLasVentas() {
        return ventaDAO.obtenerTodos();
    }
}
