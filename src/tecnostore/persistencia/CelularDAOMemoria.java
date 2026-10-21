package tecnostore.persistencia;

import tecnostore.modelo.Celular;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementación de ICelularDAO que guarda todo en memoria.
 * Permite ejecutar la aplicación sin MySQL (modo demo), manteniendo
 * la misma interface que la versión JDBC.
 */
public class CelularDAOMemoria implements ICelularDAO {

    private final ConcurrentHashMap<Integer, Celular> tabla = new ConcurrentHashMap<>();
    private final AtomicInteger secuenciaId = new AtomicInteger(0);

    @Override
    public Celular guardar(Celular celular) {
        if (celular.getId() <= 0) {
            celular.setId(secuenciaId.incrementAndGet());
        }
        tabla.put(celular.getId(), celular);
        return celular;
    }

    @Override
    public boolean actualizar(Celular celular) {
        if (!tabla.containsKey(celular.getId())) {
            return false;
        }
        tabla.put(celular.getId(), celular);
        return true;
    }

    @Override
    public boolean eliminar(int id) {
        return tabla.remove(id) != null;
    }

    @Override
    public Optional<Celular> buscarPorId(int id) {
        return Optional.ofNullable(tabla.get(id));
    }

    @Override
    public List<Celular> listarTodos() {
        return new ArrayList<>(tabla.values());
    }

    @Override
    public List<Celular> buscarPorMarca(String marca) {
        return tabla.values().stream()
            .filter(c -> c.getMarca().toLowerCase().contains(marca.toLowerCase()))
            .toList();
    }

    @Override
    public boolean actualizarStock(int id, int nuevoStock) {
        Celular celular = tabla.get(id);
        if (celular == null) {
            return false;
        }
        celular.setStock(nuevoStock);
        return true;
    }
}
