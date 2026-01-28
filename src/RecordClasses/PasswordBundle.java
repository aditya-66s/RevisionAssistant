package RecordClasses;

import javafx.scene.Group;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public record PasswordBundle(Group passwordGroup, PasswordField password, TextField copyPw, ImageView showIcon,
                             ImageView hideIcon, ImageView leftIcon) {
    public Group getIconGroup() {
        return new Group(hideIcon, showIcon, leftIcon);
    }
}

