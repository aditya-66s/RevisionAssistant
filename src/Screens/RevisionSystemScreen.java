package Screens;

import Components.ScreenManager;
import Controllers.AuthenticationController;
import Controllers.SettingsController;
import RecordClasses.RectangleButtonBundle;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class RevisionSystemScreen extends SystemScreens{
    private final AuthenticationController ac;
    private final SettingsController sc;

    public RevisionSystemScreen(AuthenticationController ac, SettingsController sC) {
        this.ac = ac;
        this.sc = sC;
    }

    public Scene create(){
        Pane root = new Pane();
        root.getStyleClass().add("screens");
        Scene scene = new Scene(root, 1920, 1057);
        Group background = createBackground("Revision System");
        getTitle().setLayoutX(275);
        ImageView back = getSwitchScreenBackView(ac);

        Group contentLayer = new Group();

        RectangleButtonBundle fcManagement = ui.buildRectangularButtonBundle(rectW, rectH, "Flashcards", "buttonText", 310, 250);
        ImageView fcmIcon = createIcon(310, 245, "/images/RevisionSystemScreen/flashcardmanagement.png");
        fcManagement.button().setOnMouseClicked(_ -> {
            try {
                fcManagementMouseClick();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        RectangleButtonBundle studyModes = ui.buildRectangularButtonBundle(rectW, rectH, "Study Modes", "buttonText", 310,  420);
        ImageView smIcon = createIcon(310, 415, "/images/RevisionSystemScreen/studymodes.png");
        studyModes.button().setOnMouseClicked(_ -> {
            try {
               smMouseClick();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        RectangleButtonBundle impOrExp = ui.buildRectangularButtonBundle(rectW, rectH, "Import/Export", "buttonText", 310, 591);
        ImageView impIcon = createIcon(310, 586, "/images/RevisionSystemScreen/import.png");

        RectangleButtonBundle analytics = ui.buildRectangularButtonBundle(rectW, rectH, "Analytics", "buttonText", 310, 762);
        ImageView aIcon = createIcon(310, 757, "/images/RevisionSystemScreen/analytics.png");

        Group buttons = new Group(fcManagement.button(), studyModes.button(), impOrExp.button(), analytics.button(), fcmIcon, smIcon, impIcon, aIcon);
        contentLayer.getChildren().add(buttons);
        root.getChildren().addAll(background, back, contentLayer);
        return scene;
    }

    private void fcManagementMouseClick() throws IOException {
        ScreenManager.switchScene(new FlashcardManagementScreen(ac, sc).create(), ac.getCurrentUser().getEmail());
    }

    private void smMouseClick() throws IOException {
        ScreenManager.switchScene(new StudyModesScreen(ac, sc).create(), ac.getCurrentUser().getEmail());
    }

}
