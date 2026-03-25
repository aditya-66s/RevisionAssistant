package Screens;

import Components.CustomAlert;
import Components.ScreenManager;
import Controllers.*;
import Models.Flashcard;
import Models.UserStats;
import RecordClasses.RectangleButtonBundle;
import javafx.animation.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Objects;


public class StudyModesScreen extends SystemScreens{
    // Controllers
    private final AuthenticationController ac;
    private final SettingsController sc;
    private final FlashcardController fc;
    private final SessionController sessionC;
    private TTSController tts;
    private final StatsController stats;
    // Content filling revision system to be modified globally
    private Text correctText;
    private Text incorrectText;
    private Label timerLabel;
    private Text progressText;
    private Text cardLabel;
    private Text cardBody;
    private Rectangle cardRect;
    // Flags
    private boolean hasFlipped = false;
    private boolean hasSessionStarted = false;
    private boolean isRetrySession = false;
    // Timer
    private Timeline timer;

    // Constructor to start a NEW Study Session
    public StudyModesScreen(AuthenticationController ac, SettingsController sC, FlashcardController fc, StatsController stats) throws IOException {
        this.ac = ac;
        this.sc = sC;
        this.fc = fc;
        this.sessionC = new SessionController(ac.getEmail(), fc);
        // only load if tts is enabled
        if(sc.getCurrentSettings().isTTSEnabled()) this.tts = new TTSController(sc.getTTSVolume());
        this.stats = stats;
    }
    // Constructor to continue a prior Study Session
    public StudyModesScreen(AuthenticationController ac, SettingsController sC, FlashcardController fc, SessionController sessionC, StatsController stats) throws IOException {
        this.ac = ac;
        this.sc = sC;
        this.fc = fc;
        this.sessionC = sessionC;
        // Only load if tts is enabled
        if(sc.getCurrentSettings().isTTSEnabled()) this.tts = new TTSController(sc.getTTSVolume());
        this.stats = stats;
    }

