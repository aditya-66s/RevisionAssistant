package Screens;

import Components.CustomAlert;
import Components.ScreenManager;
import Components.UIComponents;
import RecordClasses.RectangleButtonBundle;
import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

import java.util.EventObject;
import java.util.Objects;

public class MainMenuScreen extends SystemScreens {
    public Scene create(){
        Pane root = new Pane();
        root.getStyleClass().add("screens");
        // Creating the background rectangle using the inherited method from the SystemScreens Class
        Group background = createBackground("Main Menu");
        getTitle().setLayoutX(450);
        // Creating buttons bundles for all
        RectangleButtonBundle revisionSystem = ui.buildRectangularButtonBundle(rectW, rectH, "Revision System", "buttonText", 310, 250);
        revisionSystem.button().setOnMouseClicked(event -> revisionSystemMouseClick());
        ImageView rsIcon = createIcon(310, 245, "/images/books.png");

        RectangleButtonBundle userManagement = ui.buildRectangularButtonBundle(rectW, rectH, "User Management", "buttonText", 310,  420);
        userManagement.button().setOnMouseClicked(event -> userManagementMouseClick());
        ImageView umIcon = createIcon(310, 415, "/images/userbook.png");

        RectangleButtonBundle settings = ui.buildRectangularButtonBundle(rectW, rectH, "Settings", "buttonText", 310, 591);
        settings.button().setOnMouseClicked(event -> settingsMouseClick());
        ImageView sIcon = createIcon(310, 586, "/images/settings.png");

        RectangleButtonBundle logout = ui.buildRectangularButtonBundle(rectW, rectH, "Logout", "buttonText", 310, 762);
        logout.button().setOnMouseClicked(this::logout);
        ImageView lIcon = createIcon(310, 757, "/images/door.png");

        Group buttons = new Group(revisionSystem.button(), userManagement.button(), settings.button(), logout.button());
        Group icons = new Group(rsIcon, umIcon, sIcon, lIcon);
        root.getChildren().addAll(background, buttons, icons);
        Scene scene = new Scene(root, 1920, 1057);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());
        return scene;
    }

    private void logout(Event e) {
        Window alertWindow = ((Node) e.getSource()).getScene().getWindow();
        if(CustomAlert.confirm(alertWindow, "Logout Confirmation", "Are you sure you would like to log out?")){
            ac.logout();
        }
    }

    private void settingsMouseClick() {
        ScreenManager.switchScene(new SettingsScreen().create());
    }

    private void userManagementMouseClick() {
    }

    private void revisionSystemMouseClick() {
    }
}
