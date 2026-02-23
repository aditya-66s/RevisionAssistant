package Models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Flashcard {
    private SimpleStringProperty question;
    private SimpleStringProperty answer;

    public Flashcard(String question, String answer){
        this.question = new SimpleStringProperty(question);
        this.answer = new SimpleStringProperty(answer);
    }
    public StringProperty frontProperty() { return question; }
    public StringProperty backProperty() { return answer; }

    public void setAnswer(String answer) {
        this.answer = new SimpleStringProperty(answer);
    }

    public void setQuestion(String question) {
        this.question = new SimpleStringProperty(question);
    }

    public String getQuestion() {
        return question.get();
    }

    public String getAnswer() {
        return answer.get();
    }
}
