package Screens;

import Components.ContentNavigator;
import Components.CustomAlert;
import Components.ScreenManager;
import Components.UIComponents;
import Controllers.AuthenticationController;
import Controllers.FlashcardController;
import Models.Flashcard;
import RecordClasses.TextBundle;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Window;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static javafx.scene.text.TextAlignment.CENTER;

public abstract class SystemScreens {
    // Constants
    // Scene
    protected static final int sceneW = 1920;
    protected static final int sceneH = 1057;
    // Constant rectangular button dimensions set
    protected static final int rectW = 1300;
    protected static final int rectH = 133;
    // Background
    protected static final int bgX = 108;
    protected static final int bgY = 194;
    protected static final int bgW = 1704;
    protected static final int bgH = 745;
    // Title
    protected static final int titleY = 160;
    // 4 button layout
    protected static final int fourX = 310;
    protected static final int fourY1 = 250;
    protected static final int fourY2 = 420;
    protected static final int fourY3 = 591;
    protected static final int fourY4 = 762;
    // 3 button layout
    protected static final int threeX = 293;
    protected static final int threeY1 = 315;
    protected static final int threeY2 = 485;
    protected static final int threeY3 = 656;
    // Icon offset
    protected static final int iconOffset = -5;
    // Spacing
    protected static final int vSpacing = 10;
    protected static final int hSpacing = 20;
    // Other
    protected static final int zero = 0;
    private static final int cardPreviewH = 600;
    private static final int iconY = 4;
    protected static final int iconW = 95;
    protected static final int iconH = 90;
    private static final int searchDW = 600;
    private static final int searchFW = 1000;
    private static final int searchH = 98;
    private static final int fieldX = 95;
    private static final int fieldY = 10;
    private static final int leftW = 600;
    private static final int rightW = 1000;
    private static final int hX = 140;
    private static final int hY = 210;

    public final UIComponents ui = new UIComponents();
    public final ContentNavigator nav = new  ContentNavigator();
    private Text title;

    public Group createBackground(String titleText){
        title = new Text(titleText);
        title.setTextAlignment(CENTER);
        title.getStyleClass().add("postAuthTitle");
        title.setLayoutY(titleY);
        Rectangle background = new Rectangle(bgX, bgY, bgW,bgH);
        background.getStyleClass().add("backgroundRect");
        return new Group(title, background, ui.createLogoView());
    }

    public ImageView createIcon(int iconX, int iconY, String iconURL){
        ImageView iconView = new ImageView(Objects.requireNonNull(getClass().getResource(iconURL)).toExternalForm());
        iconView.getStyleClass().add("icons");
        iconView.setLayoutX(iconX);
        iconView.setLayoutY(iconY);
        return iconView;
    }

    public ImageView getBackView(Group contentLayer) {
        ImageView back = ui.createBRImageView("back");
        back.setOnMouseClicked(_ -> nav.goBack(contentLayer));
        return back;
    }

