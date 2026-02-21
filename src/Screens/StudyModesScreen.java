package Screens;

import Controllers.AuthenticationController;
import Controllers.SettingsController;
import RecordClasses.RectangleButtonBundle;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class StudyModesScreen extends SystemScreens{
    private final AuthenticationController ac;
    private final SettingsController sc;

    public StudyModesScreen(AuthenticationController ac, SettingsController sC) {
        this.ac = ac;
        this.sc = sC;
    }
    public Scene create(){
        Pane root = new Pane();
        root.getStyleClass().add("screens");
        Scene scene = new Scene(root, 1920, 1057);
        Group background = createBackground("Study Modes");
        getTitle().setLayoutX(420);
        ImageView back = getSwitchScreenBackView(ac);

        Group contentLayer = new Group();

        RectangleButtonBundle serial = ui.buildRectangularButtonBundle(rectW, rectH, "Serial Mode", "buttonText", 293, 315);
        ImageView serialIcon = createIcon(293, 310, "/images/StudyModesScreen/serial.png");

        RectangleButtonBundle wrongAnswers = ui.buildRectangularButtonBundle(rectW, rectH, "Wrong Answers", "buttonText", 293, 485);
        ImageView wrongAnswersIcon = createIcon(293, 480, "/images/StudyModesScreen/wrongAnswers.png");

        RectangleButtonBundle shuffle = ui.buildRectangularButtonBundle(rectW, rectH, "Shuffle Mode", "buttonText", 293, 656);
        ImageView shuffleIcon = createIcon(293, 651, "/images/StudyModesScreen/shuffle.png");

        Group buttons = new Group(serial.button(), wrongAnswers.button(), shuffle.button(), serialIcon, wrongAnswersIcon, shuffleIcon);
        contentLayer.getChildren().add(buttons);
        root.getChildren().addAll(background, back, contentLayer);
        return scene;
    }
}
