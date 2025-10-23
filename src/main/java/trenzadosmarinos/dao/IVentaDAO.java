package trenzadosmarinos.dao;

import trenzadosmarinos.model.Venta;

import java.util.List;

public interface IVentaDAO {
    /**
     * Agrega una venta y sus detalles asociados.
     */
    void agregar(Venta venta);

    Venta obtenerPorId(int id);

    List<Venta> obtenerTodos();

    List<Venta> obtenerPorIdCliente(int idCliente);
}
