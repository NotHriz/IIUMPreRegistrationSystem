package utils;

// Java Standard Library - SQL
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Java Standard Library - IO & Utils
import java.io.InputStream;
import java.util.Properties;

public class DBConnection {
    private static String url;
    private static String user;
    private static String pass;

    static {
        try {
            Properties props = new Properties();
            InputStream is = DBConnection.class
                    .getClassLoader()
                    .getResourceAsStream("resources/db.properties");

            if (is == null) {
                throw new RuntimeException("db.properties not found in classpath");
            }

            props.load(is);
            url = props.getProperty("db.url");
            user = props.getProperty("db.username");
            pass = props.getProperty("db.password");

            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Handled the exception here so you don't have to change your DAO signatures
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            System.err.println("Database Connection Error: " + e.getMessage());
            return null; 
        }
    }
}