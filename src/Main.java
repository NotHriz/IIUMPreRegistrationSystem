import pages.LoginPage;
import utils.DBConnection;

public class Main {

    public static void main(String[] args) {

        // 1. Warm up the database connection on startup
        DBConnection.getConnection();

        // 2. Launch the application via the Login Page
        LoginPage loginPage = new LoginPage();
        
        // Since LoginPage extends Application, this calls launch()
        loginPage.show();
    }
}