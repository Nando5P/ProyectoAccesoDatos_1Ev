package trenzadosmarinos.controller;

import trenzadosmarinos.dto.ClienteDto;
import trenzadosmarinos.dto.ErrorResponse;
import trenzadosmarinos.model.Cliente;
import trenzadosmarinos.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<ClienteDto> findAll() {
        return clienteService.listarTodos().stream()
                .map(c -> new ClienteDto(c.getId(), c.getNombre(), c.getDireccion()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Cliente c = clienteService.obtenerPorId(id);
        if (c == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Cliente no encontrado"));
        }
        return ResponseEntity.ok(new ClienteDto(c.getId(), c.getNombre(), c.getDireccion()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteDto create(@RequestBody ClienteDto dto) {
        Cliente c = new Cliente(null, dto.nombre(), dto.direccion());
        Cliente guardado = clienteService.guardar(c);
        return new ClienteDto(guardado.getId(), guardado.getNombre(), guardado.getDireccion());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ClienteDto dto) {
        Cliente existente = clienteService.obtenerPorId(id);
        if (existente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Cliente no encontrado"));
        }
        existente.setNombre(dto.nombre());
        existente.setDireccion(dto.direccion());
        Cliente actualizado = clienteService.guardar(existente);
        return ResponseEntity.ok(new ClienteDto(actualizado.getId(), actualizado.getNombre(), actualizado.getDireccion()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (clienteService.obtenerPorId(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Cliente no encontrado"));
        }
        clienteService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
