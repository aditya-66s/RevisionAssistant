import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class TextBundle {
    private final Group group;
    private final TextField tf;
    private final ImageView icon;

    public TextBundle(Group group, TextField tf, ImageView icon) {
        this.group = group;
        this.tf = tf;
        this.icon = icon;
    }

    public TextField getTf() {
        return tf;
    }

    public Group getGroup() {
        return group;
    }
    public ImageView getIcon() {
        return icon;
    }
}
