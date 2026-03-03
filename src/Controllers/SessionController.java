package Controllers;

import Models.Flashcard;
import Models.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class SessionController {
    private final String email;
    private Session session;
    private FlashcardController fc;


    public SessionController(String email, FlashcardController fc) {
        this.email = email;
        this.fc = fc;
    }

    public void startSession(ArrayList<Flashcard> cards, String deck, String mode){
        this.session = new Session(cards, deck, mode);
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

    public boolean recordAnswer(boolean correct) throws IOException {
        if(!correct) fc.saveWrongAnswers(session.getCurrentCard());
        return session.recordAnswer(correct);
    }

    public void tick(){
        session.tick();
    }
    public String getFormattedTime(){
        return formatTime(session.getElapsedTime());
    }

    // Getters
    public ArrayList<Flashcard> loadWrongAnswers() throws IOException {
        return fc.loadWrongAnswers();
    }
    public Flashcard getCurrentCard(){return session.getCurrentCard();}
    public String getSelectedDeck() {
        return session.getSelectedDeck();
    }
    public double getAccuracy(){
        return session.getAccuracy();
    }
    public String getStudyMode() {
        return session.getStudyMode();
    }
    public int getRemainingCount() {
        return session.getRemainingCount();
    }
    public int getIncorrectCount() {
        return session.getIncorrectCount();
    }
    public int getCorrectCount() {
        return session.getCorrectCount();
    }
    public long getElapsedTime() {
        return session.getElapsedTime();
    }
    public void setShowingAnswer(boolean showingAnswer) {
        session.setShowingAnswer(showingAnswer);
    }
    public boolean isShowingAnswer() {
        return session.isShowingAnswer();
    }
    public String getProgressText(){
        return session.getProgressText();
    }

    public String formatTime(Long seconds){
        return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
    }

}
