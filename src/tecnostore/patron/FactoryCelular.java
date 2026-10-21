package tecnostore.patron;

import tecnostore.modelo.CategoriaGama;
import tecnostore.modelo.Celular;

/**
 * Patrón Factory Method (versión simple Factory).
 * Centraliza la creación de objetos Celular con validación y,
 * opcionalmente, valores por defecto según la gama.
 *
 * Beneficio: el resto del sistema no conoce los detalles de construcción
 * de un Celular; si cambian las reglas de creación, solo se modifica aquí.
 */
public final class FactoryCelular {

    private FactoryCelular() {
    }

    /**
     * Crea un celular completo con todos sus datos.
     * Lanza IllegalArgumentException si algún dato es inválido.
     */
    public static Celular crear(int id, String marca, String modelo, double precio,
                                int stock, String sistemaOperativo, String gamaTexto) {
        CategoriaGama gama = CategoriaGama.desdeTexto(gamaTexto);
        return new Celular(id, marca, modelo, precio, stock, sistemaOperativo, gama);
    }

    /**
     * Crea un celular aplicando rangos de precio sugeridos según la gama.
     * Útil para registrar productos de forma rápida manteniendo coherencia.
     */
    public static Celular crearPorGama(int id, String marca, String modelo,
                                       int stock, String sistemaOperativo,
                                       String gamaTexto) {
        CategoriaGama gama = CategoriaGama.desdeTexto(gamaTexto);
        double precioReferencia = switch (gama) {
            case ALTA -> 1500.0;
            case MEDIA -> 800.0;
            case BAJA -> 300.0;
        };
        return new Celular(id, marca, modelo, precioReferencia, stock, sistemaOperativo, gama);
    }
}
