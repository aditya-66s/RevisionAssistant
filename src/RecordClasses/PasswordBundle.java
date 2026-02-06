package RecordClasses;

import javafx.scene.Group;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

/**
 * This is the most complex bundle record, containing multiple components needed to create a password field that can toggle between hidden (masked) and visible (plain text) modes.
 * This bundle contains:
 * @param passwordGroup: A Group containing the background rectangle, PasswordField, and TextField
 * @param password: The PasswordField that displays masked characters (e.g., ••••••)
 * @param copyPw: A TextField that mirrors the password content for "show password" functionality
 * @param showIcon: ImageView icon for showing the password (eye icon typically)
 * @param hideIcon: ImageView icon for hiding the password (crossed-eye icon typically)
 * @param leftIcon: ImageView icon displayed on the left side (typically a lock icon)
 */

public record PasswordBundle(Group passwordGroup, PasswordField password, TextField copyPw, ImageView showIcon,
                             ImageView hideIcon, ImageView leftIcon) {
    public Group getIconGroup() {
        return new Group(hideIcon, showIcon, leftIcon);
    }
}

