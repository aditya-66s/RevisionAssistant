package Models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
// Class to represent a flashcard
// Using StringProperty so data can be observed and bound to table
public class Flashcard {
    // JavaFX property representing the front of card
    private SimpleStringProperty question;
    // JavaFX property representing the back of card
    private SimpleStringProperty answer;

    // Constructor to initialise a flashcard
    public Flashcard(String question, String answer){
        this.question = new SimpleStringProperty(question);
        this.answer = new SimpleStringProperty(answer);
    }
    // returns JavaFX property for the question and answer for table binding
    public StringProperty questionProperty() { return question; }
    public StringProperty answerProperty() { return answer; }

    // Setters
    public void setAnswer(String answer) {
        this.answer.set(answer);
    }

    public void setQuestion(String question) {
        this.question.set(question);
    }

    // Getters
    public String getQuestion() {
        return question.get();
    }

    public String getAnswer() {
        return answer.get();
    }
}
