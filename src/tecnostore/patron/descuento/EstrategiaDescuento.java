package tecnostore.patron.descuento;

/**
 * Patrón Strategy: interface común para todas las estrategias de descuento.
 * Cada implementación define cómo aplicar un descuento a un monto.
 *
 * Permite añadir nuevas formas de descuento sin modificar el código existente
 * (principio Open/Closed).
 */
public interface EstrategiaDescuento {

    /**
     * Aplica el descuento al monto recibido y devuelve el monto final.
     */
    double aplicar(double monto);

    /**
     * Descripción legible de la estrategia (para reportes).
     */
    String descripcion();
}
