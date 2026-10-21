package tecnostore.persistencia;

import tecnostore.modelo.ReporteVentas;
import tecnostore.modelo.Venta;

import java.util.List;

/**
 * Interface de acceso a datos para ventas.
 */
public interface IVentaDAO {

    Venta guardar(Venta venta) throws Exception;

    List<Venta> listarTodos() throws Exception;

    List<Venta> listarPorCliente(int idCliente) throws Exception;

    /**
     * Genera el reporte de ventas filtrado por marca de celular.
     * Devuelve una lista de {@link ReporteVentas} con los datos de cada
     * venta de la marca solicitada.
     *
     * @param marca marca a consultar (Samsung, Apple, Xiaomi, ...).
     * @return filas del reporte; nunca vacío (lanza excepción si no hay).
     * @throws tecnostore.excepciones.VentaNoEncontradaException si no hay ventas.
     * @throws Exception si ocurre un error de acceso a datos.
     */
    List<ReporteVentas> reportePorMarca(String marca) throws Exception;
}
