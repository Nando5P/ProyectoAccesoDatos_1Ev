package trenzadosmarinos.model;

public class Cliente {
    private int id;
    private String nombre;
    private String direccion;

    public Cliente(int id, String nombre, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
    }

    // --- Getters y Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return "Cliente [ID=" + id + ", Nombre='" + nombre + "', Dirección='" + direccion + "']";
    }

    public String toCsv() {
        return id + "," + nombre + "," + direccion;
    }

    public static Cliente fromCsv(String csvLine) {
        String[] data = csvLine.split(",");
        return new Cliente(Integer.parseInt(data[0]), data[1], data[2]);
    }
}
