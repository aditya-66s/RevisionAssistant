import javafx.scene.Group;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class PasswordBundle {
    private final Group passwordGroup;
    private final PasswordField password;
    private final TextField copyPw;
    private final ImageView showIcon;
    private final ImageView hideIcon;

    public PasswordBundle(Group passwordGroup, PasswordField password, TextField copyPw, ImageView showIcon, ImageView hideIcon){
        this.passwordGroup = passwordGroup;
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

    public Group getPasswordGroup() {
        return passwordGroup;
    }

    public ImageView getHideIcon() {
        return hideIcon;
    }

    public ImageView getShowIcon() {
        return showIcon;
    }
}

