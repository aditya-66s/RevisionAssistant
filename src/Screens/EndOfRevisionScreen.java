package Screens;

import Components.ScreenManager;
import Controllers.*;
import RecordClasses.SessionRecord;
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
    // Constants
    private static final int titleX = 300;
    // Initial Content
    // Time
    private static final int timeRectW = 738;
    private static final int timeRectH = 143;
    private static final int timeIconX = 184;
    private static final int timeIconY = 261;
    private static final int timePaneY = 266;
    private static final int timeTextX = 1107;
    private static final int timeTextY = 360;
    private static final int timeHolderX = 1063;
    private static final int timeHolderY = 397;
    private static final int timeHolderW = 583;
    // XP
    private static final int xpRectW = 738;
    private static final int xpRectH = 127;
    private static final int xpPaneX = 184;
    private static final int xpPaneY = 443;
    private static final int xpIconY = 438;
    private static final int xpTextX = 1204;
    private static final int xpTextY = 538;
    private static final int xpHolderX = 1063;
    private static final int xpHolderY = 565;
    private static final int xpHolderW = 583;
    private static final int xpHolderH = 8;
    // Buttons
    private static final int buttonW = 1519;
    private static final int buttonH = 127;
    private static final int graphsBtnX = 184;
    private static final int graphsBtnY = 599;
    private static final int backBtnX = 184;
    private static final int backBtnY = 764;
    private static final int retryY = 957;
    // Graphs
    private static final int graphBtnW = 480;
    private static final int graphBtnH = 80;
    private static final int buttonsX = 230;
    private static final int buttonsY = 200;
    private static final int graphX = 200;
    private static final int graphY = 300;
    private static final int graphW = 1500;
    private static final int graphH = 600;
    // Answer Timeline
    private static final int totalWidth = 1500;
    private static final int blockSize = 60;
    private static final int startingX = 20;
    private static final int spaceBetween = 6;
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
        Scene scene = new Scene(root, sceneW, sceneW);
        Group background = createBackground("End of Revision");
        getTitle().setLayoutX(titleX);

        Group contentLayer = new Group();
        // timer text
        Rectangle timeRect = new Rectangle(timeRectW, timeRectH);
        Text timeInfoText = new Text("Time:");
        timeInfoText.getStyleClass().add("buttonText");
        StackPane timePane = new StackPane(timeRect, timeInfoText);
        ImageView timeIcon = createIcon(timeIconX, timeIconY, "/images/EndOfRevisionScreen/timer.png");
        ui.setPosition(timePane, timeIconX, timePaneY);
        Text timeText = new Text(session.getFormattedTime());
        timeText.getStyleClass().add("summaryText");
        ui.setPosition(timeText, timeTextX, timeTextY);
        Rectangle timeHolder = new Rectangle(timeHolderX, timeHolderY, timeHolderW, xpHolderH);

        // Xp text
        Rectangle xpRect = new Rectangle(xpRectW, xpRectH);
        Text xpInfoText = new Text("XP:");
        xpInfoText.getStyleClass().add("buttonText");
        StackPane xpPane = new StackPane(xpRect, xpInfoText);
        ImageView xpIcon = createIcon(xpPaneX, xpIconY, "/images/EndOfRevisionScreen/xp.png");
        ui.setPosition(xpPane, xpPaneX, xpPaneY);
        int xpGained =  new XPController().calculateXP(session.getSession());
        Text xpText = new Text(String.valueOf(xpGained));
        xpText.getStyleClass().add("summaryText");
        ui.setPosition(xpText, xpTextX, xpTextY);
        Rectangle xpHolder = new Rectangle(xpHolderX, xpHolderY, xpHolderW, xpHolderH);

        // button to take you to graphs
        RectangleButtonBundle graphs = ui.buildRectangularButtonBundle(buttonW, buttonH, "Graphs", "buttonText", graphsBtnX, graphsBtnY);
        ImageView graphIcon = createIcon(graphsBtnX, graphsBtnY+iconOffset, "/images/EndOfRevisionScreen/graphs.png");
        graphs.button().setOnMouseClicked(_-> buildGraphsContent(contentLayer));
        // Button to go back to main menu
        RectangleButtonBundle backToMainMenu = ui.buildRectangularButtonBundle(buttonW, buttonH, "Back to Main Menu", "buttonText", backBtnX, backBtnY);
        ImageView backIcon = createIcon(backBtnX, backBtnY+iconOffset, "/images/Constant/back.png");
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
        retry.setLayoutY(retryY);
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
        RectangleButtonBundle correctIncorrect = ui.buildRectangularButtonBundle(graphBtnW, graphBtnH, "Correct vs Incorrect", "smallerButtonText", zero, zero);
        RectangleButtonBundle accuracyComparison = ui.buildRectangularButtonBundle(graphBtnW, graphBtnH, "Accuracy Comparison", "smallerButtonText", zero, zero);
        RectangleButtonBundle answerTimeline = ui.buildRectangularButtonBundle(graphBtnW, graphBtnH, "Answer Timeline", "smallerButtonText", zero, zero);
        // Place in a HBox
        HBox buttons = new HBox(vSpacing, correctIncorrect.button(), accuracyComparison.button(), answerTimeline.button());
        ui.setPosition(buttons, buttonsX, buttonsY);
        // Group where the chart is placed
        Group chartGroup = new Group();
        ui.setPosition(chartGroup, graphX, graphY);
        chartGroup.getChildren().addAll(buildCorrectIncorrectChart());
        // On click of buttons
        correctIncorrect.button().setOnMouseClicked(_ ->
                chartGroup.getChildren().setAll(buildCorrectIncorrectChart()));
        accuracyComparison.button().setOnMouseClicked(_ -> {
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
        chart.getStyleClass().add("charts");
        chart.setTitle("Correct Vs Incorrect");
        chart.setPrefSize(graphW, graphH);
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
        // Create the chart
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.getStyleClass().add("charts");
        chart.setTitle("Accuracy Comparison with last 3 (at most) Sessions with this deck");
        chart.setPrefSize(graphW, graphH);
        chart.setLegendVisible(false);
        // Create the series and populate it with data
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        ArrayList<SessionRecord> sessionHistory = stats.loadSessionRecords(ac.getEmail());
        // Create a list for only this deck
        ArrayList<SessionRecord> deckSessionHistory = new ArrayList<>();
        for(SessionRecord s : sessionHistory){
            // if deck name matches, add to list
            if(s.deckName().equals(session.getSelectedDeck())){
                deckSessionHistory.add(s);
            }
        }
        // Get last three sessions
        int size = deckSessionHistory.size();
        int start = Math.max(0, size - 3);
        for(int i = start; i < size; i++) {
            // Add each accuracy to the chart
            series.getData().add(new XYChart.Data<>("Session - " + (size - i),
                    deckSessionHistory.get(i).accuracy()));
        }
        // Add this session's accuracy
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
        int totalW = totalWidth; // total width
        int blockS = blockSize; // Size of each block
        int startX = startingX; // Starting x position
        int startY = zero; // starting y position
        int gap = spaceBetween; // Space between blocks
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
