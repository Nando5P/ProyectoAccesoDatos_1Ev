package trenzadosmarinos.dao.impl_file;

import trenzadosmarinos.dao.IClienteDAO;
import trenzadosmarinos.model.Cliente;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImplFile implements IClienteDAO {

    private static final String FILE_NAME = "src/main/resources/clientes.csv";

    public ClienteDAOImplFile() {
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

    private List<Cliente> leerFichero() {
        List<Cliente> lista = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lista.add(Cliente.fromCsv(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo " + FILE_NAME);
        }
        return lista;
    }

    private void escribirFichero(List<Cliente> lista) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            for (Cliente c : lista) {
                writer.write(c.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error escribiendo " + FILE_NAME);
        }
    }

    private int getSiguienteId(List<Cliente> lista) {
        return lista.stream().mapToInt(Cliente::getId).max().orElse(0) + 1;
    }

    @Override
    public void agregar(Cliente cliente) {
        List<Cliente> lista = leerFichero();
        if (cliente.getId() == 0) {
            cliente.setId(getSiguienteId(lista));
        }
        lista.add(cliente);
        escribirFichero(lista);
    }

    @Override
    public void actualizar(Cliente cliente) {
        List<Cliente> lista = leerFichero();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == cliente.getId()) {
                lista.set(i, cliente);
                break;
            }
        }
        escribirFichero(lista);
    }

    @Override
    public void eliminar(int id) {
        List<Cliente> lista = leerFichero();
        lista.removeIf(c -> c.getId() == id);
        escribirFichero(lista);
    }

    @Override
    public Cliente obtenerPorId(int id) {
        return leerFichero().stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Cliente> obtenerTodos() {
        return leerFichero();
    }

    @Override
    public void eliminarTodos() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            writer.write("");
        } catch (IOException e) {
            System.err.println("Error al borrar " + FILE_NAME);
        }
    }

    @Override
    public void agregarLote(List<Cliente> clientes) {
        escribirFichero(clientes);
    }

    @Override
    public void reiniciarAutoIncrement() {
        // No aplica para ficheros CSV.
    }
}
