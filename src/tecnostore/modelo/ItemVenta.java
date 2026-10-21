package tecnostore.modelo;

import tecnostore.utilidades.Validador;

/**
 * Línea de detalle de una venta: relación entre una Venta y un Celular.
 * Cada item guarda la cantidad vendida y el subtotal (precio * cantidad).
 * Composición: un ItemVenta no tiene sentido sin su Celular.
 */
public class ItemVenta {

    private int id;
    private Celular celular;
    private int cantidad;
    private double subtotal;

    public ItemVenta() {
    }

    public ItemVenta(int id, Celular celular, int cantidad) {
        this.id = id;
        setCelular(celular);
        setCantidad(cantidad);
        recalcularSubtotal();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Celular getCelular() {
        return celular;
    }

    public void setCelular(Celular celular) {
        if (celular == null) {
            throw new IllegalArgumentException("El item de venta debe referenciar un celular.");
        }
        this.celular = celular;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (!Validador.esPositivo(cantidad)) {
            throw new IllegalArgumentException("La cantidad del item debe ser mayor a cero.");
        }
        this.cantidad = cantidad;
        recalcularSubtotal();
    }

    public double getSubtotal() {
        return subtotal;
    }

    /**
     * El subtotal se recalcula a partir del precio y la cantidad,
     * evitando que se guarde un valor inconsistente.
     */
    public void recalcularSubtotal() {
        if (celular != null && cantidad > 0) {
            this.subtotal = celular.getPrecio() * cantidad;
        }
    }

    @Override
    public String toString() {
        return String.format("ItemVenta{id=%d, celular=%s %s, cantidad=%d, subtotal=%.2f}",
            id, celular.getMarca(), celular.getModelo(), cantidad, subtotal);
    }
}
