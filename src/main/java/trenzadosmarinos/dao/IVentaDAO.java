package trenzadosmarinos.dao;

import trenzadosmarinos.model.Venta;

import java.util.List;

public interface IVentaDAO {

    void agregar(Venta venta);

    Venta obtenerPorId(int id);

    List<Venta> obtenerTodos();

    List<Venta> obtenerPorIdCliente(int idCliente);

    void eliminarTodos();

    void agregarLote(List<Venta> ventas);

    void reiniciarAutoIncrement();
}
