package Models;

import java.time.LocalDateTime;


public class Session {
    private String selectedDeck;
    private String studyMode;
    private int currentIndex;
    private int correctCount;
    private int incorrectCount;
    private boolean showingAnswer;
    private long elapsedTime;
    private LocalDateTime date;

    // New session
    public Session(String selectedDeck, String studyMode, int correctCount, int incorrectCount, long elapsedTime){
        this.selectedDeck = selectedDeck;
        this.studyMode = studyMode;
        this.correctCount = correctCount;
        this.incorrectCount = incorrectCount;
        this.elapsedTime = elapsedTime;
        this.date = LocalDateTime.now();
    }
    // Old sessions
    public Session(String selectedDeck, String studyMode, int correctCount, int incorrectCount, long elapsedTime, LocalDateTime date){
        this.selectedDeck = selectedDeck;
        this.studyMode = studyMode;
        this.correctCount = correctCount;
        this.incorrectCount = incorrectCount;
        this.elapsedTime = elapsedTime;
        this.date = date;
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
    public LocalDateTime getDate() {
        return date;
    }
    public String getTimeFormated(){
        long seconds = elapsedTime;
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
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
