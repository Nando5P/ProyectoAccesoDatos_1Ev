package trenzadosmarinos.service;

import trenzadosmarinos.dao.IClienteDAO;
import trenzadosmarinos.dao.IProductoDAO;
import trenzadosmarinos.dao.IVentaDAO;
import trenzadosmarinos.dao.impl_file.ClienteDAOImplFile;
import trenzadosmarinos.dao.impl_file.ProductoDAOImplFile;
import trenzadosmarinos.dao.impl_file.VentaDAOImplFile;
import trenzadosmarinos.dao.impl_jdbc.ClienteDAOImplJDBC;
import trenzadosmarinos.dao.impl_jdbc.ProductoDAOImplJDBC;
import trenzadosmarinos.dao.impl_jdbc.VentaDAOImplJDBC;
import trenzadosmarinos.model.Cliente;
import trenzadosmarinos.model.Producto;
import trenzadosmarinos.model.Venta;

import java.util.List;

// Servicio para sincronizar datos entre ficheros y base de datos
public class SincronizadorService {

    // Instancia DAOs de Fichero
    private final IProductoDAO productoFileDAO = new ProductoDAOImplFile();
    private final IClienteDAO clienteFileDAO = new ClienteDAOImplFile();
    private final IVentaDAO ventaFileDAO = new VentaDAOImplFile();

    // Instancia DAOs de JDBC
    private final IProductoDAO productoJdbcDAO = new ProductoDAOImplJDBC();
    private final IClienteDAO clienteJdbcDAO = new ClienteDAOImplJDBC();
    private final IVentaDAO ventaJdbcDAO = new VentaDAOImplJDBC();

    public void migrarDeFicherosABd() {
        System.out.println("Iniciando migración de Ficheros -> Base de Datos (Preservando IDs)...");

        List<Cliente> clientes = clienteFileDAO.obtenerTodos();
        List<Producto> productos = productoFileDAO.obtenerTodos();
        List<Venta> ventas = ventaFileDAO.obtenerTodos();

        System.out.println("Borrando datos antiguos de la BBDD...");
        ventaJdbcDAO.eliminarTodos();
        productoJdbcDAO.eliminarTodos();
        clienteJdbcDAO.eliminarTodos();

        System.out.println("Insertando lote de clientes...");
        clienteJdbcDAO.agregarLote(clientes);

        System.out.println("Insertando lote de productos...");
        productoJdbcDAO.agregarLote(productos);

        System.out.println("Insertando lote de ventas y detalles...");
        ventaJdbcDAO.agregarLote(ventas);

        System.out.println("Actualizando contadores AUTO_INCREMENT...");
        clienteJdbcDAO.reiniciarAutoIncrement();
        productoJdbcDAO.reiniciarAutoIncrement();
        ventaJdbcDAO.reiniciarAutoIncrement();

        System.out.println("¡Migración a BBDD completada!");
    }


    public void migrarDeBdAFicheros() {
        System.out.println("Iniciando migración de Base de Datos -> Ficheros...");

        List<Cliente> clientes = clienteJdbcDAO.obtenerTodos();
        List<Producto> productos = productoJdbcDAO.obtenerTodos();
        List<Venta> ventas = ventaJdbcDAO.obtenerTodos();

        System.out.println("Borrando ficheros CSV antiguos...");
        ventaFileDAO.eliminarTodos();
        productoFileDAO.eliminarTodos();
        clienteFileDAO.eliminarTodos();

        System.out.println("Escribiendo lote de clientes...");
        clienteFileDAO.agregarLote(clientes);

        System.out.println("Escribiendo lote de productos...");
        productoFileDAO.agregarLote(productos);

        System.out.println("Escribiendo lote de ventas...");
        ventaFileDAO.agregarLote(ventas);

        System.out.println("¡Migración a Ficheros completada!");
    }
}