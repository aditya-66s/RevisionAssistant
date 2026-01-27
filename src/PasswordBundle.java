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
    private final ImageView leftIcon;

    public PasswordBundle(Group passwordGroup, PasswordField password, TextField copyPw, ImageView showIcon, ImageView hideIcon, ImageView leftIcon) {
        this.passwordGroup = passwordGroup;
        this.password = password;
        this.copyPw = copyPw;
        this.showIcon = showIcon;
        this.hideIcon = hideIcon;
        this.leftIcon = leftIcon;
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
    public ImageView getLeftIcon() {
        return leftIcon;
    }
    public Group getIconGroup() {
        return new Group(hideIcon, showIcon, leftIcon);
    }
}

