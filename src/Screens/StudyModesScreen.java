package Screens;

import Components.CustomAlert;
import Controllers.AuthenticationController;
import Controllers.FlashcardController;
import Controllers.SessionController;
import Controllers.SettingsController;
import Models.Flashcard;
import RecordClasses.RectangleButtonBundle;
import javafx.animation.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Window;
import javafx.util.Duration;

import javax.swing.plaf.DimensionUIResource;
import java.io.IOException;
import java.util.ArrayList;


public class StudyModesScreen extends SystemScreens{
    // Controllers
    private final AuthenticationController ac;
    private final SettingsController sc;
    private final FlashcardController fc;
    private final SessionController sessionC;

    private ArrayList<Flashcard> lastFlashcards;
    private String lastDeckName;
    private String lastStudyMode;

    private Text correctText;
    private Text incorrectText;
    private Label timerLabel;
    private Text progressText;
    private Text cardLabel;
    private Text cardBody;
    private Rectangle cardRect;
    private boolean hasFlipped = false;

    private Timeline timer;

    public StudyModesScreen(AuthenticationController ac, SettingsController sC, FlashcardController fc) {
        this.ac = ac;
        this.sc = sC;
        this.fc = fc;
        this.sessionC = new SessionController(ac.getCurrentUser().getEmail());
    }
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

        RectangleButtonBundle startSession = ui.buildRectangularButtonBundle(500, 100, "Start Session", "smallButtonText", 760, 0);
        startSession.button().setOnMouseClicked(_->{
            String deckName = decksTable.getSelectionModel().getSelectedItem();
            if(deckName == null) CustomAlert.error(owner, "Select a deck before starting a session");
            try{
                ArrayList<Flashcard> cards = fc.loadFlashcardDeck(deckName);
                if(mode.equals("Shuffle")) sessionC.shuffle(cards);
                lastFlashcards = new ArrayList<>(cards);
                lastDeckName = deckName;
                lastStudyMode = mode;
                sessionC.startSession(cards, deckName, mode);
                buildSessionContent(contentLayer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        // Get preview and search icon
        HBox display = getPreviewDecksAndCards(fc, decksTable, owner, startSession.button());
        nav.switchContent(contentLayer, display, getBackView(contentLayer));
    }

    private void buildSessionContent(Group contentLayer){
        timerLabel = new Label("00:00:00");
        timerLabel.getStyleClass().add("timerLabel");
        timerLabel.setLayoutX(700); timerLabel.setLayoutY(940);

        progressText = new Text(sessionC.getProgressText());
        progressText.getStyleClass().add("infoText");
        progressText.setLayoutX(1400); progressText.setLayoutY(225);

        StackPane flashcard = buildCard();
        flashcard.setLayoutX(360); flashcard.setLayoutY(290);

        StackPane tick = buildFlashcardButton("✓","lightGreen");
        tick.setOnMouseClicked(_-> handleAnswer(true));
        tick.setLayoutX(1035); tick.setLayoutY(760);
        StackPane cross = buildFlashcardButton("✗", "red");
        cross.setOnMouseClicked(_-> handleAnswer(false));
        cross.setLayoutX(685); cross.setLayoutY(760);

        correctText = new Text("✓      0");
        correctText.getStyleClass().add("correctC");
        correctText.setLayoutX(1520); correctText.setLayoutY(830);

        incorrectText = new Text("✗      0");
        incorrectText.getStyleClass().add("incorrectC");
        incorrectText.setLayoutX(200); incorrectText.setLayoutY(830);

        Text mode = new Text(sessionC.getStudyMode() + "  ·  "
                + sessionC.getSelectedDeck());
        mode.getStyleClass().add("infoText");
        mode.setLayoutX(125); mode.setLayoutY(230);

        nav.switchContent(contentLayer, timerLabel, progressText, flashcard, tick, cross, correctText, incorrectText, mode);
        startTimer();
    }

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
        card.setOnMouseClicked(_-> flipFlashcard(card));
        return card;
    }
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

    private void flipFlashcard(StackPane flashcard){
        hasFlipped = true;
        // Set the axis of rotation to be y-axis
        flashcard.setRotationAxis(Rotate.Y_AXIS);
        // Add a perspective camera to add 3d effect
        flashcard.getScene().setCamera(new PerspectiveCamera());
        Timeline half1 = new Timeline(new KeyFrame(Duration.millis(150),
                new KeyValue(flashcard.rotateProperty(), 90, Interpolator.EASE_IN)));
        half1.setOnFinished(_->{
            sessionC.setShowingAnswer(!sessionC.isShowingAnswer());
            if(sessionC.isShowingAnswer()){
                cardBody.setText(sessionC.getCurrentCard().getAnswer());
                cardLabel.setText("Answer");
                cardRect.getStyleClass().remove("fcRect");
                cardRect.getStyleClass().add("fcBRect");
            }else{
                cardBody.setText(sessionC.getCurrentCard().getQuestion());
                cardLabel.setText("Question");
                cardRect.getStyleClass().remove("fcBRect");
                cardRect.getStyleClass().add("fcRect");
            }
            flashcard.setRotate(-90);
           Timeline half2 = new Timeline(new KeyFrame(Duration.millis(150),
                   new KeyValue(flashcard.rotateProperty(), 0, Interpolator.EASE_OUT)));
           half2.play();
        });
        half1.play();
    }

    private void handleAnswer(boolean correct){
        if(!hasFlipped){
            CustomAlert.error(correctText.getScene().getWindow(), "Check the answer first");
            return;
        }
        boolean sessionComplete = sessionC.recordAnswer(correct);
        correctText.setText("✓      " + sessionC.getCorrectCount());
        incorrectText.setText("✗      " + sessionC.getIncorrectCount());

        if(sessionComplete){
            stopTimer();
            return;
        }
        cardBody.setText(sessionC.getCurrentCard().getQuestion());
        cardLabel.setText("Question");
        cardRect.getStyleClass().remove("fcBRect");
        if(!cardRect.getStyleClass().contains("fcRect")) cardRect.getStyleClass().add("fcRect");
        progressText.setText(sessionC.getProgressText());
        hasFlipped = false;
    }

    private void startTimer(){
        timer = new Timeline(new KeyFrame(Duration.seconds(1), _->{
            sessionC.tick();
            timerLabel.setText(sessionC.getFormattedTime());
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }
    private void stopTimer(){
        if(timer != null){ timer.stop(); }
    }
}
