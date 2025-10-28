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

        // Validar productos, stock y calcular total
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
            detalles.add(new DetalleVenta(0, 0, idProducto, cantidad, precioEnVenta));

            // Preparar la actualización de stock
            producto.setStock(producto.getStock() - cantidad);
            productosAActualizar.add(producto);
        }

        // Crear la Venta
        Venta nuevaVenta = new Venta(0, LocalDate.now(), idCliente, totalVenta, detalles);

        // Descontar el stock (Actualizar Productos)
        try {
            for (Producto p : productosAActualizar) {
                productoDAO.actualizar(p);
            }
        } catch (Exception e) {
            // Error al actualizar stock. NO continuamos con la venta.
            throw new Exception("Error crítico al actualizar el stock. Venta cancelada. " + e.getMessage());
        }

        // Guardar la Venta y sus Detalles
        try {
            ventaDAO.agregar(nuevaVenta);
        } catch (Exception e) {
            throw new Exception("Error al guardar la venta, PERO el stock ya fue descontado. " + e.getMessage());
        }

        System.out.println("Venta registrada exitosamente con ID: " + nuevaVenta.getId() + " por un total de: " + totalVenta);
    }

    public List<Venta> obtenerTodasLasVentas() {
        return ventaDAO.obtenerTodos();
    }
}
