package pages;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Cursor;
import pages.LoginPage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PreRegistrationPage extends Application {

    LoginPage loginPage = new LoginPage();

    // --- Data Models ---
    // Simple class to hold subject data for the sidebar/database
    static class SubjectData {
        String code, title, lecturer, timeStr;
        double credits;
        List<String> days;
        int startHour, endHour;
        String category; // "CORE" or "ELECTIVE"

        public SubjectData(String code, String title, String lecturer, String timeStr, double credits, 
                           List<String> days, int start, int end, String cat) {
            this.code = code; this.title = title; this.lecturer = lecturer;
            this.timeStr = timeStr; this.credits = credits;
            this.days = days; this.startHour = start; this.endHour = end;
            this.category = cat;
        }
    }

    // Class to track what the user has actually registered
    static class RegisteredSubject {
        SubjectData data;
        public RegisteredSubject(SubjectData data) { this.data = data; }
    }

    // --- State Management ---
    private List<SubjectData> allDatabase = new ArrayList<>(); // All possible subjects
    private List<RegisteredSubject> myRegistered = new ArrayList<>(); // Currently registered
    private Map<String, String> scheduleMap = new HashMap<>(); // For collision detection "Monday 10:00" -> "Code"
    
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
        initializeMockDatabase(); // Load Data (ADDITION #1)

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
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Initial Render
        renderRegisteredTable();
        refreshAvailableSidebar(); 
    }

    // --- Data Initialization (ADDITION #1) ---
    private void initializeMockDatabase() {
        List<String> mw = new ArrayList<>(); mw.add("Monday"); mw.add("Wednesday");
        List<String> tth = new ArrayList<>(); tth.add("Tuesday"); tth.add("Thursday");
        List<String> fri = new ArrayList<>(); fri.add("Friday");

        // CORE
        allDatabase.add(new SubjectData("BICS 2304", "Computer Architecture", "Dr. Ahmad", "M-W 9:00 - 10:20", 3.0, mw, 9, 11, "CORE"));
        allDatabase.add(new SubjectData("BICS 2305", "Operating Systems", "Prof. Sarah", "T-TH 11:00 - 12:20", 3.0, tth, 11, 13, "CORE"));
        allDatabase.add(new SubjectData("BICS 1305", "Programming I", "Dr. Ali", "Fri 8:00 - 11:00", 3.0, fri, 8, 11, "CORE"));
        allDatabase.add(new SubjectData("MATH 1101", "Discrete Math", "Dr. Wong", "T-TH 14:00 - 15:20", 3.0, tth, 14, 16, "CORE"));
        
        // ELECTIVE
        allDatabase.add(new SubjectData("CCPS 1010", "Photography Skills", "Mr. Tan", "Fri 15:00 - 17:00", 1.0, fri, 15, 17, "ELECTIVE"));
        allDatabase.add(new SubjectData("MGT 2020", "Intro to Management", "Dr. Phil", "M-W 14:00 - 15:20", 2.0, mw, 14, 16, "ELECTIVE"));
        allDatabase.add(new SubjectData("LANG 1001", "Mandarin Level 1", "Ms. Lim", "T-TH 08:30 - 09:50", 1.0, tth, 8, 10, "ELECTIVE"));
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

    // --- Core Logic: Rendering the Registered Table (FIX #1) ---
    // Instead of removing rows by index, we clear and rebuild. This ensures stability.
    private void renderRegisteredTable() {
        registeredTable.getChildren().clear();
        registeredTable.getColumnConstraints().clear();
        
        // Setup Columns
        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(20);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(10);
        ColumnConstraints c3 = new ColumnConstraints(); c3.setPercentWidth(40);
        ColumnConstraints c4 = new ColumnConstraints(); c4.setPercentWidth(10);
        ColumnConstraints c5 = new ColumnConstraints(); c5.setPercentWidth(20);
        registeredTable.getColumnConstraints().addAll(c1, c2, c3, c4, c5);

        // Header
        String[] heads = {"Subject", "Sec", "Title", "Credits", "Action"};
        for (int i = 0; i < heads.length; i++) {
            Label h = new Label(heads[i]); h.setStyle("-fx-font-weight: bold; -fx-text-fill: #888;");
            registeredTable.add(h, i, 0);
        }

        // Rows
        int row = 1;
        totalCredits = 0.0;
        
        // Re-calculate Schedule Map based on what is currently in 'myRegistered'
        scheduleMap.clear();
        // Clear Timetable visuals
        refreshTimetableVisuals();

        for (RegisteredSubject sub : myRegistered) {
            registeredTable.add(new Label(sub.data.code), 0, row);
            registeredTable.add(new Label("1"), 1, row);
            registeredTable.add(new Label(sub.data.title), 2, row);
            registeredTable.add(new Label(String.valueOf(sub.data.credits)), 3, row);

            Button dropBtn = new Button("Drop");
            dropBtn.setStyle("-fx-background-color: " + ERROR_RED + "; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11px;");
            dropBtn.setOnAction(e -> confirmDropDialog(sub));
            registeredTable.add(dropBtn, 4, row);
            
            totalCredits += sub.data.credits;
            
            // Re-populate collision map
            for(String d : sub.data.days) {
                for(int h = sub.data.startHour; h < sub.data.endHour; h++) {
                    scheduleMap.put(d + " " + h + ":00", sub.data.code);
                    addTimetableBlock(sub.data.code, d, h);
                }
            }
            row++;
        }
        totalCreditsLabel.setText("Total Credits: " + totalCredits);
    }

    private void refreshTimetableVisuals() {
        // Remove only the colored blocks (keeping headers and empty cells)
        timetableGrid.getChildren().removeIf(node -> node instanceof VBox);
    }

    private void addTimetableBlock(String code, String day, int hour) {
        VBox block = new VBox(2);
        block.setStyle("-fx-background-color: " + BLUE_PRIMARY + "; -fx-background-radius: 4; -fx-padding: 2;");
        block.setAlignment(Pos.CENTER);
        Label l = new Label(code); 
        l.setTextFill(Color.WHITE); 
        l.setFont(Font.font("System", FontWeight.BOLD, 10));
        block.getChildren().add(l);
        timetableGrid.add(block, getDayCol(day), hour - 7);
    }

    // --- Search & Popup Logic ---

    public static class SectionModel {
        private final SimpleStringProperty course, section, time, campus;
        private final SimpleIntegerProperty vacancy;
        // Hidden info to pass to registration
        SubjectData fullData;

        public SectionModel(SubjectData data, String sec, int vac, String camp) {
            this.course = new SimpleStringProperty(data.code);
            this.section = new SimpleStringProperty(sec);
            this.vacancy = new SimpleIntegerProperty(vac);
            this.time = new SimpleStringProperty(data.timeStr);
            this.campus = new SimpleStringProperty(camp);
            this.fullData = data;
        }
        public String getCourse() { return course.get(); }
        public String getSection() { return section.get(); }
        public int getVacancy() { return vacancy.get(); }
        public String getTime() { return time.get(); }
        public String getCampus() { return campus.get(); }
    }

    private void openSectionSelectionPopup(String query) {
        toggleOverlay(true);
        Stage popup = new Stage(StageStyle.TRANSPARENT);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initOwner(rootStack.getScene().getWindow());

        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 20, 0, 0, 0);");
        box.setMinWidth(700);

        Label header = new Label("Search Results for \"" + query + "\"");
        header.setFont(Font.font("System", FontWeight.BOLD, 18));

        TableView<SectionModel> table = new TableView<>();
        table.getStyleClass().add("popup-table"); // (FIX #6)
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Define Columns
        TableColumn<SectionModel, String> colCourse = new TableColumn<>("Course");
        colCourse.setCellValueFactory(d -> d.getValue().course);
        
        TableColumn<SectionModel, String> colSec = new TableColumn<>("Section");
        colSec.setCellValueFactory(d -> d.getValue().section);
        colSec.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); } 
                else {
                    Label l = new Label(item);
                    l.getStyleClass().add("section-link");
                    l.setOnMouseClicked(e -> {
                        SectionModel row = getTableView().getItems().get(getIndex());
                        popup.close();
                        attemptRegistration(row.fullData);
                    });
                    setGraphic(l);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<SectionModel, Number> colVac = new TableColumn<>("Vacancy");
        colVac.setCellValueFactory(d -> d.getValue().vacancy);
        TableColumn<SectionModel, String> colTime = new TableColumn<>("Time");
        colTime.setCellValueFactory(d -> d.getValue().time);
        TableColumn<SectionModel, String> colCampus = new TableColumn<>("Campus");
        colCampus.setCellValueFactory(d -> d.getValue().campus);

        table.getColumns().addAll(colCourse, colSec, colVac, colTime, colCampus);

        // Mock Search Data (ADDITION #2)
        ObservableList<SectionModel> data = FXCollections.observableArrayList();
        
        // Generate some fake search results based on the query or defaults
        List<String> tth = new ArrayList<>(); tth.add("Tuesday"); tth.add("Thursday");
        
        data.add(new SectionModel(new SubjectData("BICS 1305", "Programming I", "Dr. Ali", "T-TH 8.30 - 10.00", 3.0, tth, 8, 10, "CORE"), "1", 15, "Gombak"));
        data.add(new SectionModel(new SubjectData("BICS 1305", "Programming I", "Dr. Ali", "T-TH 10.00 - 11.30", 3.0, tth, 10, 12, "CORE"), "2", 5, "Gombak"));
        data.add(new SectionModel(new SubjectData("INFO 4000", "Final Project", "Dr. Zaki", "Fri 10.00 - 12.00", 4.0, List.of("Friday"), 10, 12, "CORE"), "1", 30, "Gombak"));
        
        table.setItems(data);
        table.setPrefHeight(250);
        // Force white background on table (FIX #6)
        table.setStyle("-fx-background-color: white; -fx-control-inner-background: white;");

        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-background-color: #eee; -fx-text-fill: black;");
        closeBtn.setOnAction(e -> { popup.close(); toggleOverlay(false); });

        box.getChildren().addAll(header, table, closeBtn);
        Scene s = new Scene(box); s.setFill(Color.TRANSPARENT);
        if(rootStack.getScene() != null) s.getStylesheets().addAll(rootStack.getScene().getStylesheets());
        popup.setScene(s);
        popup.show();
    }

    // --- Registration Logic ---

    private void attemptRegistration(SubjectData data) {
        // 1. Check Duplicates (FIX #2)
        boolean alreadyReg = myRegistered.stream().anyMatch(s -> s.data.code.equals(data.code));
        if (alreadyReg) {
             toggleOverlay(true);
             showStyledAlert("ERROR", "Duplicate Subject", "You are already registered for " + data.code);
             return;
        }

        // 2. Check Clashes
        for(String d : data.days) {
            if(hasClash(d, data.startHour, data.endHour)) {
                toggleOverlay(true);
                showStyledAlert("ERROR", "Time Clash", "Conflict on " + d + " between " + data.startHour + ":00 and " + data.endHour + ":00");
                return;
            }
        }

        // 3. Success
        myRegistered.add(new RegisteredSubject(data));
        renderRegisteredTable(); // Re-draw table
        refreshAvailableSidebar(); // Hide from sidebar (FIX #5)
        
        toggleOverlay(true);
        showStyledAlert("SUCCESS", "Registered", data.code + " has been added successfully.");
    }

    private boolean hasClash(String day, int start, int end) {
        for (int i = start; i < end; i++) {
            if (scheduleMap.containsKey(day + " " + i + ":00")) return true;
        }
        return false;
    }

    // --- Dropping Logic ---

    private void confirmDropDialog(RegisteredSubject sub) {
        toggleOverlay(true);
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(rootStack.getScene().getWindow());

        VBox box = new VBox(20); box.setPadding(new Insets(30)); box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #ddd; -fx-border-radius: 15;");
        
        Label title = new Label("Confirm Drop");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");
        Label msg = new Label("Are you sure you want to drop " + sub.data.code + "?");
        
        HBox btns = new HBox(20); btns.setAlignment(Pos.CENTER);
        Button cancel = new Button("Cancel"); 
        cancel.setStyle("-fx-background-color: #eee; -fx-min-width: 100; -fx-cursor: hand;");
        cancel.setOnAction(e -> { stage.close(); toggleOverlay(false); });
        
        Button drop = new Button("Drop"); 
        drop.setStyle("-fx-background-color: " + ERROR_RED + "; -fx-text-fill: white; -fx-min-width: 100; -fx-cursor: hand;");
        
        drop.setOnAction(e -> {
            // FIX #1: Remove from model, then re-render everything
            myRegistered.remove(sub);
            renderRegisteredTable();
            refreshAvailableSidebar(); // Subject reappears in sidebar (FIX #5)
            stage.close();
            showStyledAlert("SUCCESS", "Dropped", sub.data.code + " removed.");
        });
        
        btns.getChildren().addAll(cancel, drop);
        box.getChildren().addAll(title, msg, btns);
        stage.setScene(new Scene(box)); stage.show();
    }

    // --- Sidebar & Filter Logic ---

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
        updateCategoryButtonStyles(); // (FIX #4)

        // Filter based on Category AND if already registered (FIX #5)
        List<SubjectData> filtered = allDatabase.stream()
            .filter(s -> s.category.equals(currentCategory))
            .filter(s -> myRegistered.stream().noneMatch(reg -> reg.data.code.equals(s.code)))
            .collect(Collectors.toList());

        for (SubjectData s : filtered) {
            addSubjectCard(s);
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

    // FIX #3: Added Time and Lecturer details to card
    private void addSubjectCard(SubjectData s) {
        VBox card = new VBox(6); card.getStyleClass().add("card"); card.setPadding(new Insets(15));
        
        HBox header = new HBox();
        Label c = new Label(s.code); c.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Label cr = new Label(s.credits + " Cr"); cr.setStyle("-fx-text-fill: " + BLUE_PRIMARY + "; -fx-font-weight: bold;");
        header.getChildren().addAll(c, spacer, cr);

        Label t = new Label(s.title); t.setStyle("-fx-text-fill: #333; -fx-font-weight: bold;");
        
        // Added details
        Label lec = new Label("Lec: " + s.lecturer); lec.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");
        Label time = new Label("Time: " + s.timeStr); time.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");
        
        Button add = new Button("+ Add Subject"); add.setMaxWidth(Double.MAX_VALUE);
        add.setStyle("-fx-background-color: " + BLUE_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        add.setOnAction(e -> attemptRegistration(s));
        
        card.getChildren().addAll(header, t, lec, time, new Separator(), add);
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
        grid.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);");
        grid.setHgap(1); grid.setVgap(1);
        String[] days = {"Time", "Mon", "Tue", "Wed", "Thu", "Fri"};
        
        for (int i = 0; i < 6; i++) {
            Label l = new Label(days[i]);
            l.setStyle("-fx-font-weight:bold; -fx-padding: 10; -fx-background-color: #f8f9fa; -fx-border-color: #dee2e6;");
            l.setMaxWidth(Double.MAX_VALUE); l.setAlignment(Pos.CENTER);
            grid.add(l, i, 0);
            ColumnConstraints cc = new ColumnConstraints(); cc.setPercentWidth(i == 0 ? 12 : 17.6);
            grid.getColumnConstraints().add(cc);
        }
        
        for (int h = 8; h <= 22; h++) {
            Label timeLabel = new Label(h + ":00");
            timeLabel.setStyle("-fx-padding: 8; -fx-background-color: white; -fx-border-color: #f0f0f0; -fx-font-size: 11px;");
            timeLabel.setMaxWidth(Double.MAX_VALUE);
            timeLabel.setAlignment(Pos.TOP_RIGHT);
            grid.add(timeLabel, 0, h - 7);
            
            for (int c = 1; c < 6; c++) {
                StackPane cell = new StackPane();
                cell.setStyle("-fx-background-color: white; -fx-border-color: #f0f0f0; -fx-min-height: 45;");
                grid.add(cell, c, h - 7);
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

    private int getDayCol(String d) { return switch(d) { case "Monday" -> 1; case "Tuesday" -> 2; case "Wednesday" -> 3; case "Thursday" -> 4; case "Friday" -> 5; default -> 1; }; }
    
    public static void main(String[] args) { launch(args); }
}