package Screens;

import Components.CustomAlert;
import Components.ScreenManager;
import Controllers.AuthenticationController;
import Controllers.FlashcardController;
import Controllers.SettingsController;
import Controllers.StatsController;
import RecordClasses.SessionRecord;
import RecordClasses.RectangleButtonBundle;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RevisionSystemScreen extends SystemScreens{
    // Constants
    private static final int titleX = 275;
    // Graphs
    private static final int graphBtnW = 380;
    private static final int graphBtnH = 80;
    private static final int graphButtonX = 180;
    private static final int graphButtonY = 200;
    private static final int graphX = 200;
    private static final int graphY = 300;
    private static final int graphW = 1500;
    private static final int graphH = 600;
    // Import / Export
    private static final int iconX = 297;

    private final AuthenticationController ac;
    private final SettingsController sc;
    private final FlashcardController fc;
    private final StatsController stats;

    public RevisionSystemScreen(AuthenticationController ac, SettingsController sC, StatsController stats) {
        this.ac = ac;
        this.sc = sC;
        this.fc = new FlashcardController(ac.getEmail());
        this.stats = stats;
    }
    // Creates the revision system scene
    public Scene create(){
        Pane root = new Pane();
        root.getStyleClass().add("screens");
        Scene scene = new Scene(root, sceneW, sceneH);
        Group background = createBackground("Revision System");
        getTitle().setLayoutX(titleX);
        ImageView back = getSwitchScreenBackView(ac);

        Group contentLayer = new Group();

        RectangleButtonBundle fcManagement = ui.buildRectangularButtonBundle(rectW, rectH, "Flashcards", "buttonText", fourX, fourY1);
        ImageView fcmIcon = createIcon(fourX, fourY1+iconOffset, "/images/RevisionSystemScreen/flashcardmanagement.png");
        fcManagement.button().setOnMouseClicked(_ -> {
            try {
                fcManagementMouseClick();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        RectangleButtonBundle studyModes = ui.buildRectangularButtonBundle(rectW, rectH, "Study Modes", "buttonText", fourX,  fourY2);
        ImageView smIcon = createIcon(fourX, fourY2+iconOffset, "/images/RevisionSystemScreen/studymodes.png");
        studyModes.button().setOnMouseClicked(_ -> {
            try {
               smMouseClick();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        RectangleButtonBundle impOrExp = ui.buildRectangularButtonBundle(rectW, rectH, "Import / Export", "buttonText", fourX, fourY3);
        ImageView impIcon = createIcon(fourX, fourY3+iconOffset, "/images/RevisionSystemScreen/import.png");
        impOrExp.button().setOnMouseClicked(_ -> buildImportExport(contentLayer));
        RectangleButtonBundle analytics = ui.buildRectangularButtonBundle(rectW, rectH, "Analytics", "buttonText", fourX, fourY4);
        ImageView aIcon = createIcon(fourX, fourY4+iconOffset, "/images/RevisionSystemScreen/analytics.png");
        analytics.button().setOnMouseClicked(_-> {
            try {
                buildAnalyticsContent(contentLayer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Group buttons = new Group(fcManagement.button(), studyModes.button(), impOrExp.button(), analytics.button(), fcmIcon, smIcon, impIcon, aIcon);
        contentLayer.getChildren().add(buttons);
        root.getChildren().addAll(background, back, contentLayer);
        return scene;
    }

    // Build the content to show the graphs
    private void buildAnalyticsContent(Group contentLayer) throws IOException {
        // Buttons to switch the graphs
        RectangleButtonBundle accuracy = ui.buildRectangularButtonBundle(graphBtnW, graphBtnH, "Accuracy", "smallerButtonText", zero, zero);
        RectangleButtonBundle studyTime = ui.buildRectangularButtonBundle(graphBtnW, graphBtnH, "Study Time", "smallerButtonText", zero, zero);
        RectangleButtonBundle weakDecks = ui.buildRectangularButtonBundle(graphBtnW, graphBtnH, "Weakest Decks", "smallerButtonText", zero, zero);
        RectangleButtonBundle sessions = ui.buildRectangularButtonBundle(graphBtnW, graphBtnH, "Sessions / Week", "smallerButtonText", zero, zero);
        HBox buttons = new HBox(vSpacing, accuracy.button(), studyTime.button(), weakDecks.button(), sessions.button());
        ui.setPosition(buttons, graphButtonX, graphButtonY);
        // Create the chart group
        Group chartGroup = new Group();
        ui.setPosition(chartGroup, graphX, graphY);
        // Load all session history
        ArrayList<SessionRecord> historicalSessions = stats.loadSessionRecords(ac.getEmail());
        // Initial chart
        chartGroup.getChildren().addAll(buildAccuracyChart(historicalSessions));
        // On button mouseclicks
        accuracy.button().setOnMouseClicked(_-> chartGroup.getChildren().setAll(buildAccuracyChart(historicalSessions)));
        studyTime.button().setOnMouseClicked(_-> chartGroup.getChildren().setAll(buildStudyTime(historicalSessions)));
        weakDecks.button().setOnMouseClicked(_-> chartGroup.getChildren().setAll(buildWeakDecksChart(historicalSessions)));
        sessions.button().setOnMouseClicked(_-> chartGroup.getChildren().setAll(buildSessionsPerWeekChart(historicalSessions)));
        nav.switchContent(contentLayer, buttons, chartGroup, getBackView(contentLayer));
    }

    // Accuracy Line Graph
    private LineChart<String, Number> buildAccuracyChart(ArrayList<SessionRecord> historicalSessions) {
        // Create the axis
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0, 100.0, 5);
        xAxis.setLabel("Session");
        yAxis.setLabel("Accuracy / %");
        // Create the chart
        LineChart<String, Number> lineGraph = new LineChart<>(xAxis, yAxis);
        lineGraph.getStyleClass().add("charts");
        lineGraph.setTitle("Accuracy over Time");
        lineGraph.setMaxSize(graphW, graphH);
        lineGraph.setPrefSize(graphW, graphH);
        lineGraph.setLegendVisible(false);
        lineGraph.setCreateSymbols(true); // data points at each plot point
        // Populate the graph
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        int sessionNumber = 1;
        for(SessionRecord s : historicalSessions){
            series.getData().add(
                    new XYChart.Data<>("Session " + sessionNumber,
                            s.accuracy()));
                    sessionNumber++;
        }
        lineGraph.getData().add(series);
        return lineGraph;
    }
    // Study time per session line graph
    private LineChart<String, Number> buildStudyTime(ArrayList<SessionRecord> historicalSessions) {
        // Create the axis
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Session");
        yAxis.setLabel("Time / m");
        // Create the chart
        LineChart<String, Number> lineGraph = new LineChart<>(xAxis, yAxis);
        lineGraph.getStyleClass().add("charts");
        lineGraph.setTitle("Study Time per Session");
        lineGraph.setMaxSize(graphW, graphH);
        lineGraph.setPrefSize(graphW, graphH);
        lineGraph.setLegendVisible(false);
        lineGraph.setCreateSymbols(true);
        // Populate the graph
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        int sessionNumber = 1;
        for(SessionRecord s : historicalSessions){
            double minutes = s.studySeconds() / 60.0;
            minutes = Math.round(minutes * 10.0) / 10.0;
            series.getData().add(
                    new XYChart.Data<>("Session " + sessionNumber,
                           minutes));
            sessionNumber++;
        }
        lineGraph.getData().add(series);
        return lineGraph;
    }
    // Create the weak decks bar chart
    private BarChart<String, Number> buildWeakDecksChart(ArrayList<SessionRecord> historicalSessions){
        // Create the axis
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Deck");
        yAxis.setLabel("Average Accuracy / %");
        // Create the chart
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.getStyleClass().add("charts");
        chart.setTitle("Weakest Decks");
        chart.setMaxSize(graphW, graphH);
        chart.setPrefSize(graphW, graphH);
        chart.setLegendVisible(false);
        // Group sessions by deck and avg accuracy
        HashMap<String, ArrayList<Double>> deckAccuracies = new HashMap<>();
        // Iterate through each session record
        for(SessionRecord s : historicalSessions){
            // If deck name does not exist in the map, create a new ArrayList and key for it
            // Session accuracy added to list
            deckAccuracies.computeIfAbsent(s.deckName(), _->
                    new ArrayList<>()).add(s.accuracy());
        }
        // Create the series to populate the chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        // convert each map entry from deck name and list of accuracies to deck name and average accuracies
        deckAccuracies.entrySet().stream().map(
                // Calculate the avg of the accuracy values in the list
                e ->{
                    double average = e.getValue().stream()
                            // convert to primitive doubles instead of double objects
                            // Find average
                            .mapToDouble(Double::doubleValue).average().orElse(0);
                    return Map.entry(e.getKey(), average);
                    // Sort in ascending order
                }).sorted(Map.Entry.comparingByValue())
                // Add each entry to the series
                .forEach(e -> series.getData().add(new XYChart.Data<>(e.getKey(), Math.round(e.getValue() * 10.0) / 10.0)));
        chart.getData().add(series);
        return chart;
    }
    // Sessions per Week -> bar chart
    private BarChart<String, Number> buildSessionsPerWeekChart(ArrayList<SessionRecord> historicalSessions){
        // Create the axis
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Week");
        yAxis.setLabel("Sessions");
        // Create the chart
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.getStyleClass().add("charts");
        chart.setLegendVisible(false);
        chart.setTitle("Sessions per Week");
        chart.setMaxSize(graphW, graphH);
        chart.setPrefSize(graphW, graphH);
        // Populate the chart with a hash map
        HashMap<String, Integer> weeklySessions = new HashMap<>();
        for(SessionRecord s : historicalSessions){
            // Split date into day, month and year
            String[] date = s.date().split("/");
            // Ensure date is valid
            if(date.length == 3){
                // Convert the date into a week number and label it
                String week = "W" + getWeek(date[0], date[1], date[2]);
                // Add session to correct week, if week exists increment, if not create it with value 1
                weeklySessions.merge(week, 1, Integer::sum);
            }
        }
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        // Convert to stream and sort it by week
        weeklySessions.entrySet().stream().sorted((a,b) -> {
                        int weekA = Integer.parseInt(a.getKey().substring(1)); // Remove the W and compare
                        int weekB = Integer.parseInt(b.getKey().substring(1));
                        return Integer.compare(weekA, weekB);
                        })
                // For each week, add the session count to chart
                .forEach(e -> series.getData().add(new XYChart.Data<>(e.getKey(), e.getValue())));
        chart.getData().add(series);
        return chart;
    }
    // Function to determine the week number from a date
    private int getWeek(String day, String month, String year){
        // Convert into a date object
        LocalDate date = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        // Return the week number of the year (ISO, weeks start on Monday)
        return date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }
    // Procedure switching the scene the FMS
    private void fcManagementMouseClick() throws IOException {
        ScreenManager.switchScene(new FlashcardManagementScreen(ac, sc, fc, stats).create(), ac.getEmail());
    }

    // Procedure creating the Import / Export Content
    private void buildImportExport(Group contentLayer){

        Window owner = contentLayer.getScene().getWindow();
        RectangleButtonBundle importCSV = ui.buildRectangularButtonBundle(rectW, rectH, "Import CSV", "buttonText", threeX, threeY1);
        ImageView importIcon = createIcon(iconX, threeY1+iconOffset, "/images/RevisionSystemScreen/upload.png");
        importCSV.button().setOnMouseClicked(_ -> {
            try {
                if(fc.importCSV(owner)) CustomAlert.success(owner, "Successfully imported CSV file");
            } catch (IOException e) {
                CustomAlert.error(owner, "Could not import, try again later");
                throw new RuntimeException(e);
            }
        });

        RectangleButtonBundle exportCSV = ui.buildRectangularButtonBundle(rectW, rectH, "Export CSV", "buttonText", threeX, threeY2);
        ImageView exportIcon = createIcon(threeX, threeY2+iconOffset, "/images/RevisionSystemScreen/textFile.png");
        exportCSV.button().setOnMouseClicked(_ -> {
            try {
                boolean exported = fc.export(owner, false);
                if(exported) CustomAlert.success(owner, "Successfully exported CSV file");
                if(!exported) CustomAlert.error(owner, "Could not export CSV file");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        RectangleButtonBundle exportHTML = ui.buildRectangularButtonBundle(rectW, rectH, "Export HTML", "buttonText", threeX, threeY3);
        ImageView htmlIcon = createIcon(threeX, threeY3+iconOffset, "/images/RevisionSystemScreen/file.png");
        exportHTML.button().setOnMouseClicked(_ -> {
            try {
                boolean exported = fc.export(owner, true);
                if(exported) CustomAlert.success(owner, "Successfully exported CSV file as HTML \n You can print this to a PDF");
                if(!exported) CustomAlert.error(owner, "Could not export CSV file");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ImageView hintView = ui.getHintView();
        String importInfo = "You can import a CSV which follows the " +
                "question, answer format with speech marks \n surrounding both and new lines indicated by backslash and n";
        String exportCSVInfo = "You will get an exported csv following the same lines as import versions";
        String exportHTMLInfo = "You can export your decks to a HTML page, and then print to PDF for portability";
        hintView.setOnMouseClicked(_-> CustomAlert.info(owner, importInfo + "\n" + "\n" + exportCSVInfo + "\n" + "\n" + exportHTMLInfo));

        Group importExportButtons = new Group(importCSV.button(), exportCSV.button(), exportHTML.button(), importIcon, exportIcon, htmlIcon);
        nav.switchContent(contentLayer, getBackView(contentLayer), importExportButtons, hintView);
    }
    // Switch scene when study modes is clicked
    private void smMouseClick() throws IOException {
        ScreenManager.switchScene(new StudyModesScreen(ac, sc, fc, stats).create(), ac.getEmail());
    }

}
