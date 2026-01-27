import javafx.scene.Group;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class PasswordBundle {
    private final Group inputs;
    private final PasswordField password;
    private final TextField copyPw;
    private final ImageView showIcon;
    private final ImageView hideIcon;

    public PasswordBundle(Group inputs, PasswordField password, TextField copyPw, ImageView showIcon, ImageView hideIcon){
        this.inputs = inputs;
        this.password = password;
        this.copyPw = copyPw;
        this.showIcon = showIcon;
        this.hideIcon = hideIcon;
    }
    public PasswordField getPassword() {
        return password;
    }

    public TextField getCopyPw() {
        return copyPw;
    }

    public Group getInputs() {
        return inputs;
    }

    public ImageView getHideIcon() {
        return hideIcon;
    }

    public ImageView getShowIcon() {
        return showIcon;
    }
}

