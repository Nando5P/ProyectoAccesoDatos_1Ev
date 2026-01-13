package trenzadosmarinos.dto;

public record DetalleVentaDto(
        Long productoId,
        String nombreProducto,
        Integer cantidad,
        Double precioUnitario
) {}
