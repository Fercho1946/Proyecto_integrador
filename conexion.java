package proyecto_integrador;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexion {

    String driver = "com.mysql.cj.jdbc.Driver";
    String database = "proyecto?useTimezone=true&serverTimezone=UTC&useSSL=false";
    String url = "jdbc:mysql://localhost:3306/" + database;
    String usuario = "root";
    String contrasena = "1346790.#*";
    Connection con;

    public conexion() {
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, usuario, contrasena);

        } catch (ClassNotFoundException e) {
            System.err.println("ERROR " + e);
        } catch (SQLException e) {
            System.err.println("ERROR " + e);
        }
    }

    public Connection getConexion() {
        return con;
    }   
}
