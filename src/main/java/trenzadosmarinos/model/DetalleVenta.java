package trenzadosmarinos.model;

public class DetalleVenta {
    private int id;
    private int idVenta;
    private int idProducto;
    private int cantidad;
    private double precioUnitario; // Guardamos el precio al momento de la venta

    public DetalleVenta(int id, int idVenta, int idProducto, int cantidad, double precioUnitario) {
        this.id = id;
        this.idVenta = idVenta;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // --- Getters y Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    @Override
    public String toString() {
        return "Detalle [ID=" + id + ", ID Venta=" + idVenta + ", ID Producto=" + idProducto +
                ", Cant=" + cantidad + ", P.U=" + precioUnitario + "]";
    }

    public String toCsv() {
        return id + "," + idVenta + "," + idProducto + "," + cantidad + "," + precioUnitario;
    }

    public static DetalleVenta fromCsv(String csvLine) {
        String[] data = csvLine.split(",");
        return new DetalleVenta(
                Integer.parseInt(data[0]),
                Integer.parseInt(data[1]),
                Integer.parseInt(data[2]),
                Integer.parseInt(data[3]),
                Double.parseDouble(data[4])
        );
    }
}
