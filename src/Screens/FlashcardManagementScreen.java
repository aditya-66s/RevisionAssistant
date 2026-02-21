package Screens;

import Controllers.AuthenticationController;
import Controllers.SettingsController;
import RecordClasses.RectangleButtonBundle;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class FlashcardManagementScreen extends SystemScreens{
    private final AuthenticationController ac;
    private final SettingsController sc;

    public FlashcardManagementScreen(AuthenticationController ac, SettingsController sC) {
        this.ac = ac;
        this.sc = sC;
    }
    public Scene create(){
        Pane root = new Pane();
        root.getStyleClass().add("screens");
        Scene scene = new Scene(root, 1920, 1057);
        Group background = createBackground("Flashcard Control");
        getTitle().setLayoutX(200);
        ImageView back = getSwitchScreenBackView(ac);

        Group contentLayer = new Group();

        RectangleButtonBundle create = ui.buildRectangularButtonBundle(rectW, rectH, "Create", "buttonText", 293, 315);
        ImageView createIcon = createIcon(293, 310, "/images/FlashcardManagementScreen/create.png");

        RectangleButtonBundle modify = ui.buildRectangularButtonBundle(rectW, rectH, "Modify", "buttonText", 293, 485);
        ImageView modifyIcon = createIcon(293, 480, "/images/FlashcardManagementScreen/modify.png");

        RectangleButtonBundle organise = ui.buildRectangularButtonBundle(rectW, rectH, "Organise", "buttonText", 293, 656);
        ImageView organiseIcon = createIcon(293, 651, "/images/FlashcardManagementScreen/sort.png");

        Group buttons = new Group(create.button(), modify.button(), organise.button(), createIcon, modifyIcon, organiseIcon);
        contentLayer.getChildren().add(buttons);
        root.getChildren().addAll(background, back, contentLayer);
        return scene;
    }
}
