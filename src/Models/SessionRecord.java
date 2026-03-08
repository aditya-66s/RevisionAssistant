package Models;

import java.util.ArrayList;

public class SessionRecord {
    private String date;
    private double accuracy;
    private long studySeconds;
    private int correct;
    private int incorrect;
    private String deckName;
    private ArrayList<Boolean> answerHistory; // correct = t, incorrect = f

    // Constructor
    public SessionRecord(String date, double accuracy, long studySeconds,
                         int correct, int incorrect, String deckName, ArrayList<Boolean> answerHistory) {
        this.date = date;
        this.accuracy = accuracy;
        this.studySeconds = studySeconds;
        this.correct = correct;
        this.incorrect = incorrect;
        this.deckName = deckName;
        this.answerHistory = answerHistory;
    }

    // Getters
    public String getDate() {
        return date;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public long getStudySeconds() {
        return studySeconds;
    }

    public int getCorrect() {
        return correct;
    }

    public int getIncorrect() {
        return incorrect;
    }

    public String getDeckName() {
        return deckName;
    }

    public ArrayList<Boolean> getAnswerHistory() {
        return answerHistory;
    }
}
