package pages;

// JavaFX Core
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.Cursor;

// JavaFX UI Controls
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;

// JavaFX Layouts
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.layout.ColumnConstraints;

// JavaFX Properties & Collections
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

// JavaFX Geometry & Visuals
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

// Java Utilities
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

// Data Access Objects 
import dao.CourseDAO;
import dao.SectionDAO;
import dao.PreRegistrationDAO;
import dao.StudentDAO;

// Services 
import services.RegistrationService; 

// Models 
import model.Course;
import model.Section;
import model.PreRegistration;

// Internal Pages
import pages.LoginPage;

@SuppressWarnings("unused")
public class PreRegistrationPage extends Application {

    LoginPage loginPage = new LoginPage();
    // Get Student ID From login page
    public static int studentId = -1;

    // --- Data Models ---

    // Class to track what the user has actually registered
    static class RegisteredSubject {
        // 1. Declare the list at the class level
        List<PreRegistration> registeredSubjects = new ArrayList<>();

        // 2. Create a constructor to handle the logic
        public RegisteredSubject(int studentId) {
            PreRegistrationDAO preDAO = new PreRegistrationDAO();
            // 3. Fix syntax: remove the "." after "new" and call the method on the instance
            this.registeredSubjects = preDAO.getCurrentPreRegistrations(studentId);
        }
    }
    
    private List<PreRegistration> myRegistered = new ArrayList<>();
    
    // --- UI Components ---
    private GridPane registeredTable;
    private Label totalCreditsLabel;
    private VBox subjectsListContainer;
    private GridPane timetableGrid;
    private ScrollPane mainScrollPane;
    private StackPane rootStack;
    private Region overlayEffect;
    
    private Button coreBtn;
    private Button electiveBtn;
    private TextField searchField;
    private String currentCategory = "CORE"; // Default view

    private double totalCredits = 0.0;

    // --- Constants ---
    private final String BLUE_PRIMARY = "#0055ff";
    private final String BLUE_HOVER = "#4d88ff";
    private final String SUCCESS_GREEN = "#28a745";
    private final String ERROR_RED = "#dc3545";
    private final String GREY_INACTIVE = "#f0f0f0";

    @Override
    public void start(Stage primaryStage) {
        initializeDatabase(); // Load Data from DB

        VBox mainLayout = createMainLayout();
        
        mainScrollPane = new ScrollPane(mainLayout);
        mainScrollPane.setFitToWidth(true);
        mainScrollPane.setStyle("-fx-background-color: #ffffff; -fx-background: #ffffff; -fx-border-color: transparent;");

        overlayEffect = new Region();
        overlayEffect.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");
        overlayEffect.setVisible(false);

        rootStack = new StackPane(mainScrollPane, overlayEffect);
        rootStack.setAlignment(Pos.CENTER);

        Scene scene = new Scene(rootStack, 1280, 800);
        // Added CSS for white table background in popup (FIX #6)
        scene.getStylesheets().add("data:text/css," + 
            ".search-button { -fx-background-color: " + BLUE_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; } " +
            ".search-button:hover { -fx-background-color: " + BLUE_HOVER + "; } " +
            ".card { -fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);} " +
            ".section-link { -fx-text-fill: " + BLUE_PRIMARY + "; -fx-underline: true; -fx-cursor: hand; -fx-font-weight: bold;} " +
            ".popup-table .table-row-cell { -fx-background-color: white; } " +
            ".popup-table .column-header-background { -fx-background-color: #f8f9fa; }");

