package tecnostore.patron.descuento;

/**
 * Contexto del patrón Strategy: mantiene una referencia a la estrategia
 * vigente y delega en ella el cálculo del descuento.
 *
 * Permite cambiar la estrategia en tiempo de ejecución.
 */
public class ContextoDescuento {

    private EstrategiaDescuento estrategia;

    public ContextoDescuento() {
        // Por defecto, sin descuento.
        this.estrategia = new DescuentoNinguno();
    }

    public ContextoDescuento(EstrategiaDescuento estrategia) {
        setEstrategia(estrategia);
    }

    public EstrategiaDescuento getEstrategia() {
        return estrategia;
    }

    public void setEstrategia(EstrategiaDescuento estrategia) {
        if (estrategia == null) {
            throw new IllegalArgumentException("La estrategia de descuento no puede ser nula.");
        }
        this.estrategia = estrategia;
    }

    /**
     * Aplica la estrategia vigente al monto recibido.
     */
    public double aplicarDescuento(double monto) {
        return estrategia.aplicar(monto);
    }

    public String descripcion() {
        return estrategia.descripcion();
    }
}
