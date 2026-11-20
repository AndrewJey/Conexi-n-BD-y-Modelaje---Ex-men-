import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class ExamenDbCheck {
    private static int count;
    private static void check(boolean ok, String msg) {
        count++;
        if (!ok) throw new AssertionError(msg);
    }

    public static void main(String[] args) throws Exception {
        Class.forName("org.postgresql.Driver");
        check(java.sql.DriverManager.getDriver("jdbc:postgresql://127.0.0.1/test") != null, "PostgreSQL JDBC driver loaded");

        if (System.getenv("EXAMEN_TEST_DATABASE") != null) {
            Modulos.Conectar conectar = new Modulos.Conectar();
            try (Connection cn = conectar.abrirConexion()) {
                try (Statement st = cn.createStatement()) {
                    st.execute(
                        "CREATE TEMP TABLE Usuarios ("
                        + "id SERIAL PRIMARY KEY, nombres TEXT, apellidos TEXT, "
                        + "cedulas TEXT, zona TEXT, celular TEXT, activo BOOLEAN)"
                    );
                    st.execute(
                        "INSERT INTO Usuarios (nombres, apellidos, cedulas, zona, celular, activo) VALUES "
                        + "('Andrés', 'Jiménez', '2070001', 'San Carlos', '8888-1111', true), "
                        + "('María', 'O''Brien', '2070002', 'Alajuela', '8888-2222', false)"
                    );
                }
                List<String[]> all = Vista.Busqueda.buscarUsuarios(cn, "");
                check(all.size() == 2, "Empty filter returns all rows");
                List<String[]> one = Vista.Busqueda.buscarUsuarios(cn, "andrés");
                check(one.size() == 1 && "Jiménez".equals(one.get(0)[2]), "Case-insensitive search works");
                List<String[]> sqli = Vista.Busqueda.buscarUsuarios(cn, "' OR '1'='1");
                check(sqli.isEmpty(), "SQL injection payload treated as literal text");
            }
        }
        System.out.println("PASS: " + count + " assertions");
    }
}
