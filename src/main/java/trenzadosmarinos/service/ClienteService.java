package trenzadosmarinos.service;

import trenzadosmarinos.dao.IClienteDAO;
import trenzadosmarinos.dao.IVentaDAO;
import trenzadosmarinos.model.Cliente;
import trenzadosmarinos.model.Venta;

import java.util.ArrayList;
import java.util.List;

public class ClienteService {
    private final IClienteDAO clienteDAO;
    private final IVentaDAO ventaDAO; // Para el historial de compras

    public ClienteService(IClienteDAO clienteDAO, IVentaDAO ventaDAO) {
        this.clienteDAO = clienteDAO;
        this.ventaDAO = ventaDAO;
    }

    public void registrarCliente(String nombre, String direccion) {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.err.println("Error: El nombre del cliente no puede estar vacío.");
            return;
        }
        Cliente c = new Cliente(0, nombre, direccion); // ID 0 se autogenera
        clienteDAO.agregar(c);
        System.out.println("Cliente registrado: " + c);
    }

    public void actualizarCliente(Cliente cliente) {
        if (cliente == null || cliente.getId() <= 0) {
            System.err.println("Error: Cliente inválido para actualizar.");
            return;
        }
        clienteDAO.actualizar(cliente);
        System.out.println("Cliente actualizado.");
    }

    public void eliminarCliente(int id) {
        // En una BBDD real, habría que verificar que no tenga ventas asociadas
        // o configurar ON DELETE SET NULL / RESTRICT
        clienteDAO.eliminar(id);
        System.out.println("Cliente con ID " + id + " eliminado.");
    }

    public Cliente obtenerClientePorId(int id) {
        return clienteDAO.obtenerPorId(id);
    }

    public List<Cliente> obtenerTodosLosClientes() {
        return clienteDAO.obtenerTodos();
    }

    /**
     * Caso de uso: Consultar historial de compras del cliente.
     */
    public List<Venta> obtenerHistorialCompras(int idCliente) {
        if (clienteDAO.obtenerPorId(idCliente) == null) {
            System.err.println("No se puede obtener historial: Cliente no existe.");
            return new ArrayList<>();
        }
        return ventaDAO.obtenerPorIdCliente(idCliente);
    }
}
