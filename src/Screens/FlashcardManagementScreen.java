package Screens;

import Components.CustomAlert;
import Controllers.AuthenticationController;
import Controllers.SettingsController;
import Models.Flashcard;
import RecordClasses.RectangleButtonBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FlashcardManagementScreen extends SystemScreens{
    private final AuthenticationController ac;
    private final SettingsController sc;
    private ObservableList<Flashcard> cardList = FXCollections.observableArrayList();

    public FlashcardManagementScreen(AuthenticationController ac, SettingsController sC) {
        this.ac = ac;
        this.sc = sC;
    }
    public Scene create(){
        Pane root = new Pane();
        root.getStyleClass().add("screens");
        Scene scene = new Scene(root, 1920, 1057);
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

    public void buildCreationContent(Group contentLayer){
        VBox content = new VBox(10);
        content.setPadding(new Insets(30));
        content.setMaxWidth(1704);
        content.setMaxHeight(745);

        TextArea frontField = new TextArea();
        frontField.setPromptText("Front of card...");
        frontField.setPrefRowCount(3);

        TextArea backField = new TextArea();
        backField.setPromptText("Back of card...");
        backField.setPrefRowCount(3);

        TableView<Flashcard> cardTable = buildTable();
        cardTable.setStyle("-fx-font-size: 14px; -fx-font-family: 'Arial';");
        RectangleButtonBundle save = ui.buildRectangularButtonBundle(rectW, rectH, "Save", "buttonText", 293, 315);
        save.button().setOnMouseClicked(_->{
            String question = frontField.getText().trim();
            String answer = backField.getText().trim();
            if(question.isEmpty() || answer.isEmpty()){
                CustomAlert.error(contentLayer.getScene().getWindow(), "Please fill all the fields");
                return;
            }
            cardList.add(new Flashcard(question, answer));
            frontField.clear();
            backField.clear();
        });
        VBox.setVgrow(cardTable, Priority.ALWAYS);
        content.getChildren().addAll(
                new Label("Front"), frontField,
                new Label("Back"), backField,
                save.button(), cardTable
        );

        content.setLayoutX(108);
        content.setLayoutY(170);

        nav.switchContent(contentLayer, content);
    }
    private TableView<Flashcard> buildTable() {
        TableView<Flashcard> table = new TableView<>(cardList);

        TableColumn<Flashcard, String> frontCol = new TableColumn<>("Front");
        frontCol.setCellValueFactory(new PropertyValueFactory<>("front"));

        TableColumn<Flashcard, String> backCol = new TableColumn<>("Back");
        backCol.setCellValueFactory(new PropertyValueFactory<>("back"));

        table.getColumns().addAll(frontCol, backCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }
}
