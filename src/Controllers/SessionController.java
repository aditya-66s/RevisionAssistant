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
    // If user want to continue/retry
    private ArrayList<Flashcard> lastCards;

    // Constructor
    public SessionController(String email, FlashcardController fc) {
        this.email = email;
        this.fc = fc;
    }
    // Start session
    public void startSession(ArrayList<Flashcard> cards, String deck, String mode){
        this.lastCards = new ArrayList<>(cards);
        // Create a new session object
        this.session = new Session(cards, deck, mode);
    }
    // Retry
    public void retrySession(){
        // Shuffle cards
        if(getStudyMode().equals("Shuffle")) shuffle(lastCards);
        // Continue session
        session.reset(lastCards);
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
    // Function to record answer
    public boolean recordAnswer(boolean correct) throws IOException {
        // add to wrongAnswers.csv to
        if(!correct) {
            fc.saveWrongAnswers(session.getCurrentCard());
            session.resetConsecutiveCorrect();
        }else session.incrementConsecutiveCorrect();
        // Update it in the session object
        return session.recordAnswer(correct);
    }
    // Increase by a second
    public void tick(){
        session.tick();
    }
    // Function to call the function format time
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
    public ArrayList<Flashcard> getCurrentQueue() {
        return session.getCurrentQueue();
    }

    public ArrayList<Flashcard> getLastCards(){
        return this.lastCards;
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
    public Session getSession(){
        return session;
    }

    public void setLastCards(ArrayList<Flashcard> lastCards) {
        this.lastCards = lastCards;
    }
    //  Format the time in 00:00:00
    public String formatTime(Long seconds){
        return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
    }

}
