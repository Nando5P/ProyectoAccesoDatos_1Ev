package trenzadosmarinos.dao;

import trenzadosmarinos.model.Cliente;

import java.util.List;

public interface IClienteDAO {
    void agregar(Cliente cliente);

    void actualizar(Cliente cliente);

    void eliminar(int id);

    Cliente obtenerPorId(int id);

    List<Cliente> obtenerTodos();

    void eliminarTodos();

    void agregarLote(List<Cliente> clientes);

    void reiniciarAutoIncrement();
}
