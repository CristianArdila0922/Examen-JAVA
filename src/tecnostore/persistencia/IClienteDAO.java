package tecnostore.persistencia;

import tecnostore.modelo.Cliente;

import java.util.List;
import java.util.Optional;

/**
 * Interface de acceso a datos para clientes.
 */
public interface IClienteDAO {

    Cliente guardar(Cliente cliente) throws Exception;

    boolean actualizar(Cliente cliente) throws Exception;

    boolean eliminar(int id) throws Exception;

    Optional<Cliente> buscarPorId(int id) throws Exception;

    Optional<Cliente> buscarPorIdentificacion(String identificacion) throws Exception;

    List<Cliente> listarTodos() throws Exception;
}
