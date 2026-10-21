package tecnostore.utilidades;

import tecnostore.modelo.Celular;
import tecnostore.modelo.Venta;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Utilidades para generar reportes en consola y en archivo.
 * Los cálculos usan la Stream API y colecciones.
 */
public final class ReporteUtils {

    private static final DateTimeFormatter FORMATO_FECHA =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private ReporteUtils() {
    }

    /**
     * Genera el archivo reporte_ventas.txt con el resumen de todas las ventas.
     */
    public static void generarReporteArchivo(String ruta, List<Venta> ventas) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("           REPORTE DE VENTAS - TECNOSTORE           \n");
        sb.append("====================================================\n\n");

        sb.append(String.format("Total de ventas registradas: %d%n%n", ventas.size()));

        double totalGeneral = ventas.stream().mapToDouble(Venta::getTotal).sum();
        sb.append(String.format("Monto total vendido (con IVA): $%.2f%n", totalGeneral));
        sb.append(String.format("Monto total sin IVA:           $%.2f%n%n",
            ventas.stream().mapToDouble(Venta::getSubtotal).sum()));

        sb.append("----------------------------------------------------\n");
        sb.append("Detalle de ventas\n");
        sb.append("----------------------------------------------------\n");
        for (Venta venta : ventas) {
            sb.append(String.format("Venta #%d | Cliente: %s | Fecha: %s | Total: $%.2f%n",
                venta.getId(), venta.getCliente().getNombre(),
                venta.getFecha().format(FORMATO_FECHA), venta.getTotal()));
            for (var item : venta.getItems()) {
                sb.append(String.format("   - %s %s x%d = $%.2f%n",
                    item.getCelular().getMarca(), item.getCelular().getModelo(),
                    item.getCantidad(), item.getSubtotal()));
            }
            sb.append("\n");
        }

        sb.append("====================================================\n");
        sb.append("Fin del reporte\n");
        sb.append("====================================================\n");

        ArchivoUtils.escribir(ruta, sb.toString());
    }

    /**
     * Filtra los celulares con stock bajo (menor a 5 unidades) usando Stream API.
     */
    public static List<Celular> celularesConStockBajo(List<Celular> catalogo) {
        return catalogo.stream()
            .filter(Celular::tieneStockBajo)
            .collect(Collectors.toList());
    }

    /**
     * Top 3 celulares más vendidos por cantidad de unidades, usando Stream API.
     */
    public static List<Map.Entry<Celular, Integer>> top3MasVendidos(List<Venta> ventas) {
        // Acumula las cantidades vendidas por celular.
        Map<Celular, Integer> cantidades = new TreeMap<>(
            (a, b) -> Integer.compare(a.getId(), b.getId()));
        for (Venta venta : ventas) {
            for (var item : venta.getItems()) {
                cantidades.merge(item.getCelular(),
                    item.getCantidad(), Integer::sum);
            }
        }
        return cantidades.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(3)
            .collect(Collectors.toList());
    }

    /**
     * Total de ventas agrupadas por mes (formato "yyyy-MM"), usando Stream API.
     */
    public static Map<String, DoubleSummaryStatistics> ventasPorMes(List<Venta> ventas) {
        return ventas.stream()
            .collect(Collectors.groupingBy(
                v -> v.getFecha().getYear() + "-"
                    + String.format("%02d", v.getFecha().getMonthValue()),
                TreeMap::new,
                Collectors.summarizingDouble(Venta::getTotal)));
    }
}
