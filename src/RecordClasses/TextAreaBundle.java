package RecordClasses;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * Bundle to create a text area, linked with a character counter
 * This bundle contains:
 * @param textArea: Input field for the user
 * @param characterCounter: the text for the character counter
 * @param charLimit: limit beyond character counter goes red
 * @param lowerLimit: limit of when the characters is less than, the character counter goes red
 */
public record TextAreaBundle(TextArea textArea, Label characterCounter, int charLimit, int lowerLimit) {
}
