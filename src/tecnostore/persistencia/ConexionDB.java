package tecnostore.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Patrón Singleton para gestionar la conexión a la base de datos MySQL.
 *
 * Centraliza los parámetros de conexión y garantiza una sola instancia
 * en toda la aplicación. Cambia los valores de URL/USUARIO/CLAVE según
 * tu instalación local de MySQL.
 */
public class ConexionDB {

    // ---- Configuración de la base de datos (ajústala a tu entorno) ----
    private static final String URL =
        "jdbc:mysql://localhost:3306/tecnostore_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USUARIO = "root";
    private static final String CLAVE = "";

    private static ConexionDB instancia;
    private Connection conexion;

    private ConexionDB() {
    }

    /** Devuelve la única instancia (Singleton). */
    public static synchronized ConexionDB getInstancia() {
        if (instancia == null) {
            instancia = new ConexionDB();
        }
        return instancia;
    }

    /**
     * Devuelve la conexión activa, abriéndola si era necesaria.
     * Lanza SQLException si no se puede conectar a MySQL.
     */
    public Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                throw new SQLException(
                    "No se encontró el driver de MySQL (mysql-connector-j). "
                    + "Agrega el .jar al classpath.", ex);
            }
            conexion = DriverManager.getConnection(URL, USUARIO, CLAVE);
        }
        return conexion;
    }

    /**
     * Verifica si es posible conectarse a la base de datos.
     * Útil para decidir en runtime entre persistencia MySQL o memoria.
     */
    public boolean estaDisponible() {
        try (Connection conn = DriverManager.getConnection(URL, USUARIO, CLAVE)) {
            return conn != null;
        } catch (SQLException ex) {
            return false;
        }
    }

    /** Cierra la conexión si estaba abierta. */
    public void cerrar() {
        if (conexion != null) {
            try {
                if (!conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException ex) {
                System.err.println("Error al cerrar la conexión: " + ex.getMessage());
            }
        }
    }
}
