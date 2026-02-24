package Screens;

import Components.CustomAlert;
import Controllers.AuthenticationController;
import Controllers.FlashcardController;
import Controllers.SettingsController;
import Models.Flashcard;
import RecordClasses.RectangleButtonBundle;
import RecordClasses.TextAreaBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Optional;

public class FlashcardManagementScreen extends SystemScreens{
    private final AuthenticationController ac;
    private final SettingsController sc;
    private final FlashcardController fc;
    private Scene scene;
    private final ObservableList<Flashcard> cardList = FXCollections.observableArrayList();
    private TextAreaBundle questionArea;
    private TextAreaBundle answerArea;

    public FlashcardManagementScreen(AuthenticationController ac, SettingsController sC) {
        this.ac = ac;
        this.sc = sC;
        this.fc = new FlashcardController(ac.getCurrentUser().getEmail());
    }
    public Scene create(){
        Pane root = new Pane();
        root.getStyleClass().add("screens");
        scene = new Scene(root, 1920, 1057);
        Group background = createBackground("Flashcard Control");
        getTitle().setLayoutX(200);
        ImageView back = getSwitchScreenBackView(ac);

        Group contentLayer = new Group();

        RectangleButtonBundle create = ui.buildRectangularButtonBundle(rectW, rectH, "Create", "buttonText", 293, 315);
        ImageView createIcon = createIcon(293, 310, "/images/FlashcardManagementScreen/create.png");
        create.button().setOnMouseClicked(_->buildCreationContent(contentLayer));
        RectangleButtonBundle modify = ui.buildRectangularButtonBundle(rectW, rectH, "Modify", "buttonText", 293, 485);
        ImageView modifyIcon = createIcon(293, 480, "/images/FlashcardManagementScreen/modify.png");

        RectangleButtonBundle organise = ui.buildRectangularButtonBundle(rectW, rectH, "Organise", "buttonText", 293, 656);
        ImageView organiseIcon = createIcon(293, 651, "/images/FlashcardManagementScreen/sort.png");

        Group buttons = new Group(create.button(), modify.button(), organise.button(), createIcon, modifyIcon, organiseIcon);
        contentLayer.getChildren().add(buttons);
        root.getChildren().addAll(background, back, contentLayer);
        return scene;
    }

    private void buildCreationContent(Group contentLayer) {
        VBox inputBox = new VBox(10);
        inputBox.getStyleClass().add("flashcardContent");
        questionArea = ui.createTextArea("Front of card...", 300, 10);
        answerArea = ui.createTextArea("Back of card...", 600, 1);

        TableView<Flashcard> cardTable = buildTable();
        cardTable.getStyleClass().add("cardTable");
        VBox.setVgrow(cardTable, Priority.ALWAYS);
        RectangleButtonBundle addFC = ui.buildRectangularButtonBundle(600, rectH, "Add", "buttonText", 0, 0);

        inputBox.getChildren().addAll(
                questionArea.textArea(), questionArea.characterCounter(),
                answerArea.textArea(),  answerArea.characterCounter(),
                addFC.button()
        );
        RectangleButtonBundle save = ui.buildRectangularButtonBundle(1000, rectH, "Save", "buttonText", 293, 755);
        save.button().setOnMouseClicked(_->{
            if(!cardList.isEmpty() && cardList.size() <= 50) {
                String deckNameReq = "Enter an appropriate deck name, suitable to be a file name: ";
                Optional<String> deckName = CustomAlert.input(scene.getWindow(), "Save Flashcards", deckNameReq, "Name the deck");
                fc.setFlashcardDeckName(deckName.get());
                try {
                    boolean saved = fc.saveFlashcardDeck(cardList);
                    if(saved) {
                        cardList.clear();
                        cardTable.refresh();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        VBox fcContent = new VBox(10, cardTable, save.button());

        HBox display = new HBox(20, inputBox, fcContent);
        display.setLayoutX(140);
        display.setLayoutY(210);
        display.setPrefWidth(1704);
        display.setPrefHeight(745);
        addFC.button().setOnMouseClicked(_->addFlashcard(questionArea.textArea(), answerArea.textArea()));
        nav.switchContent(contentLayer, display);
    }
    private TableView<Flashcard> buildTable(){
        TableView<Flashcard> table = new TableView<>(cardList);
        TableColumn<Flashcard, String> frontColumn = new TableColumn<>("Front");
        frontColumn.setCellValueFactory(new PropertyValueFactory<>("front"));
        TableColumn<Flashcard, String> backColumn = new TableColumn<>("Back");
        backColumn.setCellValueFactory(new PropertyValueFactory<>("back"));

        TableColumn<Flashcard, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setMaxWidth(250);
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            final RectangleButtonBundle delete = ui.buildRectangularButtonBundle(80, 25, "Delete", "tableText", 0, 0);
            final RectangleButtonBundle edit = ui.buildRectangularButtonBundle(80, 25, "Edit", "tableText", 0, 0);
            final VBox actions = new VBox(5, edit.button(), delete.button());
            {
                // Delete - removed the card at this row from the list
                delete.button().setOnMouseClicked(_->{
                    Flashcard card = getTableView().getItems().get(getIndex());
                    if(CustomAlert.confirm(scene.getWindow(), "Deletion?", "Are you sure you want to delete?")){
                        cardList.remove(card);
                    }
                });
                // Edit - changes current text field and removes from list so it can be resaved
                edit.button().setOnMouseClicked(_->{
                    Flashcard card = getTableView().getItems().get(getIndex());
                    if(CustomAlert.confirm(scene.getWindow(), "Deletion?", "Are you sure you want to edit?")){
                        questionArea.textArea().setText(card.getQuestion());
                        answerArea.textArea().setText(card.getAnswer());
                        cardList.remove(card);
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                // Only show options on the rows which have data
                setGraphic(empty ? null : actions);
            }
        });
        table.setStyle("-fx-wrap-text: true");
        table.getColumns().addAll(frontColumn, backColumn, actionsColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No cards yet"));
        return table;
    }
    private void addFlashcard(TextArea q, TextArea a){
        String qReq = "Questions can be 10-300 characters";
        String aReq = "Answers can be 1-300 characters";

        String question = q.getText().trim();
        String answer = a.getText().trim();
        boolean validQ = false;
        boolean validA = false;
        if(q.getLength() >= 10 && q.getLength() <= 300){
            validQ = true;
        }
        if(a.getLength() >= 1 && a.getLength() <= 550){
            validA = true;
        }
        if(validQ && validA){
            cardList.add(new Flashcard(question, answer));
            q.clear();
            a.clear();
        }else{
            CustomAlert.error(scene.getWindow(), qReq + "\n" + aReq);
        }

    }

    private void buildModifyContent(Group contentLayer){
        VBox decks = new VBox(10);

        TableView<String> decksTable = new TableView<>();
        decksTable.getStyleClass().add("cardTable");

        TableColumn<String, String> deckNameColumn = new TableColumn<>("Deck Names");
        deckNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));

        TableColumn<String, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(column -> new TableCell<>(){
            final RectangleButtonBundle rename = ui.buildRectangularButtonBundle(80, 25, "Rename", "tableText", 0, 0);
            final RectangleButtonBundle add = ui.buildRectangularButtonBundle(80, 25, "Add", "tableText", 0, 0);
            final RectangleButtonBundle delete = ui.buildRectangularButtonBundle(80, 25, "Delete", "tableText", 0, 0);
            final VBox buttons = new VBox(5, rename.button(), add.button(), delete.button());

        });
    }
}
