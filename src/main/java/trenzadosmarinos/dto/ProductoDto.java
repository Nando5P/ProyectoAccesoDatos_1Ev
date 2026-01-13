package trenzadosmarinos.dto;

public record ProductoDto(
        Long id,
        String nombre,
        Double precio,
        Integer stock
) {}
