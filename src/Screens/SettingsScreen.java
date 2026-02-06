package Screens;

import Components.ScreenManager;
import RecordClasses.RectangleButtonBundle;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.Objects;

public class SettingsScreen extends SystemScreens{
    private final Group background = createBackground("System Settings");
//    private final ImageView back = ui.createBRImageView("back");

    public Scene create() {
        Pane root = new Pane();
        root.getStyleClass().add("screens");
        getTitle().setLayoutX(130);
        ImageView back = ui.createBRImageView("back");
        back.setOnMouseClicked(_ -> ScreenManager.goBack());

        Group contentLayer = new Group();
        // Creating Buttons for Settings
        RectangleButtonBundle stylesheetOptions = ui.buildRectangularButtonBundle(rectW, rectH, "Stylesheet Options", "buttonText", 310, 241);
        ImageView sOIcon = createIcon(310, 239, "/images/SettingsScreen/stylesheets.png");

        RectangleButtonBundle accessibility = ui.buildRectangularButtonBundle(rectW, rectH, "Accessibility", "buttonText", 310, 412);
        ImageView aIcon = createIcon(310, 412, "/images/SettingsScreen/accessibility.png");

        RectangleButtonBundle audioControl = ui.buildRectangularButtonBundle(rectW, rectH, "Audio Control", "buttonText", 310, 583);
        ImageView aCIcon = createIcon(310, 583, "/images/SettingsScreen/audio.png");

        RectangleButtonBundle others = ui.buildRectangularButtonBundle(rectW, rectH, "Others", "buttonText", 310, 755);
        ImageView oIcon = createIcon(310, 755, "/images/SettingsScreen/others.png");
        others.button().setOnMouseClicked(_ -> buildOthersContent(contentLayer));

        Group menuLayer = new Group(stylesheetOptions.button(), accessibility.button(), audioControl.button(), others.button(), sOIcon, aIcon, aCIcon, oIcon);
        contentLayer.getChildren().add(menuLayer);
        root.getChildren().addAll(background, back, contentLayer);
        Scene scene = new Scene(root, 1920, 1057);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());
        return scene;
    }

    private void buildOthersContent(Group contentLayer) {
        RectangleButtonBundle changeEmail = ui.buildRectangularButtonBundle(rectW, rectH, "Change Email", "buttonText", 293, 315);
        ImageView emailIcon = createIcon(293, 310, "/images/SettingsScreen/whiteUser.png");

        RectangleButtonBundle changeRw = ui.buildRectangularButtonBundle(rectW, rectH, "Change Word", "buttonText", 293, 485);
        ImageView recoveryIcon = createIcon(293, 480, "/images/SettingsScreen/whiteRecovery.png");

        RectangleButtonBundle changePw = ui.buildRectangularButtonBundle(rectW, rectH, "Change Password", "buttonText", 293, 656);
        ImageView pwIcon = createIcon(293, 651, "/images/SettingsScreen/whiteLock.png");

        Group otherOptionsLayer = new Group(changeEmail.button(), changePw.button(), changeRw.button(), emailIcon, recoveryIcon, pwIcon);
        ImageView back = ui.createBRImageView("back");
        back.setOnMouseClicked(_ -> nav.goBack(contentLayer));

        nav.switchContent(contentLayer, otherOptionsLayer, back);
    }

}