    // Create initial content where user can select study modes
    public Scene create(){
        Pane root = new Pane();
        root.getStyleClass().add("screens");
        Scene scene = new Scene(root, 1920, 1057);
        Group background = createBackground("Study Modes");
        getTitle().setLayoutX(420);
        ImageView back = getSwitchScreenBackView(ac);

        Group contentLayer = new Group();

        RectangleButtonBundle serial = ui.buildRectangularButtonBundle(rectW, rectH, "Serial Mode", "buttonText", 293, 315);
        ImageView serialIcon = createIcon(293, 310, "/images/StudyModesScreen/serial.png");
        serial.button().setOnMouseClicked(_-> buildDeckSelector(contentLayer, "Serial"));

        RectangleButtonBundle wrongAnswers = ui.buildRectangularButtonBundle(rectW, rectH, "Wrong Answers", "buttonText", 293, 485);
        ImageView wrongAnswersIcon = createIcon(293, 480, "/images/StudyModesScreen/wrongAnswers.png");
        wrongAnswers.button().setOnMouseClicked(_-> {
            try {
                buildWrongAnswersMode(contentLayer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        RectangleButtonBundle shuffle = ui.buildRectangularButtonBundle(rectW, rectH, "Shuffle Mode", "buttonText", 293, 656);
        ImageView shuffleIcon = createIcon(293, 651, "/images/StudyModesScreen/shuffle.png");
        shuffle.button().setOnMouseClicked(_-> buildDeckSelector(contentLayer, "Shuffle"));

        Group buttons = new Group(serial.button(), wrongAnswers.button(), shuffle.button(), serialIcon, wrongAnswersIcon, shuffleIcon);
        contentLayer.getChildren().add(buttons);
        root.getChildren().addAll(background, back, contentLayer);
        return scene;
    }

    // Content where user can select a deck
    private void buildDeckSelector(Group contentLayer, String mode){
        Window owner = contentLayer.getScene().getWindow();
        // Create table with decks
        TableView<String> decksTable = new TableView<>();
        decksTable.getStyleClass().add("cardTable");
        decksTable.setMinHeight(500);
        TableColumn<String, String> deckNameColumn = new TableColumn<>("Deck Names");
        deckNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        decksTable.getColumns().add(deckNameColumn);
        decksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        decksTable.getItems().addAll(fc.getFileContents());
        Label deckTablesPh = new Label("No decks found");
        deckTablesPh.setStyle("-fx-text-fill: red; -fx-wrap-text: true; -fx-text-alignment: center;");
        decksTable.setPlaceholder(deckTablesPh);
        // Start session button which creates the session
        RectangleButtonBundle startSession = ui.buildRectangularButtonBundle(500, 100, "Start Session", "smallButtonText", 760, 0);
        startSession.button().setOnMouseClicked(_->{
            String deckName = decksTable.getSelectionModel().getSelectedItem();
            if(deckName == null){
                CustomAlert.error(owner, "Select a deck before starting a session");
                return;
            }
            try{
                ArrayList<Flashcard> cards = fc.loadFlashcardDeck(deckName);
                // If shuffle mode, shuffle the cards
                if(mode.equals("Shuffle")) sessionC.shuffle(cards);
                sessionC.startSession(cards, deckName, mode);
                sessionC.setLastCards(new ArrayList<>(cards));
                buildSessionContent(contentLayer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        // Get preview and search icon
        HBox display = getPreviewDecksAndCards(fc, decksTable, owner, startSession.button());
        nav.switchContent(contentLayer, display, getBackView(contentLayer));
    }

    // Create content where flashcard is displayed with buttons
    private void buildSessionContent(Group contentLayer){
        // Timer text
        timerLabel = new Label(sessionC.getFormattedTime());
        timerLabel.getStyleClass().add("timerLabel");
        timerLabel.setLayoutX(700); timerLabel.setLayoutY(940);

        progressText = new Text(sessionC.getProgressText());
        progressText.getStyleClass().add("infoText");
        progressText.setLayoutX(1400); progressText.setLayoutY(225);
        // Actual flashcard
        StackPane flashcard = buildCard();
        flashcard.setLayoutX(360); flashcard.setLayoutY(290);
        // TTS Icon
        ImageView ttsIcon = createIcon(100, 100, "/images/StudyModesScreen/tts.png");
        ttsIcon.setLayoutX(200); ttsIcon.setLayoutY(290);
        ttsIcon.setCursor(Cursor.HAND);

        ttsIcon.setOnMouseClicked(_->{
            // If showing answer, read out answer
            if(sessionC.isShowingAnswer()) tts.speak(sessionC.getCurrentCard().getAnswer());
            // If showing question, read out question
            else tts.speak(sessionC.getCurrentCard().getQuestion());
        });

        StackPane tick = buildFlashcardButton("✓","lightGreen");
        tick.setOnMouseClicked(_-> {
            try {
                handleAnswer(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        tick.setLayoutX(1035); tick.setLayoutY(760);
        StackPane cross = buildFlashcardButton("✗", "red");
        cross.setOnMouseClicked(_-> {
            try {
                handleAnswer(false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        cross.setLayoutX(685); cross.setLayoutY(760);
        // Text for correct count
        correctText = new Text("✓      " + sessionC.getCorrectCount());
        correctText.getStyleClass().add("correctC");
        correctText.setLayoutX(1520); correctText.setLayoutY(830);
        // Text for incorrect count
        incorrectText = new Text("✗      " + sessionC.getIncorrectCount());
        incorrectText.getStyleClass().add("incorrectC");
        incorrectText.setLayoutX(200); incorrectText.setLayoutY(830);
        // Extra info text
        Text mode = new Text(sessionC.getStudyMode() + "  ·  "
                + sessionC.getSelectedDeck());
        mode.getStyleClass().add("infoText");
        mode.setLayoutX(125); mode.setLayoutY(230); mode.setWrappingWidth(1350);
        Group newContent = new Group(timerLabel, progressText, flashcard, tick, cross, correctText, incorrectText, mode);

        Text rateDisplay = new Text("Rate: 1.0");
        rateDisplay.getStyleClass().add("infoText");
        ui.setPosition(rateDisplay, 110, 470);
        RectangleButtonBundle minus = ui.buildRectangularButtonBundle(40, 40, "-", "smallButtonText", 150, 480);
        RectangleButtonBundle plus = ui.buildRectangularButtonBundle(40, 40, "+", "smallButtonText", 250, 480);
        double[] rate = {1.0};
        tts.setRate(rate[0]);
        plus.button().setOnMouseClicked(_-> {
            tts.setRate(rate[0] + 0.1);
            rate[0] = tts.getRate();
            rateDisplay.setText(String.format("Rate: %.1f", rate[0]));
        });
        minus.button().setOnMouseClicked(_-> {
            tts.setRate(rate[0] - 0.1);
            rate[0] = tts.getRate();
            rateDisplay.setText(String.format("Rate: %.1f", rate[0]));
        });
        // If TTS is enabled, add the ttsIcon and rate option
        Group ttsGroup = new Group(ttsIcon, rateDisplay, minus.button(), plus.button());

        if(sc.getCurrentSettings().isTTSEnabled()) newContent.getChildren().add(ttsGroup);
        nav.switchContent(contentLayer, newContent, backButton(contentLayer));
        // Start the timer as soon as this content is built
        startTimer();
    }

    // Procedure to create the session for wrong answers
    private void buildWrongAnswersMode(Group contentLayer) throws IOException {
        // Load the wrongAnswers into an ArrayList
        ArrayList<Flashcard> wrongAnswers = sessionC.loadWrongAnswers();
        // If empty, tell user to start a different session
        if(wrongAnswers.isEmpty()){
            Text information = new Text("No wrong answers yet \n Study a deck prior to using this mode");
            information.getStyleClass().add("infoText");
            information.setTextAlignment(TextAlignment.CENTER);
            information.setLayoutX(310);
            information.setLayoutY(500);
            nav.switchContent(contentLayer, information, getBackView(contentLayer));
            return;
        }
        // Start session
        sessionC.startSession(wrongAnswers, "wrongAnswers", "Wrong Answers");
        // Build content
        buildSessionContent(contentLayer);
    }

    // Function returning the flashcard display
    private StackPane buildCard() {
        cardRect = new Rectangle(1200, 450);
        cardRect.getStyleClass().add("fcRect");

        cardLabel = new Text("Question");
        cardLabel.getStyleClass().add("cardLabel");
        cardLabel.setTranslateY(-170);

        cardBody = new Text(sessionC.getCurrentCard().getQuestion());
        cardBody.getStyleClass().add("cardBody");
        cardBody.setWrappingWidth(1100);

        Text flipHint = new Text("Click card to flip");
        flipHint.getStyleClass().add("flipH");
        flipHint.setTranslateY(185);

        StackPane card = new  StackPane(cardRect, cardLabel, cardBody, flipHint);
        card.getStyleClass().add("flashcard");
        // When clicked, it will flip
        card.setOnMouseClicked(_-> flipFlashcard(card));
        return card;
    }
    // Function to get tick and cross buttons
    private StackPane buildFlashcardButton(String symbol, String colour){
        Rectangle buttonRect = new Rectangle(200, 130);
        buttonRect.getStyleClass().add("fcButtonRect");
        buttonRect.setStyle("-fx-fill: " + colour + ";");
        Text label = new Text(symbol);
        label.getStyleClass().add("fcButtonText");
        StackPane flashcardButton = new StackPane(buttonRect, label);
        flashcardButton.getStyleClass().add("fcButton");
        return flashcardButton;
    }
    // Subroutine to handle the flipping of the card
    private void flipFlashcard(StackPane flashcard){
        // Set flag to true, so tick and cross inputs can be handled
        hasFlipped = true;
        // Set the axis of rotation to be y-axis
        flashcard.setRotationAxis(Rotate.Y_AXIS);
        // Add a perspective camera to add 3d effect
        flashcard.getScene().setCamera(new PerspectiveCamera());
        // Create first half of flip, rotating card half way
        Timeline half1 = new Timeline(new KeyFrame(Duration.millis(150),
                // 150ms, 90 degrees, start slowly
                new KeyValue(flashcard.rotateProperty(), 90, Interpolator.EASE_IN)));
        half1.setOnFinished(_->{
            // toggle whether answer side is showing
            sessionC.setShowingAnswer(!sessionC.isShowingAnswer());
            // if answer should now be shown
            if(sessionC.isShowingAnswer()){
                // Change flashcard content to answer
                cardBody.setText(sessionC.getCurrentCard().getAnswer());
                cardLabel.setText("Answer");
                cardRect.getStyleClass().remove("fcRect");
                cardRect.getStyleClass().add("fcBRect");
            }else{
                // Flip back question content
                cardBody.setText(sessionC.getCurrentCard().getQuestion());
                cardLabel.setText("Question");
                cardRect.getStyleClass().remove("fcBRect");
                cardRect.getStyleClass().add("fcRect");
            }
            // rotate card back to -90, so text change is not visible mid-flip
            flashcard.setRotate(-90);
            // Create a second half animation -90 to 0
           Timeline half2 = new Timeline(new KeyFrame(Duration.millis(150),
                   new KeyValue(flashcard.rotateProperty(), 0, Interpolator.EASE_OUT)));
           half2.play();
        });
        half1.play();
    }
    // Subroutine to handle the answer when a button is clicked
    private void handleAnswer(boolean correct) throws IOException {
        // if not flipped, then display error
        if(!hasFlipped){
            // Audial feedback
            playSFX("incorrect");
            CustomAlert.error(correctText.getScene().getWindow(), "Check the answer first");
            return;
        }
        // If correct, play correct.mp3, else incorrect.mp3
        if(correct) playSFX("correct");
        if(!correct) playSFX("incorrect");
        hasSessionStarted = true;
        // Stop TTS when above to switch
        tts.stop();
        boolean sessionComplete = sessionC.recordAnswer(correct);
        // Update counters
        correctText.setText("✓      " + sessionC.getCorrectCount());
        incorrectText.setText("✗      " + sessionC.getIncorrectCount());
        // If no remaining cards, stop timer switch to EoRScreen, play sessionComplete.mp3
        if(sessionComplete){
            stopTimer();
            changeToSummary();
            return;
        }
        // Change flashcard content to next car
        cardBody.setText(sessionC.getCurrentCard().getQuestion());
        cardLabel.setText("Question");
        // Change style class and update text
        cardRect.getStyleClass().remove("fcBRect");
        if(!cardRect.getStyleClass().contains("fcRect")) cardRect.getStyleClass().add("fcRect");
        progressText.setText(sessionC.getProgressText());
        hasFlipped = false;
    }
    // Update timer
    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), _ -> {
            sessionC.tick();
            timerLabel.setText(sessionC.getFormattedTime());
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }
    // Procedure which will get updated UserStats object and switch the scene to EORScreen
    private void changeToSummary() throws IOException {
        int xpGained = new XPController().calculateXP(sessionC.getSession());
        UserStats updated = stats.updateStats(ac.getEmail(), sessionC.getSession(),
                xpGained, isRetrySession);
        ScreenManager.switchScene(new EndOfRevisionScreen(ac, sc, sessionC, fc, stats, updated).create(),
                ac.getEmail());
        playSFX("sessionComplete");
    }
    // Stop timer
    private void stopTimer(){
        if(timer != null){ timer.stop(); }
    }

    // if user wants to retry, session is continued
    public Scene continueSession(){
        Scene scene = create();
        isRetrySession = true;
        // Find content layer and start a session
        Group contentLayer = (Group) ((Pane) scene.getRoot()).getChildren().get(2);
        buildSessionContent(contentLayer);
        return scene;
    }

    // Custom back button
    private ImageView backButton(Group contentLayer){
        ImageView back = ui.createBRImageView("back");
        back.setCursor(Cursor.HAND);
        back.setOnMouseClicked(_->{
            // Confirmation
            boolean endSession = CustomAlert.confirm(back.getScene().getWindow(),
                    "Confirmation", "Are you sure you want to end the session?");
            // If session hasn't started, and user wants to end then go back to study modes
            if(!hasSessionStarted && endSession) {
                tts.stop();
                nav.goBack(contentLayer);
            }else{
                // if user wants to end and session has started
                if (endSession) {
                    // stop timer
                    stopTimer();
                    try {
                        changeToSummary();
                        tts.stop();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        return back;
    }

    // procedure which will play the sound fx when requested
    private void playSFX(String sound) {
        try{
            // Create a temp file on the system to store the sound
            // JavaFX media requires a URI
            InputStream is = getClass().getResourceAsStream("/sounds/" + sound + ".mp3");
            File tempSoundFile = File.createTempFile("sound", ".mp3");
            // Ensure it is deleted on exit
            tempSoundFile.deleteOnExit();
            // Copy sound data from sound file to file
            Files.copy(Objects.requireNonNull(is), tempSoundFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            // Play sound via MediaPlayer
            Media testSound = new Media(tempSoundFile.toURI().toString());
            MediaPlayer player = new MediaPlayer(testSound);
            System.out.println(sc.getSFXVolume());
            player.setVolume(sc.getSFXVolume());
            player.play();
        } catch (IOException e){
            System.out.println("play fx error: " + e.getMessage());
        }
    }
}
