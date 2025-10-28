package trenzadosmarinos.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    // Asegúrate de que la BBDD se llame 'tienda'
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/tienda?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "root";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver de MySQL no encontrado.");
            throw new SQLException("Driver no encontrado", e);
        }

        Connection conn = DriverManager.getConnection(URL, USER, PASS);
        return conn;
    }
}
