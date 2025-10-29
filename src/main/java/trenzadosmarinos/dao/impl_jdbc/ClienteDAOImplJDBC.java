package trenzadosmarinos.dao.impl_jdbc;

import trenzadosmarinos.dao.IClienteDAO;
import trenzadosmarinos.model.Cliente;
import trenzadosmarinos.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ClienteDAOImplJDBC implements IClienteDAO {

    @Override
    public void agregar(Cliente cliente) {
        String sql = "INSERT INTO clientes (nombre, direccion) VALUES (?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getDireccion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) cliente.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Cliente cliente) {
        String sql = "UPDATE clientes SET nombre = ?, direccion = ? WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getDireccion());
            ps.setInt(3, cliente.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Cliente obtenerPorId(int id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(rs.getInt("id"), rs.getString("nombre"), rs.getString("direccion"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Cliente> obtenerTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clientes.add(new Cliente(rs.getInt("id"), rs.getString("nombre"), rs.getString("direccion")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }

    @Override
    public void eliminarTodos() {
        String sql = "DELETE FROM clientes";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar todos los clientes: " + e.getMessage());
        }
    }

    @Override
    public void agregarLote(List<Cliente> clientes) {
        String sql = "INSERT INTO clientes (id, nombre, direccion) VALUES (?, ?, ?)";
        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Cliente c : clientes) {
                    ps.setInt(1, c.getId());
                    ps.setString(2, c.getNombre());
                    ps.setString(3, c.getDireccion());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();

        } catch (SQLException e) {
            System.err.println("Error en lote de clientes: " + e.getMessage());
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void reiniciarAutoIncrement() {
        reiniciarContadorTabla("clientes");
    }

    private void reiniciarContadorTabla(String tabla) {
        int maxId = 0;
        String sqlMax = "SELECT MAX(id) FROM " + tabla;
        String sqlAlter = "ALTER TABLE " + tabla + " AUTO_INCREMENT = ?";
        try (Connection conn = ConexionDB.getConnection()) {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlMax)) {
                if (rs.next()) maxId = rs.getInt(1);
            }
            int nextId = maxId + 1;
            try (PreparedStatement ps = conn.prepareStatement(sqlAlter)) {
                ps.setInt(1, nextId);
                ps.executeUpdate();
            }
            System.out.println("AUTO_INCREMENT de '" + tabla + "' reiniciado a " + nextId);
        } catch (SQLException e) {
            System.err.println("Error al reiniciar AUTO_INCREMENT de " + tabla + ": " + e.getMessage());
        }
    }
}