    public ImageView getSwitchScreenBackView(AuthenticationController ac) {
        ImageView back = ui.createBRImageView("back");
        back.setOnMouseClicked(_ -> {
            try {
                ScreenManager.goBack(ac.getEmail());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return back;
    }
    public void setTitle(String titleText) {
        this.title = new Text(titleText);
    }

    public Text getTitle(){
        return title;
    }

    // Method to build the card preview table to be used in multiple screens
    protected TableView<Flashcard> buildCardPreviewTable(int height){
        // Create a new tableview bound to observable list
        TableView<Flashcard> table = new TableView<>();
        table.getStyleClass().add("cardTable");
        table.setMinHeight(height);
        table.setPrefHeight(height);
        // Create columns and link to the specific property of Flashcard.java
        // to populate the cell
        TableColumn<Flashcard, String> frontColumn = new TableColumn<>("Front");
        frontColumn.setCellValueFactory(new PropertyValueFactory<>("question"));
        TableColumn<Flashcard, String> backColumn = new TableColumn<>("Back");
        backColumn.setCellValueFactory(new PropertyValueFactory<>("answer"));
        // Add all columns to table
        table.getColumns().addAll(List.of(frontColumn, backColumn));
        // Columns are resized to fill available width, last column will shrink or expand
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        // Set the text when there are no cards
        Label placeholderText = new Label("No cards yet");
        placeholderText.setStyle("-fx-text-fill: red;");
        table.setPlaceholder(placeholderText);
        return table;
    }

    // Build HBox which displays the decks table, preview table and search fields
    protected HBox getPreviewDecksAndCards(FlashcardController fc, TableView<String> decksTable, Window owner, Node... extras){
        // Card Preview Table
        TableView<Flashcard> cardsPreview = buildCardPreviewTable(cardPreviewH);
        // Search for deck names
        String searchIcon = "/images/FlashcardManagementScreen/search.png";
        TextBundle searchDecks = ui.buildTextFieldInput(zero, zero, searchDW, searchH, fieldX, fieldY, "Search Decks", searchIcon, zero, iconY, iconW, iconH );
        searchDecks.tf().getStyleClass().add("deckSearch");
        searchDecks.group().getChildren().add(searchDecks.icon());
        // Search for specific flashcards within all decks, and display what deck it is in
        TextBundle searchFlashcards = ui.buildTextFieldInput(zero, zero, searchFW, searchH, fieldX, fieldY, "Search Flashcards", searchIcon, zero, iconY, iconW, iconH );
        searchFlashcards.tf().getStyleClass().add("fcSearch");
        searchFlashcards.group().getChildren().add(searchFlashcards.icon());
        // Listeners for searches
        // All initial decks
        ArrayList<String> allDecks = new ArrayList<>(decksTable.getItems());
        searchDecks.tf().textProperty().addListener((_, _, newValue) -> {
            TextField searchFlashcardTF = searchFlashcards.tf();
            cardsPreview.getItems().clear();
            if(newValue.isEmpty()){
                searchFlashcardTF.setDisable(false);
                searchFlashcards.group().setOnMouseClicked(null);
                // If no search just keep results to all decks
                decksTable.getItems().setAll(allDecks);
                return;
            }else {
                // Can't have two searches at once
                searchFlashcards.group().setOnMouseClicked(_-> CustomAlert.error(owner,
                        "Clear the deck search to search flashcards"));
                searchFlashcardTF.setDisable(true);
                // Set tableView contents to the following
                decksTable.getItems().setAll(
                        allDecks.stream() // Convert the array list into a stream for processing
                                // Filter so decks which match the search condition are shown
                                .filter(deck -> deck.toLowerCase().contains(newValue.trim().toLowerCase()))
                                // Add decks matching the criteria to a list
                                .collect(Collectors.toList()));
            }
        });
        searchFlashcards.tf().textProperty().addListener((_, _, newValue) -> {
            TextField searchDeckTF = searchDecks.tf();
            cardsPreview.getItems().clear();
            if(newValue.isEmpty()){
                searchDeckTF.setDisable(false);
                searchDecks.group().setOnMouseClicked(null);
                // If no search just keep results to all decks
                decksTable.getItems().setAll(allDecks);
                return;
            }else{
                searchDecks.group().setOnMouseClicked(_-> CustomAlert.error(owner,
                        "Clear the flashcard search to search decks"));
                searchDeckTF.setDisable(true);
                // Create an Array List of the decks which match criteria
                ArrayList<String> decksMatchingCriteria = new ArrayList<>();
                for (String deck : allDecks){
                    try{
                        ArrayList<Flashcard> cards = fc.loadFlashcardDeck(deck);
                        boolean matchFound = cards.stream().anyMatch(
                                card ->
                                        card.getQuestion().toLowerCase().contains(newValue.trim().toLowerCase()) ||
                                                card.getAnswer().toLowerCase().contains(newValue.trim().toLowerCase())
                        );
                        if(matchFound){
                            decksMatchingCriteria.add(deck);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }decksTable.getItems().setAll(decksMatchingCriteria);
            }
        });
        // Listener to change the card preview based on selection
        decksTable.getSelectionModel().selectedItemProperty().addListener(((_, _, newValue) -> {
            if(newValue == null) return;
            try{
                cardsPreview.getItems().setAll(fc.loadFlashcardDeck(newValue));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));

        VBox leftSide = new VBox(vSpacing, searchDecks.group(), decksTable);
        leftSide.getChildren().addAll(extras);
        leftSide.setPrefWidth(leftW);
        leftSide.setMinWidth(leftW);

        VBox rightSide = new VBox(vSpacing, searchFlashcards.group(), cardsPreview);
        rightSide.setPrefWidth(rightW);

        HBox display = new HBox(hSpacing, leftSide, rightSide);
        display.setLayoutX(hX);
        display.setLayoutY(hY);
        display.getStyleClass().add("display");
        return display;
    }
}
