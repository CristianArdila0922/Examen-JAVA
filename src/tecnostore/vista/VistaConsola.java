package tecnostore.vista;

import tecnostore.excepciones.VentaNoEncontradaException;
import tecnostore.modelo.CategoriaGama;
import tecnostore.modelo.Celular;
import tecnostore.modelo.Cliente;
import tecnostore.modelo.ReporteVentas;
import tecnostore.modelo.Venta;
import tecnostore.patron.descuento.ContextoDescuento;
import tecnostore.patron.descuento.DescuentoFijo;
import tecnostore.patron.descuento.DescuentoNinguno;
import tecnostore.patron.descuento.DescuentoPorcentaje;
import tecnostore.patron.descuento.EstrategiaDescuento;
import tecnostore.servicio.GestorCelulares;
import tecnostore.servicio.GestorClientes;
import tecnostore.servicio.GestorVentas;
import tecnostore.servicio.ReporteServicio;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Vista de consola (capa de presentación). Se encarga de la interacción
 * con el usuario: lectura de datos, validación básica de entrada y
 * presentación de resultados. No contiene lógica de negocio, solo
 * delega en los servicios.
 *
 * Sigue el patrón Modelo-Vista-Controlador: los servicios actúan como
 * controladores, las entidades son el modelo, y esta clase es la vista.
 */
public class VistaConsola {

    private static final String SEPARADOR =
        "══════════════════════════════════════════════════════════";
    private static final NumberFormat MONEDA = NumberFormat.getCurrencyInstance();

    private final Scanner sc;
    private final GestorCelulares gestorCelulares;
    private final GestorClientes gestorClientes;
    private final GestorVentas gestorVentas;
    private final ReporteServicio reporteServicio;

    public VistaConsola(GestorCelulares gestorCelulares,
                        GestorClientes gestorClientes,
                        GestorVentas gestorVentas,
                        ReporteServicio reporteServicio) {
        this.sc = new Scanner(System.in);
        this.gestorCelulares = gestorCelulares;
        this.gestorClientes = gestorClientes;
        this.gestorVentas = gestorVentas;
        this.reporteServicio = reporteServicio;
    }

    // ===================== Arranque del menú =====================

