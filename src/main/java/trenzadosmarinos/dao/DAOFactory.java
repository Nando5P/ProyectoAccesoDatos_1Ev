package trenzadosmarinos.dao;

import trenzadosmarinos.dao.impl_file.ClienteDAOImplFile;
import trenzadosmarinos.dao.impl_file.ProductoDAOImplFile;
import trenzadosmarinos.dao.impl_file.VentaDAOImplFile;
import trenzadosmarinos.dao.impl_jdbc.ClienteDAOImplJDBC;
import trenzadosmarinos.dao.impl_jdbc.ProductoDAOImplJDBC;
import trenzadosmarinos.dao.impl_jdbc.VentaDAOImplJDBC;

public class DAOFactory {

    private StorageType storageType;

    public DAOFactory(StorageType storageType) {
        this.storageType = storageType;
    }

    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }

    public StorageType getStorageType() {
        return this.storageType;
    }

    public IProductoDAO getProductoDAO() {
        switch (storageType) {
            case FILE:
                return new ProductoDAOImplFile();
            case JDBC:
                return new ProductoDAOImplJDBC();
            default:
                throw new IllegalArgumentException("Tipo de almacenamiento no soportado");
        }
    }

    public IClienteDAO getClienteDAO() {
        switch (storageType) {
            case FILE:
                return new ClienteDAOImplFile();
            case JDBC:
                return new ClienteDAOImplJDBC();
            default:
                throw new IllegalArgumentException("Tipo de almacenamiento no soportado");
        }
    }

    public IVentaDAO getVentaDAO() {
        switch (storageType) {
            case FILE:
                return new VentaDAOImplFile();
            case JDBC:
                return new VentaDAOImplJDBC();
            default:
                throw new IllegalArgumentException("Tipo de almacenamiento no soportado");
        }
    }
}
