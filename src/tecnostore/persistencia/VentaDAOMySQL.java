package tecnostore.persistencia;

import tecnostore.excepciones.VentaNoEncontradaException;
import tecnostore.modelo.Celular;
import tecnostore.modelo.Cliente;
import tecnostore.modelo.ItemVenta;
import tecnostore.modelo.ReporteVentas;
import tecnostore.modelo.Venta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación de IVentaDAO sobre MySQL.
 * La venta y sus detalles se guardan en una transacción: si algo falla,
 * se hace rollback para no dejar datos a medias.
 */
public class VentaDAOMySQL implements IVentaDAO {

    private final ConexionDB conexionDB;
    private final IClienteDAO clienteDAO;
    private final ICelularDAO celularDAO;

    public VentaDAOMySQL(IClienteDAO clienteDAO, ICelularDAO celularDAO) {
        this.conexionDB = ConexionDB.getInstancia();
        this.clienteDAO = clienteDAO;
        this.celularDAO = celularDAO;
    }

    @Override
    public Venta guardar(Venta venta) throws SQLException {
        String sqlVenta = "INSERT INTO ventas (id_cliente, fecha, total, empleado) VALUES (?, ?, ?, ?)";
        String sqlDetalle =
            "INSERT INTO detalle_ventas (id_venta, id_celular, cantidad, subtotal) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = conexionDB.getConexion();
            conn.setAutoCommit(false);

            try (PreparedStatement psVenta =
                     conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                psVenta.setInt(1, venta.getCliente().getId());
                psVenta.setTimestamp(2, Timestamp.valueOf(venta.getFecha()));
                psVenta.setDouble(3, venta.getTotal());
                psVenta.setString(4, venta.getEmpleado());
                psVenta.executeUpdate();
                int idVentaGenerado;
                try (ResultSet keys = psVenta.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("No se pudo obtener el id de la venta.");
                    }
                    idVentaGenerado = keys.getInt(1);
                    venta.setId(idVentaGenerado);
                }

                try (PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle,
                        Statement.RETURN_GENERATED_KEYS)) {
                    int itemCounter = 0;
                    for (ItemVenta item : venta.getItems()) {
                        psDetalle.setInt(1, idVentaGenerado);
                        psDetalle.setInt(2, item.getCelular().getId());
                        psDetalle.setInt(3, item.getCantidad());
                        psDetalle.setDouble(4, item.getSubtotal());
                        psDetalle.addBatch();
                        itemCounter++;
                    }
                    psDetalle.executeBatch();
                    // Asignar ids a los items (1..n generados por el batch)
                    try (ResultSet keys = psDetalle.getGeneratedKeys()) {
                        int i = 0;
                        for (ItemVenta item : venta.getItems()) {
                            if (keys.next()) {
                                item.setId(keys.getInt(1));
                            }
                            i++;
                        }
                    }
                }
            }
            conn.commit();
            return venta;
        } catch (SQLException ex) {
            if (conn != null) {
                conn.rollback();
            }
            throw ex;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }
        }
    }

    @Override
    public List<Venta> listarTodos() throws Exception {
        return listarPorCondicion(null);
    }

    @Override
    public List<Venta> listarPorCliente(int idCliente) throws Exception {
        return listarPorCondicion("WHERE v.id_cliente=" + idCliente);
    }

    private List<Venta> listarPorCondicion(String where) throws Exception {
        String sql = "SELECT v.id AS v_id, v.fecha AS v_fecha, v.total AS v_total, "
            + "v.empleado AS v_emp, "
            + "c.id AS c_id, c.nombre AS c_nombre, c.identificacion AS c_ident, "
            + "c.correo AS c_correo, c.telefono AS c_tel, "
            + "d.id AS d_id, d.id_celular AS d_id_cel, d.cantidad AS d_cant, d.subtotal AS d_sub "
            + "FROM ventas v "
            + "JOIN clientes c ON v.id_cliente = c.id "
            + "JOIN detalle_ventas d ON d.id_venta = v.id "
            + (where == null ? "" : where)
            + " ORDER BY v.id, d.id";

        Map<Integer, Venta> ventasMap = new HashMap<>();

        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int ventaId = rs.getInt("v_id");
                Venta venta = ventasMap.get(ventaId);
                if (venta == null) {
                    Cliente cliente = new Cliente(
                        rs.getInt("c_id"),
                        rs.getString("c_nombre"),
                        rs.getString("c_ident"),
                        rs.getString("c_correo"),
                        rs.getString("c_tel"));
                    venta = new Venta();
                    venta.setId(ventaId);
                    venta.setCliente(cliente);
                    venta.setEmpleado(rs.getString("v_emp"));
                    Timestamp ts = rs.getTimestamp("v_fecha");
                    if (ts != null) {
                        venta.setFecha(ts.toLocalDateTime());
                    }
                    venta.setTotal(rs.getDouble("v_total"));
                    ventasMap.put(ventaId, venta);
                }
                // Reconstruir el celular del detalle (lo buscamos en el catálogo).
                int idCel = rs.getInt("d_id_cel");
                Celular celular = celularDAO.buscarPorId(idCel).orElse(null);
                if (celular != null) {
                    ItemVenta item = new ItemVenta(
                        rs.getInt("d_id"),
                        celular,
                        rs.getInt("d_cant"));
                    item.recalcularSubtotal();
                    venta.agregarItem(item);
                }
            }
        }
        return new ArrayList<>(ventasMap.values());
    }

    /**
     * Reporte de ventas filtrado por marca de celular, ejecutado contra MySQL.
     * Usa un PreparedStatement con parámetro para evitar inyección SQL y un
     * bloque try-catch-finally para liberar los recursos JDBC.
     *
     * Si no hay ventas para la marca, lanza VentaNoEncontradaException.
     */
    @Override
    public List<ReporteVentas> reportePorMarca(String marca) throws Exception {
        String sql = "SELECT c.nombre AS cliente, v.fecha AS fecha, cel.modelo AS modelo, "
            + "cel.precio AS precio, v.empleado AS empleado, "
            + "d.cantidad AS cantidad, d.subtotal AS subtotal "
            + "FROM ventas v "
            + "JOIN clientes c ON v.id_cliente = c.id "
            + "JOIN detalle_ventas d ON d.id_venta = v.id "
            + "JOIN celulares cel ON d.id_celular = cel.id "
            + "WHERE LOWER(cel.marca) = LOWER(?) "
            + "ORDER BY v.fecha, v.id";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ReporteVentas> resultado = new ArrayList<>();
        try {
            conn = conexionDB.getConexion();
            ps = conn.prepareStatement(sql);
            ps.setString(1, marca);
            rs = ps.executeQuery();
            while (rs.next()) {
                resultado.add(new ReporteVentas(
                    rs.getString("cliente"),
                    rs.getTimestamp("fecha").toLocalDateTime(),
                    rs.getString("modelo"),
                    rs.getDouble("precio"),
                    rs.getString("empleado"),
                    rs.getInt("cantidad"),
                    rs.getDouble("subtotal")));
            }
            if (resultado.isEmpty()) {
                throw new VentaNoEncontradaException(
                    "No se encontraron ventas para la marca '" + marca + "'.");
            }
            return resultado;
        } catch (VentaNoEncontradaException ex) {
            // Re-lanzamos sin envolver para que el mensaje llegue claro al usuario.
            throw ex;
        } catch (SQLException ex) {
            throw new SQLException(
                "Error al consultar el reporte de ventas por marca: " + ex.getMessage(), ex);
        } finally {
            // Liberamos los recursos en orden inverso de apertura.
            if (rs != null) {
                try { rs.close(); } catch (SQLException ignored) { }
            }
            if (ps != null) {
                try { ps.close(); } catch (SQLException ignored) { }
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) { }
            }
        }
    }
}
