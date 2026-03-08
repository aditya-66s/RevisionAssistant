package Screens;

import Components.ScreenManager;
import Controllers.*;
import Models.SessionRecord;
import Models.UserStats;
import RecordClasses.RectangleButtonBundle;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class EndOfRevisionScreen extends SystemScreens{
    // Controller
    private final AuthenticationController ac;
    private final SettingsController sc;
    private final SessionController session;
    private final FlashcardController fc;
    private final StatsController stats;
    private final UserStats userStats;

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Constructor
    public EndOfRevisionScreen(AuthenticationController ac, SettingsController sc, SessionController session,
                               FlashcardController fc, StatsController stats,  UserStats userStats){
        this.ac = ac;
        this.sc = sc;
        this.session = session;
        this.fc = fc;
        this.stats = stats;
        this.userStats = userStats;
    }
    // Create to be displayed
    public Scene create(){
        Pane root = new Pane();
        root.getStyleClass().add("screens");
        Scene scene = new Scene(root, 1920, 1057);
        Group background = createBackground("End of Revision");
        getTitle().setLayoutX(300);

        Group contentLayer = new Group();
        // timer text
        Rectangle timeRect = new Rectangle(738, 143);
        Text timeInfoText = new Text("Time:");
        timeInfoText.getStyleClass().add("buttonText");
        StackPane timePane = new StackPane(timeRect, timeInfoText);
        ImageView timeIcon = createIcon(184, 261, "/images/EndOfRevisionScreen/timer.png");
        ui.setPosition(timePane, 184, 266);
        Text timeText = new Text(session.getFormattedTime());
        timeText.getStyleClass().add("summaryText");
        ui.setPosition(timeText, 1107, 360);
        Rectangle timeHolder = new Rectangle(1063, 397, 583, 8);

        // Xp text
        Rectangle xpRect = new Rectangle(738, 127);
        Text xpInfoText = new Text("XP:");
        xpInfoText.getStyleClass().add("buttonText");
        StackPane xpPane = new StackPane(xpRect, xpInfoText);
        ImageView xpIcon = createIcon(184, 438, "/images/EndOfRevisionScreen/xp.png");
        ui.setPosition(xpPane, 184, 443);
        int xpGained =  new XPController().calculateXP(session.getSession());
        Text xpText = new Text(String.valueOf(xpGained));
        xpText.getStyleClass().add("summaryText");
        ui.setPosition(xpText, 1204, 538);
        Rectangle xpHolder = new Rectangle(1063, 565, 583, 8);

        // button to take you to graphs
        RectangleButtonBundle graphs = ui.buildRectangularButtonBundle(1519, 127, "Graphs", "buttonText", 184, 599);
        ImageView graphIcon = createIcon(184, 594, "/images/EndOfRevisionScreen/graphs.png");
        graphs.button().setOnMouseClicked(_-> buildGraphsContent(contentLayer));
        // Button to go back to main menu
        RectangleButtonBundle backToMainMenu = ui.buildRectangularButtonBundle(1519, 127, "Back to Main Menu", "buttonText", 184, 764);
        ImageView backIcon = createIcon(184, 759, "/images/Constant/back.png");
        backToMainMenu.button().setOnMouseClicked(_ -> {
            try {
                // Save session record when user is done with the session
                stats.saveSessionRecord(ac.getEmail(), new SessionRecord(
                        LocalDate.now().format(dtf), session.getAccuracy(),
                        session.getElapsedTime(), session.getCorrectCount(),
                        session.getIncorrectCount(), session.getSelectedDeck(),
                        session.getSession().getAnswerHistory()
                ));
                ScreenManager.switchScene(new MainMenuScreen(ac, sc).create(), ac.getEmail());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ScreenManager.clearHistory();
        });
        // Retry button to continue the session
        ImageView retry = new ImageView("/images/EndOfRevisionScreen/retry.png");
        retry.getStyleClass().add("constantImages");
        retry.setLayoutY(957);
        retry.setCursor(Cursor.HAND);
        retry.setOnMouseClicked(_ -> {
            session.retrySession();
            // If retrying, the xp gained is subtracted and saved to csv
            // It will be resaved on the end of the session completely
            userStats.setTotalXP(userStats.getTotalXP() - xpGained);
            try {
                stats.saveUserStats(userStats);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            StudyModesScreen retryModes = null;
            try {
                retryModes = new StudyModesScreen(ac, sc, fc, session, stats);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ScreenManager.switchScene(retryModes.continueSession());
        });

        Group icons = new Group(timeIcon, xpIcon, graphIcon, backIcon, retry);
        contentLayer.getChildren().addAll(timePane, timeHolder, timeText, xpPane, xpHolder, xpText, graphs.button(), backToMainMenu.button(), icons);
        root.getChildren().addAll(background, contentLayer);
        return scene;
    }

    // Build content where use can switch the graphs
    private void buildGraphsContent(Group contentLayer) {
        // Create Graph Option Buttons
        RectangleButtonBundle correctIncorrect = ui.buildRectangularButtonBundle(480, 80, "Correct vs Incorrect", "smallerButtonText", 0, 0);
        RectangleButtonBundle accuracyComparison = ui.buildRectangularButtonBundle(480, 80, "Accuracy Comparison", "smallerButtonText", 0, 0);
        RectangleButtonBundle answerTimeline = ui.buildRectangularButtonBundle(480, 80, "Answer Timeline", "smallerButtonText", 0, 0);
        // Place in a HBox
        HBox buttons = new HBox(10, correctIncorrect.button(), accuracyComparison.button(), answerTimeline.button());
        ui.setPosition(buttons, 230, 200);

        Group chartGroup = new Group();
        ui.setPosition(chartGroup, 200, 300);
        chartGroup.getChildren().addAll(buildCorrectIncorrectChart());

        correctIncorrect.button().setOnMouseClicked(_ ->
                chartGroup.getChildren().setAll(buildCorrectIncorrectChart()));

        accuracyComparison.button().setOnMouseClicked(_ ->
        {
            try {
                chartGroup.getChildren().setAll(buildAccuracyComparisonChart());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        answerTimeline.button().setOnMouseClicked(_ ->
                chartGroup.getChildren().setAll(buildAnswerTimeline()));

        nav.switchContent(contentLayer, buttons, chartGroup, getBackView(contentLayer));
    }

    // Default chart -> correct vs incorrect bar chart
    private BarChart<String, Number> buildCorrectIncorrectChart(){
        // Create the axis and label them
        CategoryAxis xAxis = new CategoryAxis();
        int max = Math.max(session.getCorrectCount(), session.getIncorrectCount());
        NumberAxis yAxis = new NumberAxis(0, max + 1, 1);
        yAxis.setTickUnit(1.0);
        yAxis.setTickLabelGap(1.0);
        xAxis.setLabel("Result");
        yAxis.setLabel("Count");

        // Create the actual chart
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.getStyleClass().add("barChart1");
        chart.setTitle("Correct Vs Incorrect");
        chart.setPrefSize(1500, 600);
        chart.setLegendVisible(false);

        // Create data series to store the results
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        XYChart.Data<String, Number> correct = new XYChart.Data<>("Correct", session.getCorrectCount());
        XYChart.Data<String, Number> incorrect = new XYChart.Data<>("Incorrect", session.getIncorrectCount());
        series.getData().addAll(List.of(correct, incorrect));
        // Check if in scene then assign colours, otherwise node is null
        chart.sceneProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {
                correct.getNode().setStyle("-fx-background-color: green;");
                incorrect.getNode().setStyle("-fx-background-color: red;");
            }
        });
        // Add series to chart
        chart.getData().add(series);
        return chart;
    }
    // Chart 2 -> answer comparison for last three session
    private BarChart<String, Number> buildAccuracyComparisonChart() throws IOException {
        // Create the axis
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        xAxis.setLabel("Session");
        yAxis.setLabel("Accuracy / %");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.getStyleClass().add("barChart1");
        chart.setTitle("Accuracy Comparison with Last 3 Sessions");
        chart.setPrefSize(1500, 600);
        chart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        ArrayList<SessionRecord> sessionHistory = stats.loadSessionRecords(ac.getEmail());
        int size = sessionHistory.size();
        int start = Math.max(0, size - 3);

        for(int i = start; i < size; i++) {
            series.getData().add(new XYChart.Data<>("Session - " + (size - i),
                    sessionHistory.get(i).getAccuracy()));
        }

        series.getData().add(new XYChart.Data<>("This Session", session.getAccuracy()));

        chart.getData().add(series);
        return chart;
    }
    // Chart 3 -> answer timeline -> visual representation of using coloured blocks
    private Group buildAnswerTimeline(){
        Group group = new Group();
        ArrayList<Boolean> history = session.getSession().getAnswerHistory();
        if(history.isEmpty()) return group;

        // Layout settings for grids
        int totalW = 1500; // total width
        int blockS = 60; // Size of each block
        int startX = 20; // Starting x position
        int startY = 0; // starting y position
        int gap = 6; // Space between blocks
        int blocksPerRow = totalW / (blockS + gap); // How many blocks can fit in a row
        // loop through each answer
        for(int i = 0; i < history.size(); i++){
            // Determine column and row of current block
            int column = i%blocksPerRow;
            int row = i/blocksPerRow;
            // Calculate the screen position
            int x = startX + column*(blockS + gap);
            int y = startY + row*(blockS + gap);
            Rectangle block = new Rectangle(x, y, blockS, blockS);
            block.getStyleClass().add("block");
            // Colour changes based on true or false
            block.setStyle("-fx-fill: " + (history.get(i) ? "green" : "red") + ";");
            group.getChildren().add(block);
        }
        return group;
    }
}
