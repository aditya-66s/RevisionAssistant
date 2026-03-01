package Screens;

import Components.CustomAlert;
import Controllers.AuthenticationController;
import Controllers.FlashcardController;
import Controllers.SessionController;
import Controllers.SettingsController;
import Models.Flashcard;
import RecordClasses.RectangleButtonBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Window;

import java.io.IOException;
import java.util.ArrayList;


public class StudyModesScreen extends SystemScreens{
    // Controllers
    private final AuthenticationController ac;
    private final SettingsController sc;
    private final FlashcardController fc;
    private final SessionController sessionC;

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

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        // Get preview and search icon
        HBox display = getPreviewDecksAndCards(fc, decksTable, owner, startSession.button());
        nav.switchContent(contentLayer, display, getBackView(contentLayer));
    }

    private void buildSessionContent(Group contentLayer){
        Label timerLabel = new Label("00:00:00");
        timerLabel.getStyleClass().add("timerLabel");
        timerLabel.setLayoutX(1550); timerLabel.setLayoutY(210);

        Text progressText = new Text(sessionC.getProgressText());
        progressText.getStyleClass().add("progressText");
        progressText.setLayoutX(830); progressText.setLayoutY(210);

        StackPane card = buildCard();
    }

    private StackPane buildCard() {
        Rectangle cardRect = new Rectangle(1200, 450);
        cardRect.getStyleClass().add("flashcard");

        return new StackPane(cardRect);
    }
}
