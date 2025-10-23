package trenzadosmarinos.model;

public class Producto {
    private int id;
    private String nombre;
    private double precio;
    private int stock;

    public Producto(int id, String nombre, double precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    // --- Setters ---
    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "Producto [ID=" + id + ", Nombre='" + nombre + "', Precio=" + precio + ", Stock=" + stock + "]";
    }

    // --- Métodos para Ficheros CSV ---
    public String toCsv() {
        return id + "," + nombre + "," + precio + "," + stock;
    }

    public static Producto fromCsv(String csvLine) {
        String[] data = csvLine.split(",");
        return new Producto(
                Integer.parseInt(data[0]),
                data[1],
                Double.parseDouble(data[2]),
                Integer.parseInt(data[3])
        );
    }
}
