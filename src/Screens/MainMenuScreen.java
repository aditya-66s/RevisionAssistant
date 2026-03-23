package Screens;

import Components.CustomAlert;
import Components.ScreenManager;
import Controllers.AuthenticationController;
import Controllers.SettingsController;
import Controllers.StatsController;
import Models.UserStats;
import RecordClasses.RectangleButtonBundle;
import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class MainMenuScreen extends SystemScreens {
    private final AuthenticationController ac;
    private final SettingsController sc;
    private final StatsController stats;

    public MainMenuScreen(AuthenticationController ac, SettingsController sC) throws IOException {
        this.ac = ac;
        this.sc = sC;
        this.stats = new StatsController();
    }

    public Scene create(){
        Pane root = new Pane();
        root.getStyleClass().add("screens");
        // Creating the background rectangle using the inherited method from the SystemScreens Class
        Group background = createBackground("Main Menu");
        getTitle().setLayoutX(510);
        Group contentLayer = new Group();
        // Creating buttons bundles for all
        RectangleButtonBundle revisionSystem = ui.buildRectangularButtonBundle(rectW, rectH, "Revision System", "buttonText", 310, 250);
        revisionSystem.button().setOnMouseClicked(_ -> {
            try {
                revisionSystemMouseClick();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        ImageView rsIcon = createIcon(310, 245, "/images/MainMenuScreen/books.png");

        RectangleButtonBundle userManagement = ui.buildRectangularButtonBundle(rectW, rectH, "User Management", "buttonText", 310,  420);
        userManagement.button().setOnMouseClicked(_ -> buildUMContent(contentLayer));
        ImageView umIcon = createIcon(310, 415, "/images/MainMenuScreen/userbook.png");

        RectangleButtonBundle settings = ui.buildRectangularButtonBundle(rectW, rectH, "Settings", "buttonText", 310, 591);
        settings.button().setOnMouseClicked(_ -> {
            try {
                settingsMouseClick();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        ImageView sIcon = createIcon(310, 586, "/images/MainMenuScreen/settings.png");

        RectangleButtonBundle logout = ui.buildRectangularButtonBundle(rectW, rectH, "Logout", "buttonText", 310, 762);
        logout.button().setOnMouseClicked(this::logout);
        ImageView lIcon = createIcon(310, 757, "/images/MainMenuScreen/door.png");

        Group buttons = new Group(revisionSystem.button(), userManagement.button(), settings.button(), logout.button());
        Group icons = new Group(rsIcon, umIcon, sIcon, lIcon);
        contentLayer.getChildren().addAll(buttons, icons);
        root.getChildren().addAll(background, contentLayer);
        Scene scene = new Scene(root, 1920, 1057);
//        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());
        return scene;
    }

    private void logout(Event e) {
        Window alertWindow = ((Node) e.getSource()).getScene().getWindow();
        if(CustomAlert.confirm(alertWindow, "Logout Confirmation", "Are you sure you would like to log out?")){
            ac.logout();
        }
    }

    private void settingsMouseClick() throws IOException {
        ScreenManager.switchScene(new SettingsScreen(ac, sc).create(), ac.getEmail());
    }

    private void revisionSystemMouseClick() throws IOException {
        ScreenManager.switchScene(new RevisionSystemScreen(ac, sc, stats).create(), ac.getEmail());
    }
    // Procedure which builds the content for the User Management Page
    private void buildUMContent(Group contentLayer) {
        RectangleButtonBundle statistics = ui.buildRectangularButtonBundle(rectW, rectH, "Statistics", "buttonText", 293, 315);
        ImageView sIcon = createIcon(293, 310, "/images/MainMenuScreen/stats.png");
        statistics.button().setOnMouseClicked(_ -> {
            try {
                buildStatisticsContent(contentLayer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        RectangleButtonBundle achievements = ui.buildRectangularButtonBundle(rectW, rectH, "Achievements", "buttonText", 293, 485);
        ImageView aIcon = createIcon(293, 480, "/images/MainMenuScreen/achievement.png");
        achievements.button().setOnMouseClicked(_ -> {
            try {
                buildAchievementsContent(contentLayer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        RectangleButtonBundle fileManagement = ui.buildRectangularButtonBundle(rectW, rectH, "File Management", "buttonText", 293, 656);
        ImageView fmIcon = createIcon(293, 651, "/images/MainMenuScreen/folder.png");
        fileManagement.button().setOnMouseClicked(_ -> buildFileManagementContent(contentLayer));

        Group options = new Group(statistics.button(), fileManagement.button(), achievements.button(), sIcon, aIcon, fmIcon);

        nav.switchContent(contentLayer, options, getBackView(contentLayer));
    }
    // Build content where user can see his stats
    private void buildStatisticsContent(Group contentLayer) throws IOException {
        UserStats uStats = stats.loadUserStats(ac.getEmail());
        // Level header
        Text levelText = new Text("Level " + uStats.getLevel());
        levelText.getStyleClass().add("levelText");
        ui.setPosition(levelText, 300, 900);
        // XP Bar
        Rectangle bar = new Rectangle(300, 820, 1320, 40);
        bar.getStyleClass().add("XPBar");
        // XP Progress
        double progress = (double) uStats.getXPIntoCurrentLevel() / uStats.getXPForNext();
        progress = Math.min(progress, 1.0); // clamp to 1.0
        Rectangle progressFill = new Rectangle(300, 820, 1320*progress, 40);
        progressFill.getStyleClass().add("progressFill");
        // Text under bar
        Text xpProgressText = new Text( uStats.getXPIntoCurrentLevel() + "/" + uStats.getXPForNext());
        xpProgressText.getStyleClass().add("infoText");
        ui.setPosition(xpProgressText, 1420, 900);

        nav.switchContent(contentLayer, createStatsGrid(uStats), levelText, xpProgressText, bar, progressFill, getBackView(contentLayer));
    }
    // Creates a grid of stats
    private Group createStatsGrid(UserStats uStats) {
        Group statsGrid = new Group();
        // Preventing divide by 0 error
        int correct = uStats.getTotalCorrect();
        int incorrect = uStats.getTotalIncorrect();
        double accuracy;
        if(correct + incorrect == 0){accuracy = 0;}
        else{accuracy = (double) correct / (correct + incorrect);}
        // 2D array used to store each stat as a pair, avoiding repeating code
        String[][] grid = {
                {"Total XP", String.valueOf(uStats.getTotalXP())},
                {"Accuracy", (Math.round(100*accuracy) + "%")},
                {"Sessions Completed", String.valueOf(uStats.getTotalSessions())},
                {"Total Study Time", formatTime(uStats.getTotalStudyTime())},
                {"Total Correct", String.valueOf(correct)},
                {"Total Incorrect", String.valueOf(incorrect)},
                {"Current Streak", uStats.getCurrentStreak() + " days"},
                {"Longest Streak", uStats.getLongestStreak() + " days"},
                {"Max Streak", uStats.getConsecutiveCorrect() + " cards"},
                {"Total Retries", String.valueOf(uStats.getTotalRetries())},
                {"Achievements", uStats.getUnlockedAchievements().size() + " / 14"},
                {"Last Study Date", uStats.getLastStudyDate()},
        };
        int leftX = 200;
        int rightX = 1000;
        int startY = 310;
        int rowH = 70;
        for(int i = 0; i < grid.length; i++){
            // Even rows go on the left, odd goes to the right
            int x = (i % 2 == 0) ? leftX : rightX;
            int y = startY + (i/2) * rowH;
            // Create the stat text
            Text statNameText = new Text(grid[i][0] + ":");
            statNameText.getStyleClass().add("statText");
            ui.setPosition(statNameText, x, y);
            // Create the actual statistic to accompany this text
            Text statistic = new Text(grid[i][1]);
            statistic.getStyleClass().add("statText");
            ui.setPosition(statistic, x + 500, y);
            statsGrid.getChildren().addAll(statNameText, statistic);
        }
        return statsGrid;
    }
    // Format time
    private String formatTime(long sec){
        return String.format("%02d:%02d:%02d", sec / 3600, (sec % 3600) / 60, sec % 60);
    }

    // Builds content where the user can see what achievements they have
    public void buildAchievementsContent(Group contentLayer) throws IOException {
        UserStats uStats = stats.loadUserStats(ac.getEmail());
        // 2D Array with Achievements and descriptions
        String[][] achievements = {
                {"Sharp Mind", "Get 5 answers correct in a row"},
                {"Perfect Run", "Get 10 answers correct in a row"},
                {"Flawless", "Complete a session with 100% accuracy"},
                {"Getting Started", "Answer 10 flashcards"},
                {"Knowledge Expert", "Answer 100 flashcards correctly"},
                {"100 Club", "Answer 100 questions total"},
                {"Revision Rookie", "Complete 5 sessions"},
                {"Revision Master", "Complete 50 sessions"},
                {"Study Streak", "Study for 3 days in a row"},
                {"Dedicated Learner", "Study for 7 days in a row"},
                {"Revision Veteran", "Study for a total of 30 days"},
                {"Keep Trying", "Retry a session"},
                {"Never Give Up", "Retry 10 sessions"},
                {"Ultimate Scholar", "Unlock all achievements"},
        };
        Group grid = new Group();
        int leftX = 450;
        int rightX = 1000;
        int startY = 250;
        int rowH = 105;
        // build the Text
        for(int i = 0; i < achievements.length; i++){
            String achievement = achievements[i][0];
            String description = achievements[i][1];
            boolean unlocked = uStats.isAchievementUnlocked(achievement);
            int x = (i % 2 == 0) ? leftX : rightX;
            int y = startY + (i/2) * rowH;

            // Add correct emoji depending on
            Text achievementText = new Text((unlocked ? "🏆  " : "🔒  ") + achievement);
            achievementText.getStyleClass().add("statText");
            ui.setPosition(achievementText, x, y);

            Text descriptionText = new Text(description);
            descriptionText.getStyleClass().add("descriptionText");
            ui.setPosition(descriptionText, x + 40, y + 30);
            // Set the colour to locked achievements differently
            if(!unlocked){
                achievementText.setStyle("-fx-fill: #888888;");
                descriptionText.setStyle("-fx-fill: #888888;");
            }
            grid.getChildren().addAll(achievementText, descriptionText);
        }
        nav.switchContent(contentLayer, grid, getBackView(contentLayer));
    }
    // Create file management content
    private void buildFileManagementContent(Group contentLayer) {
        Window window = contentLayer.getScene().getWindow();
        HBox content = new HBox(20);
        ui.setPosition(content, 270, 350);
        String[] files = { "users.csv", "settings.csv", "stats.csv" };
        // Create buttons for each
        for(int i = 0; i < files.length; i++){
            String file = files[i];

            Text text = new Text(file);
            text.getStyleClass().add("infoText");

            RectangleButtonBundle exp = ui.buildRectangularButtonBundle(450, 100, "Export Backup", "smallerButtonText", 0, 0);
            exp.button().setOnMouseClicked(_ -> {
                try {
                    exportFile(window, file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            RectangleButtonBundle imp = ui.buildRectangularButtonBundle(450, 100, "Import Backup", "smallerButtonText", 0, 0);
            imp.button().setOnMouseClicked(_ -> {
                try {
                    importFile(window, file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            VBox box = new VBox(30, text, exp.button(), imp.button());
            content.getChildren().addAll(box);
        }
        nav.switchContent(contentLayer, content, getBackView(contentLayer));
    }
    // Copies the system CSV to a location chosen by user
    private void exportFile(Window window, String fileName) throws IOException {
        // Create a file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export " + fileName);
        fileChooser.setInitialFileName(fileName);
        // Filter to csv
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File destination = fileChooser.showSaveDialog(window);
        // if save cancelled, return
        if(destination == null) return;

        // Find the source and copy it to save location
        Path source = Paths.get(System.getProperty("user.dir"), "files", fileName);
        Files.copy(source, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

        CustomAlert.success(window, fileName + " has been exported");
    }
    // Replaces the system CSV with user chosen file
    private void importFile(Window window, String fileName) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import " + fileName);
        // Filter to csv
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File source = fileChooser.showOpenDialog(window);
        // return if empty or cancelled
        if(source == null) return;
        if(!ac.validateCSV(source, fileName)){
            CustomAlert.error(window, "Invalid file format");
            return;
        }
        if(source.length() == 0){
            CustomAlert.error(window, "File has no contents");
            return;
        }
        // Ask for user confirmation
        boolean confirmed = CustomAlert.confirm(window, "Import Backup", "This will replace your current " + fileName
                    + ". Are you sure you want to continue? You will be logged out following the import.");
        if(!confirmed) return;
        Path destination = Paths.get(System.getProperty("user.dir"), "files", fileName);
        Files.copy(source.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
        CustomAlert.success(window, fileName + " has been imported");
        // Log out so imported files can be read from logging in
        ac.logout();
    }
}
