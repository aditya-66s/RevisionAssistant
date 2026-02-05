package Screens;

import Components.UIComponents;
import Controllers.AuthenticationController;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;


import java.util.Objects;

import static javafx.scene.text.TextAlignment.CENTER;

public abstract class SystemScreens {
    public final UIComponents ui = new UIComponents();
    public final AuthenticationController ac = new AuthenticationController();
    // Constant rectangular button dimensions set
    public final int rectW = 1300;
    public final int rectH = 133;
    private Text title;

    public Group createBackground(String titleText){
        title = new Text(titleText);
        title.setTextAlignment(CENTER);
        title.getStyleClass().add("title");
        title.setLayoutY(160);
        Rectangle background = new Rectangle(108, 194, 1704,745);
        background.getStyleClass().add("backgroundRect");
        return new Group(title, background, ui.createLogoView());
    }

    public ImageView createIcon(int iconX, int iconY, String iconURL){
        ImageView iconView = new ImageView(Objects.requireNonNull(getClass().getResource(iconURL)).toExternalForm());
        iconView.getStyleClass().add("icons");
        iconView.setLayoutX(iconX);
        iconView.setLayoutY(iconY);
        return iconView;
    }
//    public void setTitle(String titleText) {
//        this.title = new Text(titleText);
//    }

    public Text getTitle(){
        return title;
    }
}
