import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conectar {
    public static Connection conectar() {
        String user = "TU_USUARIO";
        String password = "TU_CONTRASEÑA";
        String url = "jdbc:mysql://localhost:3306/banco";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println(connection.getCatalog());
            return connection;
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return null;
        }
    }

}