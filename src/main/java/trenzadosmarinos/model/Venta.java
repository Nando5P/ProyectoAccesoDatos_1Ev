package trenzadosmarinos.model;

import java.time.LocalDate;
import java.util.List;

public class Venta {
    private int id;
    private LocalDate fecha;
    private int idCliente;
    private double total;
    private List<DetalleVenta> detalles; // Lista de productos vendidos

    public Venta(int id, LocalDate fecha, int idCliente, double total, List<DetalleVenta> detalles) {
        this.id = id;
        this.fecha = fecha;
        this.idCliente = idCliente;
        this.total = total;
        this.detalles = detalles;
    }

    // --- Getters y Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }

    @Override
    public String toString() {
        return "Venta [ID=" + id + ", Fecha=" + fecha + ", ID Cliente=" + idCliente + ", Total=" + total + "]";
    }

    // --- Métodos para Ficheros CSV (Solo la cabecera) ---
    public String toCsv() {
        return id + "," + fecha.toString() + "," + idCliente + "," + total;
    }

    public static Venta fromCsv(String csvLine) {
        String[] data = csvLine.split(",");
        // Los detalles se cargan por separado
        return new Venta(
                Integer.parseInt(data[0]),
                LocalDate.parse(data[1]),
                Integer.parseInt(data[2]),
                Double.parseDouble(data[3]),
                null // Se cargan después
        );
    }
}
