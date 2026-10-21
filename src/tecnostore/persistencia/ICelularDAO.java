package tecnostore.persistencia;

import tecnostore.modelo.Celular;

import java.util.List;
import java.util.Optional;

/**
 * Interface de acceso a datos para celulares.
 * Permite múltiples implementaciones (MySQL, memoria, etc.) y respeta
 * el principio de inversión de dependencias (los servicios dependen
 * de la abstracción, no de una implementación concreta).
 */
public interface ICelularDAO {

    Celular guardar(Celular celular) throws Exception;

    boolean actualizar(Celular celular) throws Exception;

    boolean eliminar(int id) throws Exception;

    Optional<Celular> buscarPorId(int id) throws Exception;

    List<Celular> listarTodos() throws Exception;

    List<Celular> buscarPorMarca(String marca) throws Exception;

    /** Actualiza únicamente el stock de un celular. */
    boolean actualizarStock(int id, int nuevoStock) throws Exception;
}
