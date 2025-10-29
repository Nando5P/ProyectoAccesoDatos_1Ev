package trenzadosmarinos.dao.impl_file;

import trenzadosmarinos.dao.IProductoDAO;
import trenzadosmarinos.model.Producto;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAOImplFile implements IProductoDAO {

    private static final String FILE_NAME = "src/main/resources/productos.csv";

    public ProductoDAOImplFile() {
        initFile();
    }

    private void initFile() {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            System.err.println("Error al inicializar " + FILE_NAME);
        }
    }

    private List<Producto> leerFichero() {
        List<Producto> productos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    productos.add(Producto.fromCsv(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo " + FILE_NAME);
        }
        return productos;
    }

    private void escribirFichero(List<Producto> productos) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, false))) { // false = sobrescribir
            for (Producto p : productos) {
                writer.write(p.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error escribiendo " + FILE_NAME);
        }
    }

    private int getSiguienteId(List<Producto> productos) {
        return productos.stream()
                .mapToInt(Producto::getId)
                .max()
                .orElse(0) + 1;
    }

    @Override
    public void agregar(Producto producto) {
        List<Producto> productos = leerFichero();
        if (producto.getId() == 0) { // Solo asigna nuevo ID si es 0
            producto.setId(getSiguienteId(productos));
        }
        productos.add(producto);
        escribirFichero(productos);
    }

    @Override
    public void actualizar(Producto producto) {
        List<Producto> productos = leerFichero();
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getId() == producto.getId()) {
                productos.set(i, producto);
                break;
            }
        }
        escribirFichero(productos);
    }

    @Override
    public void eliminar(int id) {
        List<Producto> productos = leerFichero();
        productos.removeIf(p -> p.getId() == id);
        escribirFichero(productos);
    }

    @Override
    public Producto obtenerPorId(int id) {
        return leerFichero().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Producto> obtenerTodos() {
        return leerFichero();
    }

    @Override
    public void eliminarTodos() {
        // Borra el contenido del fichero
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            writer.write("");
        } catch (IOException e) {
            System.err.println("Error al borrar " + FILE_NAME);
        }
    }

    @Override
    public void agregarLote(List<Producto> productos) {
        escribirFichero(productos);
    }

    @Override
    public void reiniciarAutoIncrement() {
        // No aplica para ficheros CSV.
    }
}