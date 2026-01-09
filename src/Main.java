
import services.RegistrationService;
import dao.PreRegistrationDAO;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import model.PreRegistration;
import pages.LoginPage;
import pages.PreRegistrationPage;

import java.util.List;

import utils.*;

public class Main {

    public static void main(String[] args) {

        // Start DB connection
        DBConnection.getConnection();

        LoginPage loginPage = new LoginPage();
        PreRegistrationPage preRegistrationPage = new PreRegistrationPage();

        loginPage.show();

    
    }
}
