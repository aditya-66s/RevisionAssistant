package RecordClasses;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;


public record TextAreaBundle(TextArea textArea, Label characterCounter, int charLimit, int lowerLimit) {
}
