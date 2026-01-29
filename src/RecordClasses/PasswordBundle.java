package RecordClasses;

import javafx.scene.Group;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

// This is the most complex bundle record, containing multiple components needed to create a password field that can toggle between hidden (masked) and visible (plain text) modes.
// This bundle contains:
//  - passwordGroup: A Group containing the background rectangle, PasswordField, and TextField
//  - password: The PasswordField that displays masked characters (e.g., ••••••)
//  - copyPw: A TextField that mirrors the password content for "show password" functionality
//  - showIcon: ImageView icon for showing the password (eye icon typically)
//  - hideIcon: ImageView icon for hiding the password (crossed-eye icon typically)
//  - leftIcon: ImageView icon displayed on the left side (typically a lock icon)
public record PasswordBundle(Group passwordGroup, PasswordField password, TextField copyPw, ImageView showIcon,
                             ImageView hideIcon, ImageView leftIcon) {
    public Group getIconGroup() {
        return new Group(hideIcon, showIcon, leftIcon);
    }
}

