package trenzadosmarinos.dao.impl_jdbc;

import trenzadosmarinos.dao.IProductoDAO;
import trenzadosmarinos.model.Producto;
import trenzadosmarinos.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAOImplJDBC implements IProductoDAO {

    @Override
    public void agregar(Producto producto) {
        String sql = "INSERT INTO productos (nombre, precio, stock) VALUES (?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setInt(3, producto.getStock());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    producto.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Producto producto) {
        String sql = "UPDATE productos SET nombre = ?, precio = ?, stock = ? WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setInt(3, producto.getStock());
            ps.setInt(4, producto.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM productos WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Producto obtenerPorId(int id) {
        String sql = "SELECT * FROM productos WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Producto(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getDouble("precio"),
                            rs.getInt("stock")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Producto> obtenerTodos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos";
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                productos.add(new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    @Override
    public void eliminarTodos() {
        String sql = "DELETE FROM productos";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar todos los productos: " + e.getMessage());
        }
    }

    @Override
    public void agregarLote(List<Producto> productos) {
        String sql = "INSERT INTO productos (id, nombre, precio, stock) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Producto p : productos) {
                    ps.setInt(1, p.getId());
                    ps.setString(2, p.getNombre());
                    ps.setDouble(3, p.getPrecio());
                    ps.setInt(4, p.getStock());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();

        } catch (SQLException e) {
            System.err.println("Error en lote de productos: " + e.getMessage());
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
        reiniciarContadorTabla("productos");
    }

    private void reiniciarContadorTabla(String tabla) {
        int maxId = 0;
        String sqlMax = "SELECT MAX(id) FROM " + tabla;
        String sqlAlter = "ALTER TABLE " + tabla + " AUTO_INCREMENT = ?";

        try (Connection conn = ConexionDB.getConnection()) {
            // Obtener el ID máximo actual
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlMax)) {
                if (rs.next()) {
                    maxId = rs.getInt(1);
                }
            }

            // Establecer el siguiente ID
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
