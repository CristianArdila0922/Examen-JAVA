package tecnostore.persistencia;

import tecnostore.modelo.Cliente;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementación de IClienteDAO que guarda todo en memoria (modo demo).
 */
public class ClienteDAOMemoria implements IClienteDAO {

    private final ConcurrentHashMap<Integer, Cliente> tabla = new ConcurrentHashMap<>();
    private final AtomicInteger secuenciaId = new AtomicInteger(0);

    @Override
    public Cliente guardar(Cliente cliente) {
        if (cliente.getId() <= 0) {
            cliente.setId(secuenciaId.incrementAndGet());
        }
        tabla.put(cliente.getId(), cliente);
        return cliente;
    }

    @Override
    public boolean actualizar(Cliente cliente) {
        if (!tabla.containsKey(cliente.getId())) {
            return false;
        }
        tabla.put(cliente.getId(), cliente);
        return true;
    }

    @Override
    public boolean eliminar(int id) {
        return tabla.remove(id) != null;
    }

    @Override
    public Optional<Cliente> buscarPorId(int id) {
        return Optional.ofNullable(tabla.get(id));
    }

    @Override
    public Optional<Cliente> buscarPorIdentificacion(String identificacion) {
        return tabla.values().stream()
            .filter(c -> c.getIdentificacion().equalsIgnoreCase(identificacion))
            .findFirst();
    }

    @Override
    public List<Cliente> listarTodos() {
        return new ArrayList<>(tabla.values());
    }
}
