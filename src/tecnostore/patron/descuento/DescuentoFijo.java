package tecnostore.patron.descuento;

/**
 * Estrategia que descuenta un monto fijo del total.
 * Ej.: $100 sobre $1000 => $900.
 */
public class DescuentoFijo implements EstrategiaDescuento {

    private final double monto;

    public DescuentoFijo(double monto) {
        if (monto < 0) {
            throw new IllegalArgumentException(
                "El monto de descuento no puede ser negativo.");
        }
        this.monto = monto;
    }

    @Override
    public double aplicar(double montoBase) {
        double resultado = montoBase - monto;
        return Math.max(resultado, 0);
    }

    @Override
    public String descripcion() {
        return "Descuento fijo de $" + monto;
    }
}
