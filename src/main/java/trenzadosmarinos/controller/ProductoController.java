package trenzadosmarinos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Producto", description = "Operaciones relacionadas con la gestión de productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @Operation(summary = "Listar todos los productos", description = "Recupera una lista completa de todos los productos registrados en la base de datos.")
    @GetMapping
    public List<ProductoDto> findAll() {
        return productoService.listarTodos().stream()
                .map(p -> new ProductoDto(p.getId(), p.getNombre(), p.getPrecio(), p.getStock()))
                .collect(Collectors.toList());
    }

    @Operation(summary = "Obtener producto por ID", description = "Recupera un producto específico por su ID.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Producto p = productoService.obtenerPorId(id);
        if (p == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Producto no encontrado"));

        return ResponseEntity.ok(new ProductoDto(p.getId(), p.getNombre(), p.getPrecio(), p.getStock()));
    }

    @Operation(summary = "Buscar productos por nombre", description = "Realiza una búsqueda de productos por nombre")
    @GetMapping("/search")
    public List<ProductoDto> findByName(@RequestParam String nombre) {
        return productoService.buscarPorNombre(nombre).stream()
                .map(p -> new ProductoDto(p.getId(), p.getNombre(), p.getPrecio(), p.getStock()))
                .collect(Collectors.toList());
    }

    @Operation(summary = "Crear un nuevo producto", description = "Crea un nuevo producto en la base de datos.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoDto create(@RequestBody ProductoDto dto) {
        Producto p = new Producto(null, dto.nombre(), dto.precio(), dto.stock());
        Producto guardado = productoService.guardar(p);
        return new ProductoDto(guardado.getId(), guardado.getNombre(), guardado.getPrecio(), guardado.getStock());
    }

    @Operation(summary = "Actualizar producto por ID", description = "Actualiza un producto existente por su ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (productoService.obtenerPorId(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Producto no encontrado"));
        }
        productoService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