    public void iniciar() {
        boolean salir = false;
        while (!salir) {
            mostrarMenuPrincipal();
            int opcion = leerEntero("Seleccione una opción: ");
            switch (opcion) {
                case 1 -> menuCelulares();
                case 2 -> menuClientes();
                case 3 -> menuRegistrarVenta();
                case 4 -> menuReportes();
                case 5 -> generarReporteArchivo();
                case 6 -> menuDescuentos();
                case 7 -> menuReportePorMarca();
                case 0 -> {
                    System.out.println("Gracias por usar TecnoStore. ¡Hasta pronto!");
                    salir = true;
                }
                default -> System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    // ===================== Menú principal =====================

    private void mostrarMenuPrincipal() {
        System.out.println("\n" + SEPARADOR);
        System.out.println("           TECNOSTORE - Sistema de Ventas");
        System.out.println(SEPARADOR);
        System.out.println(" 1. Gestión de Celulares");
        System.out.println(" 2. Gestión de Clientes");
        System.out.println(" 3. Registrar Venta");
        System.out.println(" 4. Reportes y Análisis");
        System.out.println(" 5. Generar reporte_ventas.txt");
        System.out.println(" 6. Configurar Descuento (Strategy)");
        System.out.println(" 7. Reporte de Ventas por Marca");
        System.out.println(" 0. Salir");
        System.out.println(SEPARADOR);
    }

    // ===================== Submenú: Celulares =====================

    private void menuCelulares() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- Gestión de Celulares ---");
            System.out.println(" 1. Registrar celular");
            System.out.println(" 2. Actualizar celular");
            System.out.println(" 3. Eliminar celular");
            System.out.println(" 4. Listar celulares");
            System.out.println(" 5. Buscar por marca");
            System.out.println(" 0. Volver");
            int opcion = leerEntero("Opción: ");
            try {
                switch (opcion) {
                    case 1 -> registrarCelular();
                    case 2 -> actualizarCelular();
                    case 3 -> eliminarCelular();
                    case 4 -> listarCelulares();
                    case 5 -> buscarCelularesPorMarca();
                    case 0 -> volver = true;
                    default -> System.out.println("Opción no válida.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void registrarCelular() throws Exception {
        System.out.println("\n--- Registrar Celular ---");
        String marca = leerTexto("Marca: ");
        String modelo = leerTexto("Modelo: ");
        String so = leerTexto("Sistema operativo: ");
        String gama = leerTexto("Gama (Alta/Media/Baja): ");
        double precio = leerDecimal("Precio: ");
        int stock = leerEntero("Stock: ");

        Celular celular = new Celular(0, marca, modelo, precio, stock, so,
            CategoriaGama.desdeTexto(gama));
        gestorCelulares.registrar(celular);
        System.out.println("Celular registrado: " + celular);
    }

    private void actualizarCelular() throws Exception {
        System.out.println("\n--- Actualizar Celular ---");
        int id = leerEntero("ID del celular a actualizar: ");
        Celular celular = gestorCelulares.buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Celular no encontrado."));
        System.out.println("Actual: " + celular);
        String marca = leerTextoOpcional("Marca [" + celular.getMarca() + "]: ");
        String modelo = leerTextoOpcional("Modelo [" + celular.getModelo() + "]: ");
        String so = leerTextoOpcional("Sistema operativo [" + celular.getSistemaOperativo() + "]: ");
        String gama = leerTextoOpcional("Gama [" + celular.getGama().getEtiqueta() + "] (Alta/Media/Baja): ");
        double precio = leerDecimalOpcional("Precio [" + celular.getPrecio() + "]: ");
        int stock = leerEnteroOpcional("Stock [" + celular.getStock() + "]: ");

        if (!marca.isBlank()) celular.setMarca(marca);
        if (!modelo.isBlank()) celular.setModelo(modelo);
        if (!so.isBlank()) celular.setSistemaOperativo(so);
        if (!gama.isBlank()) celular.setGama(CategoriaGama.desdeTexto(gama));
        if (precio >= 0) celular.setPrecio(precio);
        if (stock >= 0) celular.setStock(stock);

        gestorCelulares.actualizar(celular);
        System.out.println("Celular actualizado: " + celular);
    }

    private void eliminarCelular() throws Exception {
        int id = leerEntero("ID del celular a eliminar: ");
        if (gestorCelulares.eliminar(id)) {
            System.out.println("Celular eliminado correctamente.");
        } else {
            System.out.println("No se encontró el celular.");
        }
    }

    private void listarCelulares() throws Exception {
        List<Celular> lista = gestorCelulares.listar();
        if (lista.isEmpty()) {
            System.out.println("No hay celulares registrados.");
            return;
        }
        System.out.println("\n--- Catálogo de Celulares ---");
        imprimirTablaCelulares(lista);
    }

    private void buscarCelularesPorMarca() throws Exception {
        String marca = leerTexto("Marca a buscar: ");
        List<Celular> lista = gestorCelulares.buscarPorMarca(marca);
        if (lista.isEmpty()) {
            System.out.println("No se encontraron celulares de esa marca.");
            return;
        }
        imprimirTablaCelulares(lista);
    }

    private void imprimirTablaCelulares(List<Celular> lista) {
        System.out.printf("%-4s %-12s %-18s %-14s %-7s %-10s %-6s%n",
            "ID", "Marca", "Modelo", "SO", "Gama", "Precio", "Stock");
        System.out.println("-".repeat(75));
        for (Celular c : lista) {
            System.out.printf("%-4d %-12s %-18s %-14s %-7s %-10s %-6d%n",
                c.getId(), c.getMarca(), c.getModelo(), c.getSistemaOperativo(),
                c.getGama().getEtiqueta(), MONEDA.format(c.getPrecio()), c.getStock());
        }
    }

    // ===================== Submenú: Clientes =====================

    private void menuClientes() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- Gestión de Clientes ---");
            System.out.println(" 1. Registrar cliente");
            System.out.println(" 2. Actualizar cliente");
            System.out.println(" 3. Eliminar cliente");
            System.out.println(" 4. Listar clientes");
            System.out.println(" 0. Volver");
            int opcion = leerEntero("Opción: ");
            try {
                switch (opcion) {
                    case 1 -> registrarCliente();
                    case 2 -> actualizarCliente();
                    case 3 -> eliminarCliente();
                    case 4 -> listarClientes();
                    case 0 -> volver = true;
                    default -> System.out.println("Opción no válida.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void registrarCliente() throws Exception {
        System.out.println("\n--- Registrar Cliente ---");
        String nombre = leerTexto("Nombre: ");
        String identificacion = leerTexto("Identificación: ");
        String correo = leerTexto("Correo: ");
        String telefono = leerTextoOpcional("Teléfono: ");
        Cliente cliente = new Cliente(0, nombre, identificacion, correo, telefono);
        gestorClientes.registrar(cliente);
        System.out.println("Cliente registrado: " + cliente);
    }

    private void actualizarCliente() throws Exception {
        int id = leerEntero("ID del cliente a actualizar: ");
        Cliente cliente = gestorClientes.buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado."));
        System.out.println("Actual: " + cliente);
        String nombre = leerTextoOpcional("Nombre [" + cliente.getNombre() + "]: ");
        String identificacion = leerTextoOpcional("Identificación [" + cliente.getIdentificacion() + "]: ");
        String correo = leerTextoOpcional("Correo [" + cliente.getCorreo() + "]: ");
        String telefono = leerTextoOpcional("Teléfono [" + cliente.getTelefono() + "]: ");

        if (!nombre.isBlank()) cliente.setNombre(nombre);
        if (!identificacion.isBlank()) cliente.setIdentificacion(identificacion);
        if (!correo.isBlank()) cliente.setCorreo(correo);
        if (!telefono.isBlank()) cliente.setTelefono(telefono);

        gestorClientes.actualizar(cliente);
        System.out.println("Cliente actualizado: " + cliente);
    }

    private void eliminarCliente() throws Exception {
        int id = leerEntero("ID del cliente a eliminar: ");
        if (gestorClientes.eliminar(id)) {
            System.out.println("Cliente eliminado correctamente.");
        } else {
            System.out.println("No se encontró el cliente.");
        }
    }

    private void listarClientes() throws Exception {
        List<Cliente> lista = gestorClientes.listar();
        if (lista.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        System.out.println("\n--- Clientes ---");
        System.out.printf("%-4s %-25s %-15s %-28s %-12s%n",
            "ID", "Nombre", "Identificación", "Correo", "Teléfono");
        System.out.println("-".repeat(90));
        for (Cliente c : lista) {
            System.out.printf("%-4d %-25s %-15s %-28s %-12s%n",
                c.getId(), c.getNombre(), c.getIdentificacion(), c.getCorreo(), c.getTelefono());
        }
    }

    // ===================== Registrar Venta =====================

    private void menuRegistrarVenta() {
        try {
            System.out.println("\n--- Registrar Venta ---");
            List<Cliente> clientes = gestorClientes.listar();
            if (clientes.isEmpty()) {
                System.out.println("Primero debe registrar al menos un cliente.");
                return;
            }
            List<Celular> catalogo = gestorCelulares.listar();
            if (catalogo.isEmpty()) {
                System.out.println("Primero debe registrar al menos un celular.");
                return;
            }

            // Elegir cliente.
            listarClientes();
            int idCliente = leerEntero("ID del cliente: ");
            Cliente cliente = gestorClientes.buscarPorId(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado."));

            // Elegir celulares.
            List<GestorVentas.SolicitudItem> solicitudes = new ArrayList<>();
            boolean agregarMas = true;
            while (agregarMas) {
                listarCelulares();
                int idCel = leerEntero("ID del celular a comprar (0 para terminar): ");
                if (idCel == 0) {
                    agregarMas = false;
                    break;
                }
                Celular celular = gestorCelulares.buscarPorId(idCel)
                    .orElseThrow(() -> new IllegalArgumentException("Celular no encontrado."));
                if (celular.getStock() <= 0) {
                    System.out.println("Ese celular no tiene stock disponible.");
                    continue;
                }
                int cantidad = leerEntero("Cantidad (disponible " + celular.getStock() + "): ");
                solicitudes.add(new GestorVentas.SolicitudItem(celular, cantidad));

                String seguir = leerTextoOpcional("¿Agregar otro celular? (s/n) [n]: ");
                agregarMas = seguir.trim().equalsIgnoreCase("s");
            }

            if (solicitudes.isEmpty()) {
                System.out.println("Venta cancelada: no se seleccionaron celulares.");
                return;
            }

            // Empleado que realiza la venta (dato requerido por el reporte por marca).
            String empleado = leerTexto("Empleado que realiza la venta: ");

            // Mostrar resumen previo.
            double subtotal = solicitudes.stream()
                .mapToDouble(s -> s.celular.getPrecio() * s.cantidad).sum();
            double totalEstimado = gestorVentas.simularTotal(subtotal);
            System.out.println("\n--- Resumen de la venta ---");
            System.out.println("Descuento aplicado: " + gestorVentas.getContextoDescuento().descripcion());
            System.out.printf("Subtotal:        %s%n", MONEDA.format(subtotal));
            System.out.printf("IVA (19%%):        %s%n", MONEDA.format(totalEstimado - (totalEstimado / 1.19)));
            System.out.printf("Total estimado:   %s%n", MONEDA.format(totalEstimado));

            String confirmar = leerTextoOpcional("¿Confirmar venta? (s/n) [n]: ");
            if (!confirmar.trim().equalsIgnoreCase("s")) {
                System.out.println("Venta cancelada.");
                return;
            }

            Venta venta = gestorVentas.registrarVenta(cliente, solicitudes, empleado);
            System.out.println("\n✓ Venta registrada con éxito:");
            System.out.println("Venta #" + venta.getId() + " | Total: " + MONEDA.format(venta.getTotal()));
            System.out.println("Items:");
            for (var item : venta.getItems()) {
                System.out.printf("  - %s %s x%d = %s%n",
                    item.getCelular().getMarca(), item.getCelular().getModelo(),
                    item.getCantidad(), MONEDA.format(item.getSubtotal()));
            }
        } catch (Exception ex) {
            System.out.println("Error al registrar la venta: " + ex.getMessage());
        }
    }

    // ===================== Reportes =====================

    private void menuReportes() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- Reportes y Análisis ---");
            System.out.println(" 1. Celulares con stock bajo (< 5)");
            System.out.println(" 2. Top 3 celulares más vendidos");
            System.out.println(" 3. Ventas totales por mes");
            System.out.println(" 4. Listar ventas");
            System.out.println(" 0. Volver");
            int opcion = leerEntero("Opción: ");
            try {
                switch (opcion) {
                    case 1 -> reporteStockBajo();
                    case 2 -> reporteTop3();
                    case 3 -> reporteVentasPorMes();
                    case 4 -> listarVentas();
                    case 0 -> volver = true;
                    default -> System.out.println("Opción no válida.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void reporteStockBajo() throws Exception {
        List<Celular> lista = reporteServicio.stockBajo();
        if (lista.isEmpty()) {
            System.out.println("No hay celulares con stock bajo. Todo en orden.");
            return;
        }
        System.out.println("\n--- Celulares con stock bajo (menor a 5) ---");
        for (Celular c : lista) {
            System.out.printf("- [id=%d] %s %s  | Stock: %d | %s%n",
                c.getId(), c.getMarca(), c.getModelo(), c.getStock(),
                c.getGama().getEtiqueta());
        }
    }

    private void reporteTop3() throws Exception {
        var top = reporteServicio.top3MasVendidos();
        if (top.isEmpty()) {
            System.out.println("Aún no hay ventas registradas para calcular el top.");
            return;
        }
        System.out.println("\n--- Top 3 celulares más vendidos ---");
        int posicion = 1;
        for (var entry : top) {
            Celular c = entry.getKey();
            System.out.printf("%d. %s %s - %d unidades vendidas (gama %s)%n",
                posicion++, c.getMarca(), c.getModelo(), entry.getValue(), c.getGama().getEtiqueta());
        }
    }

    private void reporteVentasPorMes() throws Exception {
        var ventasMes = reporteServicio.ventasPorMes();
        if (ventasMes.isEmpty()) {
            System.out.println("Aún no hay ventas registradas.");
            return;
        }
        System.out.println("\n--- Ventas totales por mes ---");
        System.out.printf("%-10s %-10s %-15s%n", "Mes", "Ventas", "Total");
        System.out.println("-".repeat(40));
        for (var entry : ventasMes.entrySet()) {
            System.out.printf("%-10s %-10d %-15s%n",
                entry.getKey(), entry.getValue().getCount(),
                MONEDA.format(entry.getValue().getSum()));
        }
    }

    private void listarVentas() throws Exception {
        List<Venta> ventas = gestorVentas.listarVentas();
        if (ventas.isEmpty()) {
            System.out.println("No hay ventas registradas.");
            return;
        }
        System.out.println("\n--- Ventas registradas ---");
        for (Venta v : ventas) {
            System.out.printf("Venta #%d | %s | %s | Total: %s | Items: %d%n",
                v.getId(), v.getCliente().getNombre(), v.getFechaFormateada(),
                MONEDA.format(v.getTotal()), v.getItems().size());
        }
    }

    // ===================== Reporte de Ventas por Marca =====================

    private void menuReportePorMarca() {
        System.out.println("\n--- Reporte de Ventas por Marca ---");
        // Mostrar las marcas disponibles como ayuda al usuario.
        try {
            List<Celular> catalogo = gestorCelulares.listar();
            if (!catalogo.isEmpty()) {
                String marcas = catalogo.stream()
                    .map(Celular::getMarca).distinct()
                    .reduce((a, b) -> a + ", " + b).orElse("");
                System.out.println("Marcas disponibles: " + marcas);
            }
        } catch (Exception ignored) {
            // Si no se puede listar el catálogo, se continúa sin mostrar marcas.
        }

        String marca = leerTexto("Ingrese la marca a consultar: ");
        try {
            List<ReporteVentas> filas = reporteServicio.reporteVentasPorMarca(marca);
            System.out.println("\nVentas de la marca: " + marca);
            System.out.printf("%-18s %-16s %-18s %-11s %-15s %-5s %-10s%n",
                "Cliente", "Fecha", "Modelo", "Precio", "Empleado", "Cant.", "Subtotal");
            System.out.println("-".repeat(98));
            for (ReporteVentas r : filas) {
                System.out.printf("%-18s %-16s %-18s %-11s %-15s %-5d %-10s%n",
                    r.getNombreCliente(), r.getFechaFormateada(), r.getModeloCelular(),
                    MONEDA.format(r.getPrecioVenta()), r.getEmpleado(),
                    r.getCantidad(), MONEDA.format(r.getSubtotal()));
            }
            int totalUnidades = filas.stream().mapToInt(ReporteVentas::getCantidad).sum();
            double valorTotal = filas.stream().mapToDouble(ReporteVentas::getSubtotal).sum();
            System.out.println("-".repeat(98));
            System.out.printf("Total de unidades vendidas: %d%n", totalUnidades);
            System.out.printf("Valor total de ventas: %s%n", MONEDA.format(valorTotal));
        } catch (VentaNoEncontradaException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Error al generar el reporte: " + ex.getMessage());
        }
    }

    // ===================== Reporte en archivo =====================

    private void generarReporteArchivo() {
        try {
            reporteServicio.generarReporteArchivo();
            System.out.println("Reporte generado correctamente: reporte_ventas.txt");
        } catch (Exception ex) {
            System.out.println("Error al generar el reporte: " + ex.getMessage());
        }
    }

    // ===================== Descuentos (Strategy) =====================

    private void menuDescuentos() {
        System.out.println("\n--- Configurar Descuento (Patrón Strategy) ---");
        System.out.println(" Estrategia actual: " + gestorVentas.getContextoDescuento().descripcion());
        System.out.println(" 1. Sin descuento");
        System.out.println(" 2. Descuento por porcentaje");
        System.out.println(" 3. Descuento fijo");
        System.out.println(" 0. Volver");
        int opcion = leerEntero("Opción: ");
        ContextoDescuento contexto = gestorVentas.getContextoDescuento();
        EstrategiaDescuento nueva = null;
        switch (opcion) {
            case 1 -> nueva = new DescuentoNinguno();
            case 2 -> {
                double pct = leerDecimal("Porcentaje de descuento (0-100): ");
                nueva = new DescuentoPorcentaje(pct);
            }
            case 3 -> {
                double monto = leerDecimal("Monto fijo de descuento: ");
                nueva = new DescuentoFijo(monto);
            }
            case 0 -> {
                System.out.println("Configuración sin cambios.");
                return;
            }
            default -> System.out.println("Opción no válida.");
        }
        if (nueva != null) {
            contexto.setEstrategia(nueva);
            System.out.println("Descuento configurado: " + contexto.descripcion());
        }
    }

    // ===================== Utilidades de entrada =====================

    private String leerTexto(String mensaje) {
        String valor;
        do {
            System.out.print(mensaje);
            valor = sc.nextLine().trim();
            if (valor.isEmpty()) {
                System.out.println("El valor no puede estar vacío.");
            }
        } while (valor.isEmpty());
        return valor;
    }

    private String leerTextoOpcional(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine().trim();
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = sc.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException ex) {
                System.out.println("Ingrese un número entero válido.");
            }
        }
    }

    private int leerEnteroOpcional(String mensaje) {
        System.out.print(mensaje);
        String linea = sc.nextLine().trim();
        if (linea.isEmpty()) {
            return -1; // Indica "no cambiar".
        }
        try {
            return Integer.parseInt(linea);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private double leerDecimal(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = sc.nextLine().trim().replace(",", ".");
            try {
                return Double.parseDouble(linea);
            } catch (NumberFormatException ex) {
                System.out.println("Ingrese un número decimal válido.");
            }
        }
    }

    private double leerDecimalOpcional(String mensaje) {
        System.out.print(mensaje);
        String linea = sc.nextLine().trim().replace(",", ".");
        if (linea.isEmpty()) {
            return -1; // Indica "no cambiar".
        }
        try {
            return Double.parseDouble(linea);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}
