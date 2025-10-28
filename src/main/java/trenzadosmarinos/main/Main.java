package trenzadosmarinos.main;

import trenzadosmarinos.dao.DAOFactory;
import trenzadosmarinos.dao.IClienteDAO;
import trenzadosmarinos.dao.IProductoDAO;
import trenzadosmarinos.dao.IVentaDAO;
import trenzadosmarinos.dao.StorageType;
import trenzadosmarinos.service.ClienteService;
import trenzadosmarinos.service.ProductoService;
import trenzadosmarinos.service.VentaService;
import trenzadosmarinos.ui.MenuConsola;

public class Main {
    public static void main(String[] args) {

        //  ================================================================
        //  ||                     IMPORTANTE!!                           ||
        //  ||     AQUÍ ES EL ÚNICO CAMBIO PARA FASE 1 vs FASE 2          ||
        //  ================================================================

        // Opción 1: Usar Ficheros (Fase 1)
        StorageType TIPO_PERSISTENCIA = StorageType.FILE;

        // Opción 2: Usar Base de Datos (Fase 2)
        // StorageType TIPO_PERSISTENCIA = StorageType.JDBC;

        // ================================================================

        System.out.println("Iniciando sistema con persistencia: " + TIPO_PERSISTENCIA);

        // Creamos la DAOFactory con el tipo de persistencia elegido
        DAOFactory factory = new DAOFactory(TIPO_PERSISTENCIA);

        // Obtenemos las implementaciones de los DAOs necesarios
        IProductoDAO productoDAO = factory.getProductoDAO();
        IClienteDAO clienteDAO = factory.getClienteDAO();
        IVentaDAO ventaDAO = factory.getVentaDAO();

        // Inyectamos los DAOs en los Servicios
        ProductoService productoService = new ProductoService(productoDAO);
        ClienteService clienteService = new ClienteService(clienteDAO, ventaDAO);
        VentaService ventaService = new VentaService(ventaDAO, productoDAO, clienteDAO);

        // Iniciar la Interfaz de Usuario
        MenuConsola menu = new MenuConsola(productoService, clienteService, ventaService);
        menu.iniciar();
    }
}
