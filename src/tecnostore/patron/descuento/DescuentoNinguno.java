package tecnostore.patron.descuento;

/**
 * Estrategia que no aplica ningún descuento.
 */
public class DescuentoNinguno implements EstrategiaDescuento {

    @Override
    public double aplicar(double monto) {
        return monto;
    }

    @Override
    public String descripcion() {
        return "Sin descuento";
    }
}
