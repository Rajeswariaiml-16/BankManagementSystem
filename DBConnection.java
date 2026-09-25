import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bank_management",
                "root",
                "R16aj@i12"
            );

            System.out.println("Database Connected Successfully");

            return con;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}