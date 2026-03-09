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
    private int consecutiveCorrect;
    private ArrayList<Boolean> answerHistory = new ArrayList<>();

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
        consecutiveCorrect = 0;
    }

    // Returns true if session is complete
    public boolean recordAnswer(boolean correct){
        answerHistory.add(correct);
        // Remove flashcard from active queue
        Flashcard current = currentQueue.removeFirst();
        // add it to correct list if correct
        if(correct) correctList.add(current);
        else{
            // Add to incorrect list, and place at end of queue again
            incorrectList.add(current);
            currentQueue.add(current);
        }
        // Set to question side for next flashcard
        showingAnswer = false;
        // Return true if the session is complete
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
    public ArrayList<Boolean> getAnswerHistory() {
        return answerHistory;
    }
    public boolean isShowingAnswer() {
        return showingAnswer;
    }
    public LocalDateTime getDate() {
        return date;
    }
    public ArrayList<Flashcard> getCurrentQueue() {
        return currentQueue;
    }
    public Flashcard getCurrentCard(){
        return currentQueue.getFirst();
    }
    public int getRemainingCount(){
        return currentQueue.size();
    }
    public int getConsecutiveCorrect() {
        return consecutiveCorrect;
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

    public void incrementConsecutiveCorrect(){
        consecutiveCorrect++;
    }
    public void resetConsecutiveCorrect(){
        consecutiveCorrect = 0;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    // Method to restart the session with same card
    public void reset(ArrayList<Flashcard> lastCards) {
        // Clear the current cards
        currentQueue.clear();
        // add the last cards
        currentQueue.addAll(lastCards);
        // set to question side
        showingAnswer = false;
    }

}
