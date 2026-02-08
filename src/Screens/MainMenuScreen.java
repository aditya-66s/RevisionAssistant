package Screens;

import Components.CustomAlert;
import Components.ScreenManager;
import Controllers.AuthenticationController;
import Controllers.SettingsController;
import RecordClasses.RectangleButtonBundle;
import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Objects;

public class MainMenuScreen extends SystemScreens {
    private final AuthenticationController ac;
    private final SettingsController sc;

    public MainMenuScreen(AuthenticationController ac, SettingsController sC) {
        this.ac = ac;
        this.sc = sC;
    }

    public Scene create(){
        Pane root = new Pane();
        root.getStyleClass().add("screens");
        // Creating the background rectangle using the inherited method from the SystemScreens Class
        Group background = createBackground("Main Menu");
        getTitle().setLayoutX(510);
        // Creating buttons bundles for all
        RectangleButtonBundle revisionSystem = ui.buildRectangularButtonBundle(rectW, rectH, "Revision System", "buttonText", 310, 250);
        revisionSystem.button().setOnMouseClicked(_ -> revisionSystemMouseClick());
        ImageView rsIcon = createIcon(310, 245, "/images/MainMenuScreen/books.png");

        RectangleButtonBundle userManagement = ui.buildRectangularButtonBundle(rectW, rectH, "User Management", "buttonText", 310,  420);
        userManagement.button().setOnMouseClicked(_ -> userManagementMouseClick());
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
        root.getChildren().addAll(background, buttons, icons);
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
        ScreenManager.switchScene(new SettingsScreen(ac, sc).create(), ac.getCurrentUser().getEmail());
    }

    private void userManagementMouseClick() {
    }

    private void revisionSystemMouseClick() {
    }
}
