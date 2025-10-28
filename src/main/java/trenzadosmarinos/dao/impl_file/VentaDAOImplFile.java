package trenzadosmarinos.dao.impl_file;

import trenzadosmarinos.dao.IVentaDAO;
import trenzadosmarinos.model.DetalleVenta;
import trenzadosmarinos.model.Venta;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VentaDAOImplFile implements IVentaDAO {

    private static final String VENTA_FILE = "src/main/resources/ventas.csv";
    private static final String DETALLE_FILE = "src/main/resources/detalle_ventas.csv";

    public VentaDAOImplFile() {
        initFile(VENTA_FILE);
        initFile(DETALLE_FILE);
    }

    private void initFile(String fileName) {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Error al inicializar el fichero " + fileName + ": " + e.getMessage());
        }
    }

    private List<String> leerLineas(String fileName) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo " + fileName + ": " + e.getMessage());
        }
        return lines;
    }

    private int getSiguienteVentaId() {
        return leerVentas().stream().mapToInt(Venta::getId).max().orElse(0) + 1;
    }

    private int getSiguienteDetalleId() {
        return leerDetalles().stream().mapToInt(DetalleVenta::getId).max().orElse(0) + 1;
    }

    private List<Venta> leerVentas() {
        List<Venta> ventas = new ArrayList<>();
        for (String line : leerLineas(VENTA_FILE)) {
            ventas.add(Venta.fromCsv(line));
        }
        return ventas;
    }

    private List<DetalleVenta> leerDetalles() {
        List<DetalleVenta> detalles = new ArrayList<>();
        for (String line : leerLineas(DETALLE_FILE)) {
            detalles.add(DetalleVenta.fromCsv(line));
        }
        return detalles;
    }


    @Override
    public void agregar(Venta venta) {
        // 1. Asignar nuevos IDs
        int nextVentaId = (venta.getId() == 0) ? getSiguienteVentaId() : venta.getId();
        int nextDetalleId = getSiguienteDetalleId();

        venta.setId(nextVentaId);

        // 2. Guardar la cabecera (Venta)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(VENTA_FILE, true))) { // true = append
            writer.write(venta.toCsv());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error escribiendo en " + VENTA_FILE + ": " + e.getMessage());
        }

        // 3. Guardar los detalles
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DETALLE_FILE, true))) { // true = append
            for (DetalleVenta detalle : venta.getDetalles()) {
                if (detalle.getId() == 0) {
                    detalle.setId(nextDetalleId++); // Asigna y autoincrementa
                }
                detalle.setIdVenta(nextVentaId); // Vincula al padre
                writer.write(detalle.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error escribiendo en " + DETALLE_FILE + ": " + e.getMessage());
        }
    }

    @Override
    public Venta obtenerPorId(int id) {
        Venta venta = leerVentas().stream()
                .filter(v -> v.getId() == id)
                .findFirst()
                .orElse(null);

        if (venta != null) {
            List<DetalleVenta> detalles = leerDetalles().stream()
                    .filter(d -> d.getIdVenta() == id)
                    .collect(Collectors.toList());
            venta.setDetalles(detalles);
        }
        return venta;
    }

    @Override
    public List<Venta> obtenerTodos() {
        List<Venta> ventas = leerVentas();
        List<DetalleVenta> todosLosDetalles = leerDetalles();

        for (Venta v : ventas) {
            v.setDetalles(
                    todosLosDetalles.stream()
                            .filter(d -> d.getIdVenta() == v.getId())
                            .collect(Collectors.toList())
            );
        }
        return ventas;
    }

    @Override
    public List<Venta> obtenerPorIdCliente(int idCliente) {
        return obtenerTodos().stream()
                .filter(v -> v.getIdCliente() == idCliente)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarTodos() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(VENTA_FILE, false))) {
            writer.write("");
        } catch (IOException e) {
            System.err.println("Error al borrar " + VENTA_FILE);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DETALLE_FILE, false))) {
            writer.write("");
        } catch (IOException e) {
            System.err.println("Error al borrar " + DETALLE_FILE);
        }
    }
}