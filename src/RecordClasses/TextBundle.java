package RecordClasses;

import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
// Record class that bundles together the components of a text input field with an icon.
// This bundle contains:
//  - group: A Group containing the background rectangle and the TextField
//  - tf: The actual TextField where users enter text
//  - icon: An ImageView icon displayed alongside the input field (e.g., user icon, email icon)
public record TextBundle(Group group, TextField tf, ImageView icon) {
}
