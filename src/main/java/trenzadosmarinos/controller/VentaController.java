package trenzadosmarinos.controller;

import trenzadosmarinos.dto.DetalleVentaDto;
import trenzadosmarinos.dto.VentaDto;
import trenzadosmarinos.model.Cliente;
import trenzadosmarinos.model.DetalleVenta;
import trenzadosmarinos.model.Producto;
import trenzadosmarinos.model.Venta;
import trenzadosmarinos.service.VentaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VentaDto create(@RequestBody VentaDto dto) {
        Venta venta = new Venta();
        Cliente cliente = new Cliente();
        cliente.setId(dto.clienteId());
        venta.setCliente(cliente);

        List<DetalleVenta> detalles = dto.detalles().stream().map(d -> {
            DetalleVenta dv = new DetalleVenta();
            Producto p = new Producto();
            p.setId(d.productoId());
            dv.setProducto(p);
            dv.setCantidad(d.cantidad());
            return dv;
        }).collect(Collectors.toList());

        venta.setDetalles(detalles);
        Venta guardada = ventaService.registrarVenta(venta);

        return mapToDto(guardada);
    }

    @GetMapping
    public List<VentaDto> findAll() {
        return ventaService.listarVentas().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private VentaDto mapToDto(Venta v) {
        List<DetalleVentaDto> detallesDto = v.getDetalles().stream()
                .map(d -> new DetalleVentaDto(d.getProducto().getId(), d.getProducto().getNombre(), d.getCantidad(), d.getPrecioUnitario()))
                .collect(Collectors.toList());

        return new VentaDto(v.getId(), v.getFecha(), v.getCliente().getId(), v.getCliente().getNombre(), v.getTotal(), detallesDto);
    }
}
