package tecnostore.servicio;

import tecnostore.modelo.Cliente;
import tecnostore.persistencia.IClienteDAO;
import tecnostore.utilidades.Validador;

import java.util.List;
import java.util.Optional;

/**
 * Lógica de negocio para clientes.
 * Valida formato de correo y unicidad de la identificación.
 */
public class GestorClientes {

    private final IClienteDAO clienteDAO;

    public GestorClientes(IClienteDAO clienteDAO) {
        if (clienteDAO == null) {
            throw new IllegalArgumentException("El DAO de clientes es obligatorio.");
        }
        this.clienteDAO = clienteDAO;
    }

    /**
     * Registra un cliente validando correo y que la identificación sea única.
     */
    public Cliente registrar(Cliente cliente) throws Exception {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo.");
        }
        // Validación de formato (el propio modelo ya valida el correo).
        if (!Validador.esCorreoValido(cliente.getCorreo())) {
            throw new IllegalArgumentException("El correo del cliente no es válido.");
        }
        // Unicidad de la identificación.
        if (clienteDAO.buscarPorIdentificacion(cliente.getIdentificacion()).isPresent()) {
            throw new IllegalArgumentException(
                "Ya existe un cliente con la identificación '" + cliente.getIdentificacion() + "'.");
        }
        return clienteDAO.guardar(cliente);
    }

    public boolean actualizar(Cliente cliente) throws Exception {
        return clienteDAO.actualizar(cliente);
    }

    public boolean eliminar(int id) throws Exception {
        return clienteDAO.eliminar(id);
    }

    public List<Cliente> listar() throws Exception {
        return clienteDAO.listarTodos();
    }

    public Optional<Cliente> buscarPorId(int id) throws Exception {
        return clienteDAO.buscarPorId(id);
    }

    public Optional<Cliente> buscarPorIdentificacion(String identificacion) throws Exception {
        return clienteDAO.buscarPorIdentificacion(identificacion);
    }
}
