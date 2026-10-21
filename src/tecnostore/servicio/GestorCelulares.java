package tecnostore.servicio;

import tecnostore.modelo.Celular;
import tecnostore.persistencia.ICelularDAO;

import java.util.List;
import java.util.Optional;

/**
 * Lógica de negocio para celulares.
 * Depende de la abstracción ICelularDAO (inversión de dependencias),
 * por lo que funciona igual con la implementación MySQL o la de memoria.
 */
public class GestorCelulares {

    private final ICelularDAO celularDAO;

    public GestorCelulares(ICelularDAO celularDAO) {
        if (celularDAO == null) {
            throw new IllegalArgumentException("El DAO de celulares es obligatorio.");
        }
        this.celularDAO = celularDAO;
    }

    /** Registra un celular nuevo. */
    public Celular registrar(Celular celular) throws Exception {
        if (celular == null) {
            throw new IllegalArgumentException("El celular no puede ser nulo.");
        }
        return celularDAO.guardar(celular);
    }

    /** Actualiza los datos de un celular existente. */
    public boolean actualizar(Celular celular) throws Exception {
        return celularDAO.actualizar(celular);
    }

    /** Elimina un celular por id. */
    public boolean eliminar(int id) throws Exception {
        return celularDAO.eliminar(id);
    }

    /** Lista todos los celulares del catálogo. */
    public List<Celular> listar() throws Exception {
        return celularDAO.listarTodos();
    }

    /** Busca un celular por id. */
    public Optional<Celular> buscarPorId(int id) throws Exception {
        return celularDAO.buscarPorId(id);
    }

    /** Busca celulares por marca (búsqueda parcial). */
    public List<Celular> buscarPorMarca(String marca) throws Exception {
        return celularDAO.buscarPorMarca(marca);
    }
}
