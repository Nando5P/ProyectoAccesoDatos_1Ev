package trenzadosmarinos.service;

import trenzadosmarinos.dao.IProductoDAO;
import trenzadosmarinos.model.Producto;

import java.util.List;

public class ProductoService {
    private final IProductoDAO productoDAO;

    public ProductoService(IProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    public void agregarProducto(String nombre, double precio, int stock) {
        if (precio <= 0 || stock < 0) {
            System.err.println("Datos de producto inválidos.");
            return;
        }
        Producto p = new Producto(0, nombre, precio, stock); // ID 0 se autogenera
        productoDAO.agregar(p);
    }

    public void actualizarProducto(Producto producto) {
        productoDAO.actualizar(producto);
    }

    public void eliminarProducto(int id) {
        productoDAO.eliminar(id);
    }

    public Producto obtenerProductoPorId(int id) {
        return productoDAO.obtenerPorId(id);
    }

    public List<Producto> obtenerTodosLosProductos() {
        return productoDAO.obtenerTodos();
    }
}
