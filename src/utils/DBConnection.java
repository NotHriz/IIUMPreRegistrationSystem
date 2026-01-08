package utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnection {

    private static Connection connection;

    static {
        System.out.println(
            DBConnection.class.getClassLoader()
            .getResource("resources/db.properties")
        );

        try {
            Properties props = new Properties();

            InputStream is = DBConnection.class
                    .getClassLoader()
                    .getResourceAsStream("resources/db.properties");

            if (is == null) {
                throw new RuntimeException("db.properties not found in classpath");
            }

            props.load(is);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.username");
            String pass = props.getProperty("db.password");

            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(url, user, pass);

            System.out.println("Database connected successfully");

        } catch (Exception e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}
