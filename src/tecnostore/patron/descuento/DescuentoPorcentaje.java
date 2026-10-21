package tecnostore.patron.descuento;

/**
 * Estrategia que descuenta un porcentaje del monto.
 * Ej.: 10% sobre 1000 => 900.
 */
public class DescuentoPorcentaje implements EstrategiaDescuento {

    private final double porcentaje;

    public DescuentoPorcentaje(double porcentaje) {
        if (porcentaje < 0 || porcentaje > 100) {
            throw new IllegalArgumentException(
                "El porcentaje de descuento debe estar entre 0 y 100.");
        }
        this.porcentaje = porcentaje;
    }

    @Override
    public double aplicar(double monto) {
        return monto * (1 - porcentaje / 100);
    }

    @Override
    public String descripcion() {
        return "Descuento del " + porcentaje + "%";
    }
}
