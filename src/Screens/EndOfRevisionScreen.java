package Screens;

import Components.ScreenManager;
import Controllers.*;
import Models.UserStats;
import RecordClasses.RectangleButtonBundle;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import java.io.IOException;


public class EndOfRevisionScreen extends SystemScreens{
    // Controller
    private final AuthenticationController ac;
    private final SettingsController sc;
    private final SessionController session;
    private final FlashcardController fc;
    private final StatsController stats;
    private final UserStats userStats;
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
        Text xpText = new Text(String.valueOf(new XPController().calculateXP(session.getSession())));
        xpText.getStyleClass().add("summaryText");
        ui.setPosition(xpText, 1204, 538);
        Rectangle xpHolder = new Rectangle(1063, 565, 583, 8);

        // button to take you to graphs
        RectangleButtonBundle graphs = ui.buildRectangularButtonBundle(1519, 127, "Graphs", "buttonText", 184, 599);
        ImageView graphIcon = createIcon(184, 594, "/images/EndOfRevisionScreen/graphs.png");
        // Button to go back to main menu
        RectangleButtonBundle backToMainMenu = ui.buildRectangularButtonBundle(1519, 127, "Back to Main Menu", "buttonText", 184, 764);
        ImageView backIcon = createIcon(184, 759, "/images/Constant/back.png");
        backToMainMenu.button().setOnMouseClicked(_ -> {
            try {
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
}
