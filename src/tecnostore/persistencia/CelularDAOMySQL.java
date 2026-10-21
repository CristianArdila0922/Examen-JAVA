package tecnostore.persistencia;

import tecnostore.modelo.CategoriaGama;
import tecnostore.modelo.Celular;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de ICelularDAO sobre MySQL usando JDBC.
 * Usa PreparedStatement (evita inyección SQL) y try-with-resources.
 */
public class CelularDAOMySQL implements ICelularDAO {

    private final ConexionDB conexionDB;

    public CelularDAOMySQL() {
        this.conexionDB = ConexionDB.getInstancia();
    }

    private Celular mapear(ResultSet rs) throws SQLException {
        return new Celular(
            rs.getInt("id"),
            rs.getString("marca"),
            rs.getString("modelo"),
            rs.getDouble("precio"),
            rs.getInt("stock"),
            rs.getString("sistema_operativo"),
            CategoriaGama.desdeTexto(rs.getString("gama"))
        );
    }

    @Override
    public Celular guardar(Celular celular) throws SQLException {
        String sql = "INSERT INTO celulares (marca, modelo, precio, stock, sistema_operativo, gama) "
            + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, celular.getMarca());
            ps.setString(2, celular.getModelo());
            ps.setDouble(3, celular.getPrecio());
            ps.setInt(4, celular.getStock());
            ps.setString(5, celular.getSistemaOperativo());
            ps.setString(6, celular.getGama().getEtiqueta());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    celular.setId(keys.getInt(1));
                }
            }
            return celular;
        }
    }

    @Override
    public boolean actualizar(Celular celular) throws SQLException {
        String sql = "UPDATE celulares SET marca=?, modelo=?, precio=?, stock=?, "
            + "sistema_operativo=?, gama=? WHERE id=?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, celular.getMarca());
            ps.setString(2, celular.getModelo());
            ps.setDouble(3, celular.getPrecio());
            ps.setInt(4, celular.getStock());
            ps.setString(5, celular.getSistemaOperativo());
            ps.setString(6, celular.getGama().getEtiqueta());
            ps.setInt(7, celular.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM celulares WHERE id=?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Celular> buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM celulares WHERE id=?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Celular> listarTodos() throws SQLException {
        List<Celular> lista = new ArrayList<>();
        String sql = "SELECT * FROM celulares ORDER BY marca, modelo";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Celular> buscarPorMarca(String marca) throws SQLException {
        List<Celular> lista = new ArrayList<>();
        String sql = "SELECT * FROM celulares WHERE marca LIKE ? ORDER BY modelo";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + marca + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public boolean actualizarStock(int id, int nuevoStock) throws SQLException {
        String sql = "UPDATE celulares SET stock=? WHERE id=?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nuevoStock);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }
}
