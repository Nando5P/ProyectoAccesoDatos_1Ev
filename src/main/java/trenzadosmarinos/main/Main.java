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

        // ================================================================
        // ==     AQUÍ ES EL ÚNICO CAMBIO PARA FASE 1 vs FASE 2           ==
        // ================================================================

        // Opción 1: Usar persistencia en Ficheros (Fase 1)
        StorageType TIPO_PERSISTENCIA = StorageType.FILE;

        // Opción 2: Usar persistencia en Base de Datos (Fase 2)
        // StorageType TIPO_PERSISTENCIA = StorageType.JDBC;

        // ================================================================


        System.out.println("Iniciando sistema con persistencia: " + TIPO_PERSISTENCIA);

        // 1. Creamos la fábrica con el tipo de persistencia elegido
        DAOFactory factory = new DAOFactory(TIPO_PERSISTENCIA);

        // 2. La fábrica nos da las implementaciones correctas
        IProductoDAO productoDAO = factory.getProductoDAO();
        IClienteDAO clienteDAO = factory.getClienteDAO();
        IVentaDAO ventaDAO = factory.getVentaDAO();

        // 3. Inyectamos los DAOs en los Servicios
        ProductoService productoService = new ProductoService(productoDAO);
        ClienteService clienteService = new ClienteService(clienteDAO, ventaDAO);
        VentaService ventaService = new VentaService(ventaDAO, productoDAO, clienteDAO);

        // 4. Iniciar la Interfaz de Usuario
        MenuConsola menu = new MenuConsola(productoService, clienteService, ventaService);
        menu.iniciar();
    }
}
