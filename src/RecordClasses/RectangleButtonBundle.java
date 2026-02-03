package RecordClasses;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
// Record class that bundles together the components of a rectangular button.
// This bundle contains:
//  - buttonRect: The Rectangle shape that forms the button's background
//  - buttonText: The Text element displayed on the button
//  - button: The StackPane container that holds both the rectangle and text together

public record RectangleButtonBundle(Rectangle buttonRect, Text buttonText, StackPane button) {
}
