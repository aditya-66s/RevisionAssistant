package Controllers;

import Models.Flashcard;

import java.util.ArrayList;
import java.util.Random;

public class SessionController {
    private final String email;
    private ArrayList<Flashcard> sessionCards;
    private String selectedDeck = "";
    private String studyMode = "";
    private int currentIndex = 0;
    private int correctCount = 0;
    private int incorrectCount = 0;
    private boolean showingAnswer = false;
    private long elapsedTime = 0;

    public SessionController(String email) {
        this.email = email;
    }

    // Shuffle ArrayList of cards for Shuffle mode
    public void shuffle(ArrayList<Flashcard> cards) {
        // Create a random object to generate random indices
        Random rand = new Random();
        // Starting at the last element and move backwards
        for(int i = cards.size()-1; i > 0; i--) {
            // Generate a random index between 0 and i inclusive
            int j = rand.nextInt(i+1);
            // Store element at index i temporarily
            Flashcard temp = cards.get(i);
            // Replace element at index i with element at index j
            cards.set(i, cards.get(j));
            // Place stored element into index j
            cards.set(j, temp);

        }
    }

    // Getters and Setters
    public String getSelectedDeck() {
        return selectedDeck;
    }
    public String getStudyMode() {
        return studyMode;
    }
    public int getCurrentIndex() {
        return currentIndex;
    }
    public int getIncorrectCount() {
        return incorrectCount;
    }
    public int getCorrectCount() {
        return correctCount;
    }
    public long getElapsedTime() {
        return elapsedTime;
    }
    public boolean isShowingAnswer() {
        return showingAnswer;
    }
    public String getProgressText(){
        return "Card " + (currentIndex+1) + " / " + sessionCards.size();
    }
    public String getTimeFormated(Long seconds){
        return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
    }
    public void setSelectedDeck(String selectedDeck) {
        this.selectedDeck = selectedDeck;
    }
    public void setStudyMode(String studyMode) {
        this.studyMode = studyMode;
    }
    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }
    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }
    public void setIncorrectCount(int incorrectCount) {
        this.incorrectCount = incorrectCount;
    }
    public void setShowingAnswer(boolean showingAnswer) {
        this.showingAnswer = showingAnswer;
    }
    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}
