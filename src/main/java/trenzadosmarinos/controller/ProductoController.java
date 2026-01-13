package trenzadosmarinos.controller;

import trenzadosmarinos.dto.ErrorResponse;
import trenzadosmarinos.dto.ProductoDto;
import trenzadosmarinos.model.Producto;
import trenzadosmarinos.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<ProductoDto> findAll() {
        return productoService.listarTodos().stream()
                .map(p -> new ProductoDto(p.getId(), p.getNombre(), p.getPrecio(), p.getStock()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Producto p = productoService.obtenerPorId(id);
        if (p == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Producto no encontrado"));

        return ResponseEntity.ok(new ProductoDto(p.getId(), p.getNombre(), p.getPrecio(), p.getStock()));
    }

    @GetMapping("/search")
    public List<ProductoDto> findByName(@RequestParam String nombre) {
        return productoService.buscarPorNombre(nombre).stream()
                .map(p -> new ProductoDto(p.getId(), p.getNombre(), p.getPrecio(), p.getStock()))
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoDto create(@RequestBody ProductoDto dto) {
        Producto p = new Producto(null, dto.nombre(), dto.precio(), dto.stock());
        Producto guardado = productoService.guardar(p);
        return new ProductoDto(guardado.getId(), guardado.getNombre(), guardado.getPrecio(), guardado.getStock());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (productoService.obtenerPorId(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Producto no encontrado"));
        }
        productoService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
