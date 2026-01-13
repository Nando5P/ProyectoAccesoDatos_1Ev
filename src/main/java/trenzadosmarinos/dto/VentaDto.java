package trenzadosmarinos.dto;

import java.time.LocalDateTime;
import java.util.List;

public record VentaDto(
        Long id,
        LocalDateTime fecha,
        Long clienteId,
        String nombreCliente,
        Double total,
        List<DetalleVentaDto> detalles
) {}