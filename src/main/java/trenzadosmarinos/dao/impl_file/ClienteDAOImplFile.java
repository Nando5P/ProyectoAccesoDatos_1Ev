package trenzadosmarinos.dao.impl_file;

import trenzadosmarinos.dao.IClienteDAO;
import trenzadosmarinos.model.Cliente;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImplFile implements IClienteDAO {

    private static final String FILE_NAME = "clientes.csv";

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
        List<Cliente> clientes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    clientes.add(Cliente.fromCsv(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo " + FILE_NAME);
        }
        return clientes;
    }

    private void escribirFichero(List<Cliente> clientes) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, false))) { // false = sobrescribir
            for (Cliente c : clientes) {
                writer.write(c.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error escribiendo " + FILE_NAME);
        }
    }

    private int getSiguienteId(List<Cliente> clientes) {
        return clientes.stream()
                .mapToInt(Cliente::getId)
                .max()
                .orElse(0) + 1;
    }

    @Override
    public void agregar(Cliente cliente) {
        List<Cliente> clientes = leerFichero();
        cliente.setId(getSiguienteId(clientes));
        clientes.add(cliente);
        escribirFichero(clientes);
    }

    @Override
    public void actualizar(Cliente cliente) {
        List<Cliente> clientes = leerFichero();
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getId() == cliente.getId()) {
                clientes.set(i, cliente);
                break;
            }
        }
        escribirFichero(clientes);
    }

    @Override
    public void eliminar(int id) {
        List<Cliente> clientes = leerFichero();
        clientes.removeIf(p -> p.getId() == id);
        escribirFichero(clientes);
    }

    @Override
    public Cliente obtenerPorId(int id) {
        return leerFichero().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Cliente> obtenerTodos() {
        return leerFichero();
    }
}
