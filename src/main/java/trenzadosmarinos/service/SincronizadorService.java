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

/**
 * Servicio para migrar datos entre la persistencia de Ficheros y JDBC.
 * Utiliza una estrategia de "Borrar y Cargar Todo" (Wipe-and-Load).
 */
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
        System.out.println("Iniciando migración de Ficheros -> Base de Datos...");

        // Obtener todos los datos de los ficheros
        List<Cliente> clientes = clienteFileDAO.obtenerTodos();
        List<Producto> productos = productoFileDAO.obtenerTodos();
        List<Venta> ventas = ventaFileDAO.obtenerTodos();

        // Borrar todos los datos de la BBDD (en orden inverso a las FK)
        System.out.println("Borrando datos antiguos de la BBDD...");
        // Borramos detalles y ventas primero
        ventaJdbcDAO.eliminarTodos();
        // Luego productos y clientes
        productoJdbcDAO.eliminarTodos();
        clienteJdbcDAO.eliminarTodos();

        // Insertar los datos en la BBDD (en orden correcto de FK)
        System.out.println("Insertando clientes...");
        for(Cliente c : clientes) {
            // El ID se autogenera, solo pasamos los datos
            clienteJdbcDAO.agregar(new Cliente(0, c.getNombre(), c.getDireccion()));
        }

        System.out.println("Insertando productos...");
        for(Producto p : productos) {
            productoJdbcDAO.agregar(new Producto(0, p.getNombre(), p.getPrecio(), p.getStock()));
        }

        System.out.println("Insertando ventas...");

        System.out.println("AVISO: La migración de ventas de Fichero a BD no es 100% fiable por los IDs.");
        ventas.forEach(ventaJdbcDAO::agregar);

        System.out.println("¡Migración a BBDD completada!");
    }

    public void migrarDeBdAFicheros() {
        System.out.println("Iniciando migración de Base de Datos -> Ficheros...");

        // Obtener todos los datos de la BBDD
        List<Cliente> clientes = clienteJdbcDAO.obtenerTodos();
        List<Producto> productos = productoJdbcDAO.obtenerTodos();
        List<Venta> ventas = ventaJdbcDAO.obtenerTodos();

        // Borrar todos los datos de los ficheros
        System.out.println("Borrando ficheros CSV antiguos...");
        ventaFileDAO.eliminarTodos();
        productoFileDAO.eliminarTodos();
        clienteFileDAO.eliminarTodos();

        // Insertar los datos en los ficheros
        // El DAO de Fichero asignará nuevos IDs secuenciales
        System.out.println("Escribiendo clientes...");
        clientes.forEach(clienteFileDAO::agregar);

        System.out.println("Escribiendo productos...");
        productos.forEach(productoFileDAO::agregar);

        System.out.println("Escribiendo ventas...");
        ventas.forEach(ventaFileDAO::agregar);

        System.out.println("¡Migración a Ficheros completada!");
    }
}
