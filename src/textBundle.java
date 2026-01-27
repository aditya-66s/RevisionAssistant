import javafx.scene.Group;
import javafx.scene.control.TextField;

public class textBundle {
    private final Group group;
    private final TextField tf;

    public textBundle(Group group, TextField tf) {
        this.group = group;
        this.tf = tf;
    }

    public TextField getTf() {
        return tf;
    }

    public Group getGroup() {
        return group;
    }
}
