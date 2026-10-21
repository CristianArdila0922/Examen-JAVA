package tecnostore.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entidad Venta. Agrega varios ItemVenta y a un Cliente.
 * El total se calcula sumando subtotales y aplicando IVA (19%).
 */
public class Venta {

    /** IVA aplicado a las ventas: 19%. */
    public static final double IVA = 0.19;

    private int id;
    private Cliente cliente;
    private LocalDateTime fecha;
    private final List<ItemVenta> items;
    private double total;
    private String empleado;

    public Venta() {
        this.items = new ArrayList<>();
        this.fecha = LocalDateTime.now();
    }

    public Venta(int id, Cliente cliente) {
        this();
        this.id = id;
        setCliente(cliente);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("La venta debe tener un cliente asociado.");
        }
        this.cliente = cliente;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    /**
     * Devuelve una vista no modificable de los items para preservar el encapsulamiento.
     */
    public List<ItemVenta> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void agregarItem(ItemVenta item) {
        if (item == null) {
            throw new IllegalArgumentException("El item de venta no puede ser nulo.");
        }
        items.add(item);
        recalcularTotal();
    }

    public double getSubtotal() {
        return items.stream().mapToDouble(ItemVenta::getSubtotal).sum();
    }

    public double getIva() {
        return getSubtotal() * IVA;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    /**
     * Empleado que realizó la venta. Es obligatorio y se valida que no sea vacío.
     */
    public String getEmpleado() {
        return empleado;
    }

    public void setEmpleado(String empleado) {
        if (!tecnostore.utilidades.Validador.noVacio(empleado)) {
            throw new IllegalArgumentException(
                "El empleado que realiza la venta es obligatorio.");
        }
        this.empleado = empleado.trim();
    }

    /**
     * Recalcula el total = subtotal + IVA.
     */
    public void recalcularTotal() {
        this.total = getSubtotal() * (1 + IVA);
    }

    public String getFechaFormateada() {
        return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    @Override
    public String toString() {
        return String.format("Venta{id=%d, cliente=%s, empleado=%s, fecha=%s, items=%d, total=%.2f}",
            id, cliente.getNombre(), empleado, getFechaFormateada(), items.size(), total);
    }
}
