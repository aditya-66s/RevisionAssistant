package Models;

import java.time.LocalDateTime;
import java.util.ArrayList;


public class Session {
    private String selectedDeck;
    private String studyMode;
    private final ArrayList<Flashcard> currentQueue;
    private final ArrayList<Flashcard> correctList;
    private final ArrayList<Flashcard> incorrectList;
    private boolean showingAnswer;
    private long elapsedTime;
    private LocalDateTime date;

    // New session
    public Session(ArrayList<Flashcard> cards, String selectedDeck, String studyMode){
        this.selectedDeck = selectedDeck;
        this.studyMode = studyMode;
        this.currentQueue = new ArrayList<>(cards);
        this.correctList = new ArrayList<>();
        this.incorrectList = new ArrayList<>();
        this.elapsedTime = 0;
        this.showingAnswer = false;
        this.date = LocalDateTime.now();
    }

    // Returns true if session is complete
    public boolean recordAnswer(boolean correct){
        Flashcard current = currentQueue.removeFirst();
        if(correct) correctList.add(current);
        else{
            incorrectList.add(current);
            currentQueue.add(current);
        }
        showingAnswer = false;
        return currentQueue.isEmpty();
    }

    // Getters and Setters
    public String getSelectedDeck() {
        return selectedDeck;
    }
    public String getStudyMode() {
        return studyMode;
    }
    public int getIncorrectCount() {
        return incorrectList.size();
    }
    public int getCorrectCount() {
        return correctList.size();
    }
    public long getElapsedTime() {
        return elapsedTime;
    }
    public boolean isShowingAnswer() {
        return showingAnswer;
    }
    public LocalDateTime getDate() {
        return date;
    }
    public Flashcard getCurrentCard(){
        return currentQueue.getFirst();
    }
    public int getRemainingCount(){
        return currentQueue.size();
    }
    public double getAccuracy(){
        int total = correctList.size() + incorrectList.size();
        return (double) (correctList.size() * 100) / total;
    }
    public String getProgressText(){
        return "Remaining: " + currentQueue.size() + "\n Correct: " + correctList.size();
    }

    public void setShowingAnswer(boolean showingAnswer){
        this.showingAnswer = showingAnswer;
    }

    public void tick(){elapsedTime++;}


    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void reset(ArrayList<Flashcard> lastCards) {
        currentQueue.clear();
        currentQueue.addAll(lastCards);
        showingAnswer = false;
    }
}
