package trenzadosmarinos.repository;

import trenzadosmarinos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Método personalizado para buscar por nombre (Spring lo implementa solo)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
