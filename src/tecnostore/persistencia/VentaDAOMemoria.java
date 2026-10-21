package tecnostore.persistencia;

import tecnostore.excepciones.VentaNoEncontradaException;
import tecnostore.modelo.ItemVenta;
import tecnostore.modelo.ReporteVentas;
import tecnostore.modelo.Venta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Implementación de IVentaDAO que guarda todo en memoria (modo demo).
 */
public class VentaDAOMemoria implements IVentaDAO {

    private final Map<Integer, Venta> tabla = new ConcurrentHashMap<>();
    private final AtomicInteger secuenciaId = new AtomicInteger(0);

    @Override
    public Venta guardar(Venta venta) {
        if (venta.getId() <= 0) {
            venta.setId(secuenciaId.incrementAndGet());
        }
        tabla.put(venta.getId(), venta);
        return venta;
    }

    @Override
    public List<Venta> listarTodos() {
        return new ArrayList<>(tabla.values());
    }

    @Override
    public List<Venta> listarPorCliente(int idCliente) {
        return tabla.values().stream()
            .filter(v -> v.getCliente().getId() == idCliente)
            .collect(Collectors.toList());
    }

    /**
     * Reporte de ventas por marca en memoria (modo demo).
     * Recorre las ventas registradas y construye una fila de
     * {@link ReporteVentas} por cada item cuya marca coincida.
     */
    @Override
    public List<ReporteVentas> reportePorMarca(String marca) throws Exception {
        if (marca == null || marca.trim().isEmpty()) {
            throw new IllegalArgumentException("La marca a consultar no puede estar vacía.");
        }
        List<ReporteVentas> resultado = new ArrayList<>();
        for (Venta venta : tabla.values()) {
            for (ItemVenta item : venta.getItems()) {
                if (item.getCelular().getMarca().equalsIgnoreCase(marca.trim())) {
                    resultado.add(new ReporteVentas(
                        venta.getCliente().getNombre(),
                        venta.getFecha(),
                        item.getCelular().getModelo(),
                        item.getCelular().getPrecio(),
                        venta.getEmpleado(),
                        item.getCantidad(),
                        item.getSubtotal()));
                }
            }
        }
        if (resultado.isEmpty()) {
            throw new VentaNoEncontradaException(
                "No se encontraron ventas para la marca '" + marca + "'.");
        }
        return resultado;
    }
}
