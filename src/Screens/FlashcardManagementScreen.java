package Screens;

import Components.CustomAlert;
import Controllers.AuthenticationController;
import Controllers.SettingsController;
import Models.Flashcard;
import RecordClasses.RectangleButtonBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FlashcardManagementScreen extends SystemScreens{
    private final AuthenticationController ac;
    private final SettingsController sc;
    private Scene scene;
    private ObservableList<Flashcard> cardList = FXCollections.observableArrayList();

    public FlashcardManagementScreen(AuthenticationController ac, SettingsController sC) {
        this.ac = ac;
        this.sc = sC;
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

        TextArea questionArea = new TextArea();
        questionArea.setPromptText("Front of card...");
        questionArea.getStyleClass().add("textArea");
        TextArea answerArea = new TextArea();
        answerArea.setPromptText("Back of card...");
        answerArea.getStyleClass().add("textArea");

        TableView<Flashcard> cardTable = buildTable();
        cardTable.getStyleClass().add("cardTable");
        VBox.setVgrow(cardTable, Priority.ALWAYS);
        RectangleButtonBundle saveFc = ui.buildRectangularButtonBundle(600, rectH, "Save", "buttonText", 0, 0);

        inputBox.getChildren().addAll(
                questionArea, answerArea, saveFc.button()
        );

        HBox display = new HBox(20, inputBox, cardTable);
        display.setLayoutX(140);
        display.setLayoutY(210);
        display.setPrefWidth(1704);
        display.setPrefHeight(745);

//        RectangleButtonBundle save = ui.buildRectangularButtonBundle(600, rectH, "Save", "buttonText", 293, 755);
        saveFc.button().setOnMouseClicked(_->saveFlashcard(questionArea,answerArea));
        nav.switchContent(contentLayer, display);
    }

    private TableView<Flashcard> buildTable(){
        TableView<Flashcard> table = new TableView<>(cardList);
        TableColumn<Flashcard, String> frontCol = new TableColumn<>("Front");
        frontCol.setCellValueFactory(new PropertyValueFactory<>("front"));

        TableColumn<Flashcard, String> backCol = new TableColumn<>("Back");
        backCol.setCellValueFactory(new PropertyValueFactory<>("back"));
        table.setStyle("-fx-wrap-text: true");
        table.getColumns().addAll(frontCol, backCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }
    private void saveFlashcard(TextArea q, TextArea a){
        String qReq = "Questions can be 10-300 characters";
        String aReq = "Answers can be 1-300 characters";

        String question = q.getText().trim();
        String answer = a.getText().trim();
        boolean validQ = false;
        boolean validA = false;
        if(q.getLength() >= 10 && q.getLength() <= 300){
            validQ = true;
        }
        if(a.getLength() >= 1 && a.getLength() <= 300){
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
}
