package trenzadosmarinos.dao;

import trenzadosmarinos.model.Producto;
import java.util.List;

public interface IProductoDAO {
    void agregar(Producto producto);
    void actualizar(Producto producto);
    void eliminar(int id);
    Producto obtenerPorId(int id);
    List<Producto> obtenerTodos();
}
