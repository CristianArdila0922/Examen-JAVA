package tecnostore.servicio;

import tecnostore.modelo.Celular;
import tecnostore.modelo.Cliente;
import tecnostore.modelo.ItemVenta;
import tecnostore.modelo.ReporteVentas;
import tecnostore.modelo.Venta;
import tecnostore.patron.descuento.ContextoDescuento;
import tecnostore.persistencia.ICelularDAO;
import tecnostore.persistencia.IVentaDAO;

import java.util.List;
import java.util.Optional;

/**
 * Lógica de negocio para ventas.
 * Registra una venta seleccionando un cliente y uno o varios celulares,
 * calcula el total (incluyendo IVA del 19%), descuenta el stock y persiste.
 *
 * El descuento se aplica con el patrón Strategy (ContextoDescuento),
 * por lo que es intercambiable sin modificar esta clase.
 */
public class GestorVentas {

    private final IVentaDAO ventaDAO;
    private final ICelularDAO celularDAO;
    private ContextoDescuento contextoDescuento;

    public GestorVentas(IVentaDAO ventaDAO, ICelularDAO celularDAO) {
        if (ventaDAO == null || celularDAO == null) {
            throw new IllegalArgumentException("Los DAOs de venta y celular son obligatorios.");
        }
        this.ventaDAO = ventaDAO;
        this.celularDAO = celularDAO;
        this.contextoDescuento = new ContextoDescuento();
    }

    public void setContextoDescuento(ContextoDescuento contextoDescuento) {
        this.contextoDescuento = contextoDescuento;
    }

    public ContextoDescuento getContextoDescuento() {
        return contextoDescuento;
    }

    public List<Venta> listarVentas() throws Exception {
        return ventaDAO.listarTodos();
    }

    public List<Venta> ventasPorCliente(int idCliente) throws Exception {
        return ventaDAO.listarPorCliente(idCliente);
    }

    /**
     * Registra una venta a partir de un cliente y una lista de (celular, cantidad).
     *
     * @param cliente       cliente que compra.
     * @param solicitudes    pares (celular, cantidad) solicitados.
     * @return la venta persistida con su total calculado.
     */
    public Venta registrarVenta(Cliente cliente, List<SolicitudItem> solicitudes) throws Exception {
        // Sobrecarga por compatibilidad; el empleado queda como "No asignado".
        return registrarVenta(cliente, solicitudes, "No asignado");
    }

    /**
     * Registra una venta a partir de un cliente, una lista de (celular, cantidad)
     * y el empleado que la realiza.
     *
     * @param cliente       cliente que compra.
     * @param solicitudes    pares (celular, cantidad) solicitados.
     * @param empleado       nombre del empleado que registra la venta.
     * @return la venta persistida con su total calculado.
     */
    public Venta registrarVenta(Cliente cliente, List<SolicitudItem> solicitudes,
                                String empleado) throws Exception {
        if (cliente == null) {
            throw new IllegalArgumentException("La venta requiere un cliente.");
        }
        if (solicitudes == null || solicitudes.isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un celular.");
        }

        // Validar stock de cada solicitud antes de crear la venta (operación atómica).
        for (SolicitudItem s : solicitudes) {
            if (s.cantidad <= 0) {
                throw new IllegalArgumentException(
                    "La cantidad para " + s.celular.getModelo() + " debe ser mayor a cero.");
            }
            if (s.celular.getStock() < s.cantidad) {
                throw new IllegalStateException(
                    "Stock insuficiente para " + s.celular.getMarca() + " "
                        + s.celular.getModelo()
                        + ". Disponible: " + s.celular.getStock()
                        + ", solicitado: " + s.cantidad);
            }
        }

        // Construir la venta.
        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setEmpleado(empleado);

        for (SolicitudItem s : solicitudes) {
            ItemVenta item = new ItemVenta();
            item.setCelular(s.celular);
            item.setCantidad(s.cantidad);
            venta.agregarItem(item);

            // Actualizar el stock del celular.
            int nuevoStock = s.celular.getStock() - s.cantidad;
            s.celular.setStock(nuevoStock);
            celularDAO.actualizarStock(s.celular.getId(), nuevoStock);
        }

        // Aplicar descuento (Strategy) sobre el subtotal, luego sumar IVA.
        double subtotal = venta.getSubtotal();
        double subtotalConDescuento = contextoDescuento.aplicarDescuento(subtotal);
        double iva = subtotalConDescuento * Venta.IVA;
        double total = subtotalConDescuento + iva;
        venta.setTotal(total);

        return ventaDAO.guardar(venta);
    }

    /**
     * Recalcula el total mostrando el descuento que se aplicaría, sin registrar la venta.
     * Útil para que el usuario vea el precio antes de confirmar.
     */
    public double simularTotal(double subtotal) {
        return contextoDescuento.aplicarDescuento(subtotal) * (1 + Venta.IVA);
    }

    /**
     * Recarga un celular desde el DAO para mostrar datos frescos (stock actualizado).
     */
    public Optional<Celular> recargarCelular(int id) throws Exception {
        return celularDAO.buscarPorId(id);
    }

    /**
     * Genera el reporte de ventas filtrado por marca de celular.
     * Delega en el DAO (MySQL con JDBC o memoria) y puede lanzar
     * VentaNoEncontradaException si no hay ventas para la marca.
     */
    public List<ReporteVentas> reportePorMarca(String marca) throws Exception {
        return ventaDAO.reportePorMarca(marca);
    }

    /** Estructura auxiliar para representar una línea de la venta. */
    public static class SolicitudItem {
        public final Celular celular;
        public final int cantidad;

        public SolicitudItem(Celular celular, int cantidad) {
            this.celular = celular;
            this.cantidad = cantidad;
        }
    }
}