        primaryStage.setTitle("University Pre-Registration System");
        primaryStage.setMaximized(true); 
        primaryStage.setFullScreen(true); 
        primaryStage.setFullScreenExitHint("");
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Initial Render
        renderRegisteredTable();
        refreshAvailableSidebar(); 
    }

    // --- Data Initialization 
    private void initializeDatabase() {
        // Load subjects section 
        List<Section> allDatabase = new ArrayList<>();
        SectionDAO section = new SectionDAO();
        allDatabase = section.getAllSections();
    }

    private VBox createMainLayout() {
        VBox mainContainer = new VBox(25);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setStyle("-fx-background-color: #ffffff;");
        
        // Registered Section
        VBox registeredSection = createSectionContainer("Registered Subjects");
        registeredTable = new GridPane();
        registeredTable.setVgap(10);
        
        totalCreditsLabel = new Label("Total Credits: 0.0");
        totalCreditsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + BLUE_PRIMARY + ";");
        
        HBox footer = new HBox(totalCreditsLabel);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10, 0, 0, 0));
        registeredSection.getChildren().addAll(registeredTable, footer);

        // Lower Layout
        HBox lowerLayout = new HBox(25);
        lowerLayout.setAlignment(Pos.TOP_LEFT);

        VBox timetableSection = createSectionContainer("My Timetable");
        timetableGrid = createTimetableGrid();
        timetableSection.getChildren().add(timetableGrid);
        HBox.setHgrow(timetableSection, Priority.ALWAYS);

        VBox availableSection = createSectionContainer("Available Subjects");
        availableSection.setMinWidth(400); 
        availableSection.setMaxWidth(400);
        availableSection.getChildren().add(createAvailableSubjectsSidebar());

        lowerLayout.getChildren().addAll(timetableSection, availableSection);
        mainContainer.getChildren().addAll(registeredSection, lowerLayout);

        return mainContainer;
    }

    private int getDayCol(String d) {
        // Trim and upper case to handle "T " or "th"
        String day = d.trim().toUpperCase();
        return switch (day) {
            case "M" -> 1;
            case "T" -> 2;
            case "W" -> 3;
            case "TH" -> 4;
            case "F" -> 5;
            default -> -1; // Return -1 so we can skip invalid days instead of forcing them to Monday
        };
    }

    private void parseAndAddTimetableBlocks(String courseCode, String schedule) {
        if (schedule == null || !schedule.contains(" ")) return;

        try {
            String[] parts = schedule.split("\\s+");
            String daysPart = parts[0];
            String timePart = parts[1];

            String[] times = timePart.split("-");
            
            // Start Time
            int startH = Integer.parseInt(times[0].substring(0, 2));
            int startM = Integer.parseInt(times[0].substring(2));
            
            // End Time
            int endH = Integer.parseInt(times[1].substring(0, 2));
            int endM = Integer.parseInt(times[1].substring(2));

            // --- NEW 30-MIN MATH ---
            // Calculate start row: (Hour-8)*2. If minutes >= 30, add 1.
            int startRowIndex = ((startH - 8) * 2) + (startM >= 30 ? 1 : 0) + 1;

            // Calculate total duration in minutes
            int totalMinutes = ((endH * 60) + endM) - ((startH * 60) + startM);
            
            // Calculate row span: duration divided by 30, rounded up
            // e.g., 80 mins / 30 = 2.66 -> 3 blocks
            int rowSpan = (int) Math.ceil(totalMinutes / 30.0);

            String[] dayArray = daysPart.split("-");
            for (String d : dayArray) {
                int col = getDayCol(d);
                if (col != -1) {
                    addTimetableBlock(courseCode, col, startRowIndex, rowSpan);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing schedule: " + schedule);
        }
    }

    // --- Core Logic: Rendering the Registered Table (FIX #1) ---
    // Instead of removing rows by index, we clear and rebuild. This ensures stability.
    private void renderRegisteredTable() {
        registeredTable.getChildren().clear();
        registeredTable.getColumnConstraints().clear();
        refreshTimetableVisuals();

        PreRegistrationDAO preDAO = new PreRegistrationDAO();
        CourseDAO courseDAO = new CourseDAO(); // Added to fetch course names
        myRegistered = preDAO.getCurrentPreRegistrations(studentId);
        
        // Column Setup (kept the same)
        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(15);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(10);
        ColumnConstraints c3 = new ColumnConstraints(); c3.setPercentWidth(40);
        ColumnConstraints c4 = new ColumnConstraints(); c4.setPercentWidth(15);
        ColumnConstraints c5 = new ColumnConstraints(); c5.setPercentWidth(20);
        registeredTable.getColumnConstraints().addAll(c1, c2, c3, c4, c5);

        // Header Row (kept the same)
        String[] heads = {"Subject", "Sec", "Title", "Credits", "Action"};
        for (int i = 0; i < heads.length; i++) {
            Label h = new Label(heads[i]); 
            h.setStyle("-fx-font-weight: bold; -fx-text-fill: #888; -fx-padding: 5;");
            registeredTable.add(h, i, 0);
        }

        int row = 1;
        SectionDAO sectionDAO = new SectionDAO();

        for (PreRegistration sub : myRegistered) {
            // --- FETCH ACTUAL COURSE DATA ---
            Course courseData = courseDAO.getCourseByCode(sub.getCourseCode());
            String courseTitle = (courseData != null) ? courseData.getCourse_name() : sub.getCourseCode();
            String creditText = (courseData != null) ? courseData.getCredit_hour() + " Chr" : "0 Chr";

            Label codeLbl = new Label(sub.getCourseCode());
            Label secLbl = new Label(String.valueOf(sub.getSectionId()));
            Label titleLbl = new Label(courseTitle); // Fix: Now shows Course Name
            Label credLbl = new Label(creditText);   // Fix: Now shows correct hours + "Chr"
            
            String rowStyle = "-fx-padding: 8; -fx-border-color: #f4f4f4; -fx-border-width: 0 0 1 0;";
            codeLbl.setStyle(rowStyle + "-fx-font-weight: bold;");
            secLbl.setStyle(rowStyle);
            titleLbl.setStyle(rowStyle);
            credLbl.setStyle(rowStyle);

            Button dropBtn = new Button("Drop");
            dropBtn.setStyle("-fx-background-color: " + ERROR_RED + "; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11px;");
            dropBtn.setMinWidth(80);
            dropBtn.setOnAction(e -> confirmDropDialog(sub));

            registeredTable.add(codeLbl, 0, row);
            registeredTable.add(secLbl, 1, row);
            registeredTable.add(titleLbl, 2, row);
            registeredTable.add(credLbl, 3, row);
            registeredTable.add(dropBtn, 4, row);

            // Timetable Rendering (logic you updated previously)
            List<Section> sections = sectionDAO.getSectionsByCourse(sub.getCourseCode());
            if (sections != null) {
                for (Section s : sections) {
                    if (s.getSectionId() == sub.getSectionId() && s.getSchedule() != null) {
                        parseAndAddTimetableBlocks(sub.getCourseCode(), s.getSchedule());
                        break;
                    }
                }
            }
            row++;
        }

        totalCredits = preDAO.getTotalRegisteredCredits(studentId);
        totalCreditsLabel.setText("Total Credits: " + totalCredits + " Chr"); // Updated footer suffix
    }

    private void refreshTimetableVisuals() {
        // Remove only the colored blocks (keeping headers and empty cells)
        timetableGrid.getChildren().removeIf(node -> node instanceof VBox);
    }

    private void addTimetableBlock(String code, int col, int row, int rowSpan) {
        VBox block = new VBox();
        
        // RESTORE FULL WIDTH:
        block.setMaxWidth(Double.MAX_VALUE); 
        GridPane.setHgrow(block, Priority.ALWAYS);
        
        block.setStyle("-fx-background-color: #007bff; " +
                    "-fx-background-radius: 4; " +
                    "-fx-border-color: white; " +
                    "-fx-border-width: 1; " +
                    "-fx-padding: 2;");
        block.setAlignment(Pos.CENTER);
        
        Label l = new Label(code);
        l.setTextFill(Color.WHITE);
        l.setFont(Font.font("System", FontWeight.BOLD, 9)); // Slightly smaller font for 30m rows
        l.setWrapText(true);
        
        block.getChildren().add(l);

        javafx.application.Platform.runLater(() -> {
            // Use the overload: add(Node, col, row, colSpan, rowSpan)
            timetableGrid.add(block, col, row, 1, rowSpan);
            
            // Final layout tweaks to prevent shrinking
            GridPane.setMargin(block, new Insets(1, 2, 1, 2));
        });
    }

    // --- Search & Popup Logic ---

    public static class SectionModel {
        private final SimpleStringProperty course, section, time, campus;
        private final SimpleIntegerProperty vacancy;
        Section fullData; // Reference to the backend model

        public SectionModel(Section data) {
            CourseDAO courseDAO = new CourseDAO();
            Course courseObj = courseDAO.getCourseByCode(data.getCourseCode());
            
            this.course = new SimpleStringProperty(data.getCourseCode() + " - " + 
                        (courseObj != null ? courseObj.getCourse_name() : "Unknown"));
            this.section = new SimpleStringProperty(data.getSectionCode());
            this.vacancy = new SimpleIntegerProperty(30 - data.getCurrCapacity());
            this.time = new SimpleStringProperty(data.getSchedule());
            this.campus = new SimpleStringProperty(data.getVenue()); // Using venue as campus
            this.fullData = data;
        }
    }
    
    // attempt registration logic
    private void attemptRegistration(Section section) {
        RegistrationService service = new RegistrationService();

        // Call the service which handles all the IIUM logic (Prereqs, Credits, Clashes)
        int result = service.addCourse(studentId, section.getSectionId(), section.getCourseCode());

        switch (result) {
            case 0 -> {
                showStyledAlert("SUCCESS", "Registered", "Successfully registered for " + section.getCourseCode());
                // Refresh UI to show the new course in the table and timetable
                renderRegisteredTable(); 
                refreshAvailableSidebar();
            }
            case 102 -> showStyledAlert("ERROR", "Prerequisite Not Met", "You haven't completed the required courses for this subject.");
            case 104 -> showStyledAlert("ERROR", "Credit Limit", "Maximum credit limit (20) reached.");
            case 106 -> showStyledAlert("ERROR", "Schedule Clash", "This course conflicts with your current timetable.");
            case 500 -> showStyledAlert("ERROR", "Database Error", "Unable to save to database. Please try again.");
            default -> showStyledAlert("ERROR", "Registration Failed", "Error Code: " + result);
        }
    }

    private void openSectionSelectionPopup(String query) {
        toggleOverlay(true);
        Stage popup = new Stage(StageStyle.TRANSPARENT);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initOwner(rootStack.getScene().getWindow());

        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 20, 0, 0, 0);");
        box.setMinWidth(750);

        Label header = new Label("Search Results for \"" + query.toUpperCase() + "\"");
        header.setFont(Font.font("System", FontWeight.BOLD, 18));
        
        Label hint = new Label("Tip: Double-click a row to register for that section.");
        hint.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");

        TableView<SectionModel> table = new TableView<>();
        table.getStyleClass().add("popup-table");
        
        // --- Define Columns (Simplified) ---
        TableColumn<SectionModel, String> colCourse = new TableColumn<>("Course");
        colCourse.setCellValueFactory(d -> d.getValue().course);
        colCourse.setPrefWidth(250);
        
        TableColumn<SectionModel, String> colSec = new TableColumn<>("Section");
        colSec.setCellValueFactory(d -> d.getValue().section);
        colSec.setPrefWidth(100);

        TableColumn<SectionModel, Number> colVac = new TableColumn<>("Vacancy");
        colVac.setCellValueFactory(d -> d.getValue().vacancy);

        TableColumn<SectionModel, String> colTime = new TableColumn<>("Time/Schedule");
        colTime.setCellValueFactory(d -> d.getValue().time);
        colTime.setPrefWidth(200);

        table.getColumns().setAll(colCourse, colSec, colVac, colTime);

        // --- Row Factory for Double-Click Selection ---
        table.setRowFactory(tv -> {
            TableRow<SectionModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    SectionModel rowData = row.getItem();
                    popup.close();
                    toggleOverlay(false);
                    attemptRegistration(rowData.fullData);
                }
            });
            row.setCursor(Cursor.HAND); // Changes mouse to pointer to show it's clickable
            return row;
        });

        // --- Database Fetching ---
        ObservableList<SectionModel> dataList = FXCollections.observableArrayList();
        CourseDAO courseDAO = new CourseDAO();
        SectionDAO sectionDAO = new SectionDAO();
        
        Course foundCourse = courseDAO.getCourseByCode(query.trim().toUpperCase());

        if (foundCourse != null) {
            List<Section> sections = sectionDAO.getSectionsByCourse(foundCourse.getCourseCode());
            if (sections != null && !sections.isEmpty()) {
                for (Section s : sections) {
                    dataList.add(new SectionModel(s));
                }
            } else {
                table.setPlaceholder(new Label("No sections found for " + foundCourse.getCourseCode()));
            }
        } else {
            table.setPlaceholder(new Label("No course found with code: " + query.toUpperCase()));
        }

        table.setItems(dataList);
        table.setPrefHeight(300);

        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-background-color: #eee; -fx-padding: 8 20; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> { 
            popup.close(); 
            toggleOverlay(false); 
        });

        HBox footer = new HBox(closeBtn);
        footer.setAlignment(Pos.CENTER_RIGHT);
        
        box.getChildren().addAll(header, hint, table, footer);
        
        Scene s = new Scene(box);
        s.setFill(Color.TRANSPARENT);
        popup.setScene(s);
        popup.show();
    }


    // --- Dropping Logic ---

    // Fix: Use PreRegistration model directly
    private void confirmDropDialog(PreRegistration sub) {
        toggleOverlay(true);
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(rootStack.getScene().getWindow());

        VBox box = new VBox(20); 
        box.setPadding(new Insets(30)); 
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #ddd; -fx-border-radius: 15;");
        
        Label title = new Label("Confirm Drop");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");
        
        // Fix: Dynamic message using the course code
        Label msg = new Label("Are you sure you want to drop " + sub.getCourseCode() + "?"); 
        
        HBox btns = new HBox(20); btns.setAlignment(Pos.CENTER);
        Button cancel = new Button("Cancel"); 
        cancel.setStyle("-fx-background-color: #eee; -fx-min-width: 100; -fx-cursor: hand;");
        cancel.setOnAction(e -> { stage.close(); toggleOverlay(false); });
        
        Button drop = new Button("Drop"); 
        drop.setStyle("-fx-background-color: " + ERROR_RED + "; -fx-text-fill: white; -fx-min-width: 100; -fx-cursor: hand;");
        
        drop.setOnAction(e -> {
            PreRegistrationDAO preDAO = new PreRegistrationDAO();
            
            // Fix: Call the DAO to delete from the actual database
            boolean success = preDAO.removePreRegistration(
                sub.getStudentId(), 
                sub.getCourseCode()
            );

            if (success) {
                stage.close();
                // Re-render the UI to reflect changes
                renderRegisteredTable();
                refreshAvailableSidebar(); 
                showStyledAlert("SUCCESS", "Dropped", sub.getCourseCode() + " has been removed.");
            } else {
                showStyledAlert("ERROR", "Drop Failed", "Could not remove the course from the database.");
            }
        });
        
        btns.getChildren().addAll(cancel, drop);
        box.getChildren().addAll(title, msg, btns);
        stage.setScene(new Scene(box)); 
        stage.show();
    }

    // --- Sidebar & Filter Logic ---

    private void addSubjectCardFromCourse(Course c) {
        VBox card = new VBox(6); 
        card.getStyleClass().add("card"); 
        card.setPadding(new Insets(15));
        
        HBox header = new HBox();
        Label code = new Label(c.getCourseCode()); 
        code.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        
        Region spacer = new Region(); 
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label cr = new Label(c.getCredit_hour() + " Cr"); 
        cr.setStyle("-fx-text-fill: " + BLUE_PRIMARY + "; -fx-font-weight: bold;");
        
        header.getChildren().addAll(code, spacer, cr);

        Label title = new Label(c.getCourse_name()); 
        title.setStyle("-fx-text-fill: #333; -fx-font-weight: bold;");
        title.setWrapText(true);
        
        Button addBtn = new Button("View Sections"); 
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setStyle("-fx-background-color: " + BLUE_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        
        // When clicked, open the popup we made earlier to choose a section
        addBtn.setOnAction(e -> openSectionSelectionPopup(c.getCourseCode()));
        
        card.getChildren().addAll(header, title, new Separator(), addBtn);
        subjectsListContainer.getChildren().add(card);
    }

    private VBox createAvailableSubjectsSidebar() {
        VBox sidebar = new VBox(12);
        VBox searchContainer = new VBox(10);
        searchContainer.setPadding(new Insets(15));
        searchContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #eee;");

        searchField = new TextField(); 
        searchField.setPromptText("Try BICS 2304...");
        
        Button searchBtn = new Button("Search Course Code"); 
        searchBtn.getStyleClass().add("search-button"); 
        searchBtn.setMaxWidth(Double.MAX_VALUE);
        searchBtn.setOnAction(e -> {
            if(!searchField.getText().isEmpty()) openSectionSelectionPopup(searchField.getText());
        });
        
        coreBtn = new Button("Core Subjects"); coreBtn.setMaxWidth(Double.MAX_VALUE);
        electiveBtn = new Button("Elective Subjects"); electiveBtn.setMaxWidth(Double.MAX_VALUE);
        coreBtn.setCursor(Cursor.HAND); electiveBtn.setCursor(Cursor.HAND);
        
        coreBtn.setOnAction(e -> { currentCategory = "CORE"; refreshAvailableSidebar(); });
        electiveBtn.setOnAction(e -> { currentCategory = "ELECTIVE"; refreshAvailableSidebar(); });

        searchContainer.getChildren().addAll(searchField, searchBtn, coreBtn, electiveBtn);

        subjectsListContainer = new VBox(12);
        
        ScrollPane scroll = new ScrollPane(subjectsListContainer);
        scroll.setFitToWidth(true); 
        scroll.setPrefHeight(450);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        sidebar.getChildren().addAll(searchContainer, scroll);
        return sidebar;
    }

    private void refreshAvailableSidebar() {
        subjectsListContainer.getChildren().clear();
        updateCategoryButtonStyles(); 

        CourseDAO courseDAO = new CourseDAO();
        List<Course> allCourses = courseDAO.getAllCourses();

        if (allCourses == null || allCourses.isEmpty()) {
            subjectsListContainer.getChildren().add(new Label("No courses found."));
            return;
        }

        List<Course> filtered = allCourses.stream()
            .filter(c -> {
                // FIX: Use your category column from the DB
                // Assuming the column in DB is called 'category' or 'cat'
                return c.getCat().equalsIgnoreCase(currentCategory); 
            })
            .filter(c -> myRegistered.stream()
                .noneMatch(reg -> reg.getCourseCode().equalsIgnoreCase(c.getCourseCode())))
            .collect(Collectors.toList());

        for (Course c : filtered) {
            addSubjectCard(c); 
        }
    }

    // FIX #4: Active = Blue, Inactive = Grey
    private void updateCategoryButtonStyles() {
        if (currentCategory.equals("CORE")) {
            coreBtn.setStyle("-fx-background-color: " + BLUE_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold;");
            electiveBtn.setStyle("-fx-background-color: " + GREY_INACTIVE + "; -fx-text-fill: black; -fx-font-weight: bold;");
        } else {
            electiveBtn.setStyle("-fx-background-color: " + BLUE_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold;");
            coreBtn.setStyle("-fx-background-color: " + GREY_INACTIVE + "; -fx-text-fill: black; -fx-font-weight: bold;");
        }
    }

    // Added Time and Lecturer details to card
    private void addSubjectCard(Course c) {
        VBox card = new VBox(6); 
        card.getStyleClass().add("card"); 
        card.setPadding(new Insets(15));
        
        HBox header = new HBox();
        Label codeLabel = new Label(c.getCourseCode()); 
        codeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        
        Region spacer = new Region(); 
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // FIX: Change "Cr" to "Chr"
        Label creditLabel = new Label(c.getCredit_hour() + " Chr"); 
        creditLabel.setStyle("-fx-text-fill: " + BLUE_PRIMARY + "; -fx-font-weight: bold;");
        
        header.getChildren().addAll(codeLabel, spacer, creditLabel);

        // Body: Course Title
        Label titleLabel = new Label(c.getCourse_name()); 
        titleLabel.setStyle("-fx-text-fill: #333; -fx-font-weight: bold;");
        titleLabel.setWrapText(true);
        titleLabel.setMinHeight(40); // Ensures alignment if titles are long

        // Footer: Action Button
        Button viewSectionsBtn = new Button("View Available Sections"); 
        viewSectionsBtn.setMaxWidth(Double.MAX_VALUE);
        viewSectionsBtn.setStyle("-fx-background-color: " + BLUE_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        
        // ACTION: Open the popup to choose specific sections for THIS course
        viewSectionsBtn.setOnAction(e -> openSectionSelectionPopup(c.getCourseCode()));
        
        card.getChildren().addAll(header, titleLabel, new Separator(), viewSectionsBtn);
        subjectsListContainer.getChildren().add(card);
    }

    // --- Helpers ---

    private VBox createSectionContainer(String title) {
        VBox v = new VBox(15, new Label(title) {{ setStyle("-fx-font-size: 18; -fx-font-weight: bold;"); }});
        v.setPadding(new Insets(20)); v.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #eee; -fx-border-radius: 12;");
        return v;
    }

    private GridPane createTimetableGrid() {
        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: white; -fx-padding: 0;");
        grid.setHgap(0); // Set gap to 0 so borders handle the lines
        grid.setVgap(0);
        
        // 1. CLEAR AND SET COLUMN CONSTRAINTS FIRST
        grid.getColumnConstraints().clear();
        String[] days = {"Time", "Mon", "Tue", "Wed", "Thu", "Fri"};
        
        for (int i = 0; i < 6; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            if (i == 0) {
                cc.setPercentWidth(15); // Time Column
            } else {
                cc.setPercentWidth(17); // Day Columns
            }
            cc.setHgrow(Priority.ALWAYS); // Force columns to expand
            grid.getColumnConstraints().add(cc);
            
            // 2. Add Header at Row 0
            Label l = new Label(days[i]);
            l.setStyle("-fx-font-weight:bold; -fx-padding: 10; -fx-background-color: #f8f9fa; -fx-border-color: #dee2e6;");
            l.setMaxWidth(Double.MAX_VALUE); 
            l.setAlignment(Pos.CENTER);
            grid.add(l, i, 0);
        }
        
        // 3. Time Rows (30-minute intervals) starting from Row 1
        int currentRow = 1;
        for (int h = 8; h <= 21; h++) { 
            for (int min : new int[]{0, 30}) {
                String startTime = String.format("%02d:%02d", h, min);
                
                Label timeLabel = new Label(startTime);
                timeLabel.setStyle("-fx-padding: 5; -fx-background-color: #fdfdfd; -fx-border-color: #f0f0f0; -fx-font-size: 10px;");
                timeLabel.setMaxWidth(Double.MAX_VALUE);
                timeLabel.setAlignment(Pos.CENTER);
                
                grid.add(timeLabel, 0, currentRow);
                
                // Add empty "ghost" cells to keep grid lines consistent
                for (int c = 1; c < 6; c++) {
                    StackPane cell = new StackPane();
                    cell.setStyle("-fx-border-color: #f0f0f0; -fx-min-height: 25;");
                    grid.add(cell, c, currentRow);
                }
                currentRow++;
            }
        }
        return grid;
    }

    private void toggleOverlay(boolean show) {
        overlayEffect.setVisible(show);
        overlayEffect.toFront();
    }

    private void showStyledAlert(String type, String title, String msg) {
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(rootStack.getScene().getWindow());

        VBox box = new VBox(15);
        box.setPadding(new Insets(25));
        box.setMinWidth(350);
        box.setAlignment(Pos.CENTER_LEFT);
        
        String color = type.equals("SUCCESS") ? SUCCESS_GREEN : ERROR_RED;
        box.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                     "-fx-border-color: " + color + "; -fx-border-width: 2; -fx-border-radius: 12; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 0, 5);");
        
        Label t = new Label(title);
        t.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: " + color + ";");
        Label m = new Label(msg);
        m.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        m.setWrapText(true);
        
        Button ok = new Button("Close");
        ok.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-cursor: hand;");
        ok.setOnAction(e -> { stage.close(); toggleOverlay(false); });
        
        HBox buttonContainer = new HBox(ok);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        box.getChildren().addAll(t, m, buttonContainer);
        
        Scene alertScene = new Scene(box); alertScene.setFill(Color.TRANSPARENT);
        stage.setScene(alertScene); stage.show();
    }

    // Comment for now
    //private int getDayCol(String d) { return switch(d) { case "Monday" -> 1; case "Tuesday" -> 2; case "Wednesday" -> 3; case "Thursday" -> 4; case "Friday" -> 5; default -> 1; }; }
    
    public static void main(String[] args) { launch(args); }
}