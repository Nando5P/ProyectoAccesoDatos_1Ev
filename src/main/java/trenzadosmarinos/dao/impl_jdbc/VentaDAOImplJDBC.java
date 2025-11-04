package trenzadosmarinos.dao.impl_jdbc;

import trenzadosmarinos.dao.IVentaDAO;
import trenzadosmarinos.model.DetalleVenta;
import trenzadosmarinos.model.Venta;
import trenzadosmarinos.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAOImplJDBC implements IVentaDAO {

    @Override
    public void agregar(Venta venta) {
        String sqlVenta = "INSERT INTO ventas (fecha, id_cliente, total) VALUES (?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement psVenta = null;
        PreparedStatement psDetalle = null;
        ResultSet rsKeys = null;

        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            psVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            psVenta.setDate(1, Date.valueOf(venta.getFecha()));
            psVenta.setInt(2, venta.getIdCliente());
            psVenta.setDouble(3, venta.getTotal());
            psVenta.executeUpdate();

            int idVentaGenerada;
            rsKeys = psVenta.getGeneratedKeys();
            if (rsKeys.next()) {
                idVentaGenerada = rsKeys.getInt(1);
                venta.setId(idVentaGenerada);
            } else {
                throw new SQLException("No se pudo obtener el ID de la venta. Haciendo rollback.");
            }

            psDetalle = conn.prepareStatement(sqlDetalle);
            for (DetalleVenta detalle : venta.getDetalles()) {
                psDetalle.setInt(1, idVentaGenerada);
                psDetalle.setInt(2, detalle.getIdProducto());
                psDetalle.setInt(3, detalle.getCantidad());
                psDetalle.setDouble(4, detalle.getPrecioUnitario());
                psDetalle.addBatch();
            }
            psDetalle.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            System.err.println("Error al registrar la venta: " + e.getMessage());
            if (conn != null) {
                try {
                    System.err.println("Transacción fallida. Realizando rollback...");
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error al hacer rollback: " + ex.getMessage());
                }
            }
        } finally {
            try {
                if (rsKeys != null) rsKeys.close();
                if (psVenta != null) psVenta.close();
                if (psDetalle != null) psDetalle.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }

    private List<DetalleVenta> obtenerDetallesPorVentaId(int idVenta, Connection conn) throws SQLException {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sqlDetalle = "SELECT * FROM detalle_ventas WHERE id_venta = ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlDetalle)) {
            ps.setInt(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(new DetalleVenta(
                            rs.getInt("id"),
                            rs.getInt("id_venta"),
                            rs.getInt("id_producto"),
                            rs.getInt("cantidad"),
                            rs.getDouble("precio_unitario")
                    ));
                }
            }
        }
        return detalles;
    }

    @Override
    public Venta obtenerPorId(int id) {
        String sqlVenta = "SELECT * FROM ventas WHERE id = ?";
        Venta venta = null;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlVenta)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    venta = new Venta(
                            rs.getInt("id"),
                            rs.getDate("fecha").toLocalDate(),
                            rs.getInt("id_cliente"),
                            rs.getDouble("total"),
                            null
                    );
                    venta.setDetalles(obtenerDetallesPorVentaId(venta.getId(), conn));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener venta por ID: " + e.getMessage());
        }
        return venta;
    }

    @Override
    public List<Venta> obtenerTodos() {
        List<Venta> ventas = new ArrayList<>();
        String sqlVenta = "SELECT * FROM ventas";

        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlVenta)) {

            while (rs.next()) {
                Venta v = new Venta(
                        rs.getInt("id"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getInt("id_cliente"),
                        rs.getDouble("total"),
                        null
                );
                v.setDetalles(obtenerDetallesPorVentaId(v.getId(), conn));
                ventas.add(v);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las ventas: " + e.getMessage());
        }
        return ventas;
    }

    @Override
    public List<Venta> obtenerPorIdCliente(int idCliente) {
        List<Venta> ventas = new ArrayList<>();
        String sqlVenta = "SELECT * FROM ventas WHERE id_cliente = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlVenta)) {

            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Venta v = new Venta(
                            rs.getInt("id"),
                            rs.getDate("fecha").toLocalDate(),
                            rs.getInt("id_cliente"),
                            rs.getDouble("total"),
                            null
                    );
                    v.setDetalles(obtenerDetallesPorVentaId(v.getId(), conn));
                    ventas.add(v);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ventas por cliente: " + e.getMessage());
        }
        return ventas;
    }

    @Override
    public void eliminarTodos() {
        String sqlDetalle = "DELETE FROM detalle_ventas";
        String sqlVenta = "DELETE FROM ventas";

        try (Connection conn = ConexionDB.getConnection()) {
            conn.setAutoCommit(false); // Transacción

            try (PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle);
                 PreparedStatement psVenta = conn.prepareStatement(sqlVenta)) {

                psDetalle.executeUpdate();
                psVenta.executeUpdate();

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error al eliminar ventas: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.err.println("Error de conexión al eliminar ventas: " + e.getMessage());
        }
    }

    @Override
    public void agregarLote(List<Venta> ventas) {
        String sqlVenta = "INSERT INTO ventas (id, fecha, id_cliente, total) VALUES (?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalle_ventas (id, id_venta, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psVenta = conn.prepareStatement(sqlVenta);
                 PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle)) {

                for (Venta v : ventas) {
                    // Añadir Venta
                    psVenta.setInt(1, v.getId());
                    psVenta.setDate(2, Date.valueOf(v.getFecha()));
                    psVenta.setInt(3, v.getIdCliente());
                    psVenta.setDouble(4, v.getTotal());
                    psVenta.addBatch();

                    // Añadir sus Detalles
                    for (DetalleVenta d : v.getDetalles()) {
                        psDetalle.setInt(1, d.getId());
                        psDetalle.setInt(2, d.getIdVenta()); // Este ID debe coincidir con v.getId()
                        psDetalle.setInt(3, d.getIdProducto());
                        psDetalle.setInt(4, d.getCantidad());
                        psDetalle.setDouble(5, d.getPrecioUnitario());
                        psDetalle.addBatch();
                    }
                }

                // Importante orden de ejecución por las tablas, ya que son dependientes
                psVenta.executeBatch();
                psDetalle.executeBatch();
            }
            conn.commit();

        } catch (SQLException e) {
            System.err.println("Error en lote de ventas: " + e.getMessage());
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
        // Reiniciamos ambas tablas
        reiniciarContadorTabla("ventas");
        reiniciarContadorTabla("detalle_ventas");
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