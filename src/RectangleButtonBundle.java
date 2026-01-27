import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class RectangleButtonBundle {
    private final Rectangle buttonRect;
    private final Text buttonText;
    private final StackPane button;

    public RectangleButtonBundle(Rectangle buttonRect, Text buttonText, StackPane button) {
        this.buttonRect = buttonRect;
        this.buttonText = buttonText;
        this.button = button;
    }

    public Rectangle getButtonRect() {
        return buttonRect;
    }
    public Text getButtonText() {
        return buttonText;
    }
    public StackPane getButton() {
        return button;
    }
}
