package pages;

// JavaFX Core & Stage
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

// JavaFX Layouts
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.ColumnConstraints;

// JavaFX Controls
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

// JavaFX Geometry & Visuals
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

// Internal Layers
import dao.StudentDAO;
import services.AuthService;


public class LoginPage extends Application {

    private static final double NORMAL_WIDTH = 350;
    private static final double NORMAL_HEIGHT = 280;
    private static final double MAX_WIDTH = 400;
    private static final double MAX_HEIGHT = 540;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("University Pre-Registration Login");

        // ===================== LOGIN CARD =====================
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(25));
        grid.setAlignment(Pos.CENTER);

        grid.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        grid.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        grid.setPrefSize(NORMAL_WIDTH, NORMAL_HEIGHT);

        // Welcome text
        Text welcomeText = new Text("Welcome to the New Semester!");
        welcomeText.setTextAlignment(TextAlignment.CENTER);
        GridPane.setHalignment(welcomeText, HPos.CENTER);
        grid.add(welcomeText, 0, 0, 2, 1);

        // Matric number
        Label matricLabel = new Label("Matric Number:");
        TextField matricField = new TextField();
        grid.add(matricLabel, 0, 1);
        grid.add(matricField, 1, 1);

        // Password
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordField, 1, 2);

        // Forgot password
        Label forgotPasswordLabel = new Label("Forgot Password?");
        forgotPasswordLabel.setUnderline(true);
        grid.add(forgotPasswordLabel, 1, 3);

        // Login button
        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(120);
        loginButton.setTextFill(Color.WHITE);
        loginButton.setBackground(new Background(
                new BackgroundFill(Color.DEEPSKYBLUE, new CornerRadii(10), Insets.EMPTY)
        ));
        grid.add(loginButton, 1, 4);

        loginButton.setOnAction(e -> {
            String matric = matricField.getText().trim();
            String password = passwordField.getText().trim();

            AuthService authService = new AuthService();

            if (authService.authenticate(matric, password)) {
                try {
                    // 1. Get current stage
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    
                    // 2. Data Hand-off
                    StudentDAO studentDAO = new StudentDAO();
                    int studentId = studentDAO.getStudentIdByMatric(matric);
                    PreRegistrationPage.studentId = studentId;

                    // 3. SEAMLESS SWITCH
                    // We call start on the EXISTING stage
                    new PreRegistrationPage().start(stage);
                    
                    // 4. RE-FORCE FULLSCREEN
                    // JavaFX can drop fullscreen when a new Scene is attached to the Stage
                    stage.setFullScreen(true);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                // ERROR ALERT (Fullscreen might exit when alert pops up, this is normal JavaFX behavior)
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText("Invalid Credentials");
                alert.setContentText("Matric number or password is incorrect.");
                alert.showAndWait();
            }
        });
        
        // Column constraints
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHalignment(HPos.RIGHT);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPrefWidth(220);
        grid.getColumnConstraints().addAll(col1, col2);

        // Card background
        grid.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(12), Insets.EMPTY)
        ));

        // ===================== ROOT + BACKGROUND IMAGE =====================
        StackPane root = new StackPane(); // no padding here

        // Background image
        Image bgImage = new Image(
                getClass().getResource("uiaLP.jpg").toExternalForm()
        );

        BackgroundSize bgSize = new BackgroundSize(
                100, 100, true, true, true, true  // scale with window
        );

        BackgroundImage backgroundImage = new BackgroundImage(
                bgImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                bgSize
        );

        root.setBackground(new Background(backgroundImage));

        // Overlay for readability
        Region overlay = new Region();
        overlay.setBackground(new Background(
                new BackgroundFill(Color.rgb(0, 0, 0, 0.35), CornerRadii.EMPTY, Insets.EMPTY)
        ));
        overlay.prefWidthProperty().bind(root.widthProperty());
        overlay.prefHeightProperty().bind(root.heightProperty());

        // Card wrapper with padding
        StackPane cardWrapper = new StackPane(grid);
        cardWrapper.setPadding(new Insets(40)); // padding only around card

        // Add overlay and card wrapper to root
        root.getChildren().addAll(overlay, cardWrapper);

        // ===================== SCENE & STAGE =====================
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setMaximized(true); 
        primaryStage.setFullScreen(true); 
        primaryStage.setScene(scene);

        // Apply correct size if started maximized
        if (primaryStage.isMaximized()) {
            grid.setPrefSize(MAX_WIDTH, MAX_HEIGHT);
        }

        primaryStage.show();

        // Resize card on maximize
        primaryStage.maximizedProperty().addListener((obs, oldVal, isMaximized) -> {
            if (isMaximized) {
                grid.setPrefSize(MAX_WIDTH, MAX_HEIGHT);
            } else {
                grid.setPrefSize(NORMAL_WIDTH, NORMAL_HEIGHT);
            }
        });
    }

	public void show() {
		launch();
	}
}



