package Modulos;

import java.awt.GraphicsEnvironment;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 * Gestión de conexión PostgreSQL compatible con PostgreSQL 17/18 (SCRAM-SHA-256)
 * configurable mediante variables de entorno: EXAMEN_JDBC_URL, EXAMEN_DATABASE, EXAMEN_USER, EXAMEN_PASSWORD.
 */
public class Conectar {

    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return value == null ? fallback : value;
    }

    public Connection abrirConexion() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        String dbName = env("EXAMEN_DATABASE", "ExamenArchivos");
        String url = env("EXAMEN_JDBC_URL", "jdbc:postgresql://127.0.0.1:5432/" + dbName);
        Properties props = new Properties();
        props.setProperty("user", env("EXAMEN_USER", "postgres"));
        props.setProperty("password", env("EXAMEN_PASSWORD", ""));
        props.setProperty("connectTimeout", "5");
        return DriverManager.getConnection(url, props);
    }

    public Connection conexion() {
        try {
            return abrirConexion();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error de conexión PostgreSQL: " + e.getMessage());
            if (!GraphicsEnvironment.isHeadless()) {
                JOptionPane.showMessageDialog(null, "Error de conexión: " + e.getMessage());
            }
            return null;
        }
    }
}
