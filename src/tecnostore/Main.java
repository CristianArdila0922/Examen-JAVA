package tecnostore;

import tecnostore.modelo.CategoriaGama;
import tecnostore.modelo.Celular;
import tecnostore.modelo.Cliente;
import tecnostore.persistencia.CelularDAOMemoria;
import tecnostore.persistencia.CelularDAOMySQL;
import tecnostore.persistencia.ClienteDAOMemoria;
import tecnostore.persistencia.ClienteDAOMySQL;
import tecnostore.persistencia.ConexionDB;
import tecnostore.persistencia.ICelularDAO;
import tecnostore.persistencia.IClienteDAO;
import tecnostore.persistencia.IVentaDAO;
import tecnostore.persistencia.VentaDAOMemoria;
import tecnostore.persistencia.VentaDAOMySQL;
import tecnostore.servicio.GestorCelulares;
import tecnostore.servicio.GestorClientes;
import tecnostore.servicio.GestorVentas;
import tecnostore.servicio.ReporteServicio;
import tecnostore.vista.VistaConsola;

import java.util.List;

/**
 * Punto de entrada de la aplicación TecnoStore.
 *
 * Al iniciar intenta conectar con MySQL (tecnostore_db). Si la base de
 * datos no está disponible, cae automáticamente a una implementación en
 * memoria y carga datos de demostración, de modo que el menú sea usable
 * de inmediato sin necesidad de configurar MySQL.
 */
public class Main {

    private static final String RUTA_REPORTE = "reporte_ventas.txt";

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║        SISTEMA DE VENTAS - TECNOSTORE                ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");

        boolean usarMySQL = ConexionDB.getInstancia().estaDisponible();

        ICelularDAO celularDAO;
        IClienteDAO clienteDAO;
        IVentaDAO ventaDAO;

        if (usarMySQL) {
            System.out.println("✓ Conexión a MySQL (tecnostore_db) establecida.\n");
            celularDAO = new CelularDAOMySQL();
            clienteDAO = new ClienteDAOMySQL();
            ventaDAO = new VentaDAOMySQL(clienteDAO, celularDAO);
        } else {
            System.out.println("⚠ No se pudo conectar a MySQL. "
                + "Modo DEMO activado (datos en memoria).");
            System.out.println("  Para usar persistencia real, configura la conexión en "
                + ConexionDB.class.getSimpleName() + ".java y crea la BD con el script SQL.\n");
            celularDAO = new CelularDAOMemoria();
            clienteDAO = new ClienteDAOMemoria();
            ventaDAO = new VentaDAOMemoria();
        }

        GestorCelulares gestorCelulares = new GestorCelulares(celularDAO);
        GestorClientes gestorClientes = new GestorClientes(clienteDAO);
        GestorVentas gestorVentas = new GestorVentas(ventaDAO, celularDAO);
        ReporteServicio reporteServicio =
            new ReporteServicio(gestorCelulares, gestorVentas, RUTA_REPORTE);

        // En modo demo, cargar datos de ejemplo para que el sistema sea usable.
        if (!usarMySQL) {
            cargarDatosDemo(gestorCelulares, gestorClientes, gestorVentas);
        }

        VistaConsola vista = new VistaConsola(gestorCelulares, gestorClientes,
            gestorVentas, reporteServicio);
        vista.iniciar();

        if (usarMySQL) {
            ConexionDB.getInstancia().cerrar();
        }
    }

    /**
     * Crea celulares y clientes de ejemplo y registra un par de ventas
     * para que los reportes muestren resultados de inmediato.
     */
    private static void cargarDatosDemo(GestorCelulares gestorCelulares,
                                        GestorClientes gestorClientes,
                                        GestorVentas gestorVentas) {
        try {
            System.out.println("Cargando datos de demostración...");

            gestorCelulares.registrar(new Celular(0, "Samsung", "Galaxy S24", 1199.99, 12,
                "Android", CategoriaGama.ALTA));
            gestorCelulares.registrar(new Celular(0, "Apple", "iPhone 15", 1299.00, 8,
                "iOS", CategoriaGama.ALTA));
            gestorCelulares.registrar(new Celular(0, "Xiaomi", "Redmi Note 13", 349.50, 25,
                "Android", CategoriaGama.MEDIA));
            gestorCelulares.registrar(new Celular(0, "Motorola", "Moto G Power", 279.00, 3,
                "Android", CategoriaGama.MEDIA));
            gestorCelulares.registrar(new Celular(0, "Nokia", "C100", 149.99, 2,
                "Android", CategoriaGama.BAJA));

            gestorClientes.registrar(new Cliente(0, "Ana Torres", "100200300", "ana@correo.com", "3101234567"));
            gestorClientes.registrar(new Cliente(0, "Luis Gómez", "100200301", "luis@correo.com", "3109876543"));
            gestorClientes.registrar(new Cliente(0, "María Pérez", "100200302", "maria@correo.com", "3115550011"));

            // Ventas de ejemplo para que el top y el reporte por mes tengan datos.
            Cliente ana = gestorClientes.buscarPorIdentificacion("100200300").orElseThrow();
            Cliente luis = gestorClientes.buscarPorIdentificacion("100200301").orElseThrow();
            Cliente maria = gestorClientes.buscarPorIdentificacion("100200302").orElseThrow();

            gestorVentas.registrarVenta(ana, List.of(
                new GestorVentas.SolicitudItem(
                    gestorCelulares.buscarPorMarca("Samsung").get(0), 1)
            ), "Juan Vargas");
            gestorVentas.registrarVenta(luis, List.of(
                new GestorVentas.SolicitudItem(
                    gestorCelulares.buscarPorMarca("Apple").get(0), 1),
                new GestorVentas.SolicitudItem(
                    gestorCelulares.buscarPorMarca("Xiaomi").get(0), 2)
            ), "Laura Sánchez");
            gestorVentas.registrarVenta(maria, List.of(
                new GestorVentas.SolicitudItem(
                    gestorCelulares.buscarPorMarca("Samsung").get(0), 2)
            ), "Pedro Ramírez");

            System.out.println("Datos de demostración cargados: 5 celulares, 3 clientes, 3 ventas.\n");
        } catch (Exception ex) {
            System.out.println("No se pudieron cargar los datos demo: " + ex.getMessage());
        }
    }
}
