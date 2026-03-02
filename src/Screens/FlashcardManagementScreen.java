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
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/* Class to be used to create the interface of the FlashcardManagementScreen, containing logic on creating,
* editing, and organising flashcard and flashcard decks.*/
public class FlashcardManagementScreen extends SystemScreens{
    // Controllers to be used for core system functionality
    private final AuthenticationController ac;
    private final SettingsController sc;
    private final FlashcardController fc;
    // Global Scene for creating alert windows
    private Scene scene;
    // Observable lists storing all Flashcard objects being created or being edited
    private final ObservableList<Flashcard> cardList = FXCollections.observableArrayList();
    // Decks table to be updated in save
    private final TableView<String> decksTable = new TableView<>();
    // ArrayList to be updated when saved.
    private ArrayList<String> allDecks;
    // Record class instance being used for flashcard front and back inputs
    private TextAreaBundle questionArea;
    private TextAreaBundle answerArea;

    /* Constructor class for FMScreen, controllers as parameters to maintain
    consistency across screens. */
    public FlashcardManagementScreen(AuthenticationController ac, SettingsController sC, FlashcardController fc) {
        this.ac = ac;
        this.sc = sC;
        this.fc = fc;
    }
    // Method for creating the initial interface the user see when the screen is switched to the FMScreen
    public Scene create(){
        Pane root = new Pane();
        root.getStyleClass().add("screens");
        scene = new Scene(root, 1920, 1057);
        // Method inherited from parent class to add background
        Group background = createBackground("Flashcard Control");
        getTitle().setLayoutX(200);
        ImageView back = getSwitchScreenBackView(ac);

        // Group storing content within a page of the screen
        Group contentLayer = new Group();

        // Record class instances to bundle multiple logic components together
        RectangleButtonBundle create = ui.buildRectangularButtonBundle(rectW, rectH, "Create", "buttonText", 293, 315);
        ImageView createIcon = createIcon(293, 310, "/images/FlashcardManagementScreen/create.png");
        create.button().setOnMouseClicked(_->buildCreationContent(contentLayer, false));

        RectangleButtonBundle modify = ui.buildRectangularButtonBundle(rectW, rectH, "Modify", "buttonText", 293, 485);
        ImageView modifyIcon = createIcon(293, 480, "/images/FlashcardManagementScreen/modify.png");
        modify.button().setOnMouseClicked(_-> {
            try {
                buildModifyContent(contentLayer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        RectangleButtonBundle organise = ui.buildRectangularButtonBundle(rectW, rectH, "Organise", "buttonText", 293, 656);
        ImageView organiseIcon = createIcon(293, 651, "/images/FlashcardManagementScreen/sort.png");
        organise.button().setOnMouseClicked(_-> {
            try {
                buildOrganisationContent(contentLayer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Group buttons = new Group(create.button(), modify.button(), organise.button(), createIcon, modifyIcon, organiseIcon);
        contentLayer.getChildren().add(buttons);
        root.getChildren().addAll(background, back, contentLayer);
        return scene;
    }

    // Procedure creating page for the Creation of Flashcards
    private void buildCreationContent(Group contentLayer, boolean alreadyExists) {
        // Container for input fields and add button
        VBox inputBox = new VBox(10);
        inputBox.getStyleClass().add("flashcardContent");
        questionArea = ui.createTextArea("Front of card...", 300, 10);
        answerArea = ui.createTextArea("Back of card...", 600, 1);

        // Table displaying all flashcards in the deck
        TableView<Flashcard> cardTable = buildFlashcardTableWActions();
        cardTable.getStyleClass().add("cardTable");
        // To prevent table from having flashcard data from previous modifications
        if(!alreadyExists){
            fc.setFlashcardDeckName(null);
            cardList.clear();
            cardTable.refresh();
        }
        // Allow the table to grow vertically in its VBox
        VBox.setVgrow(cardTable, Priority.ALWAYS);
        RectangleButtonBundle addFC = ui.buildRectangularButtonBundle(600, rectH, "Add", "buttonText", 0, 0);

        inputBox.getChildren().addAll(
                questionArea.textArea(), questionArea.characterCounter(),
                answerArea.textArea(),  answerArea.characterCounter(),
                addFC.button()
        );
        // Flag to determine if the user wants to save to a diff file
        final boolean[] diffFile = {false};
        RectangleButtonBundle save = ui.buildRectangularButtonBundle(1000, rectH, "Save", "buttonText", 293, 755);
        save.button().setOnMouseClicked(_-> saving(cardTable, alreadyExists, diffFile, contentLayer));

        // Deck constraints label to indicate to user
        int size = cardTable.getItems().size();
        Label deckConstraint = new Label(size + " / 60");
        // Set initial colour identifier
        if(size == 0 || size > 60){
            deckConstraint.setStyle("-fx-text-fill: red;");
        }else {
            deckConstraint.setStyle("-fx-text-fill: green;");
        }
        deckConstraint.getStyleClass().add("counter");
        // Listener so it can change in real time
        cardTable.getItems().addListener((ListChangeListener<Flashcard>) _ ->{
            int varyingSize = cardTable.getItems().size();
            deckConstraint.setText(varyingSize + " / 60");
            if(varyingSize == 0 || varyingSize > 60){
                deckConstraint.setStyle("-fx-text-fill: red;");
            }else{
                deckConstraint.setStyle("-fx-text-fill: green;");
            }
        });
        // Another VBox containing the cardTable and save button
        VBox fcContent = new VBox(10, cardTable, deckConstraint, save.button());

        // HBox with spacing of 20 to display the two Vboxes
        HBox display = new HBox(20, inputBox, fcContent);
        display.setLayoutX(140);
        display.setLayoutY(210);
        display.getStyleClass().add("display");

        addFC.button().setOnMouseClicked(_->addFlashcard(questionArea.textArea(), answerArea.textArea()));
        nav.switchContent(contentLayer, display, getBackView(contentLayer));
    }
    // Method used to save created flashcards, outputting appropriate messages
    private void saving(TableView<Flashcard> cardTable, boolean alreadyExists, boolean[] diffFile, Group contentLayer){
        // Checks if flashcard deck is a valid size
        if(!cardList.isEmpty() && cardList.size() <= 60) {
            // Does this already exist -> write into same file as before
            // Does this not exist already or user has chosen diffFile -> write into a new file
            if (!alreadyExists || diffFile[0]) {
                // Asks user to input the file name
                String deckNameReq = "Enter an appropriate deck name, suitable to be a file name: ";
                Optional<String> deckName = CustomAlert.input(scene.getWindow(), "Save Flashcards", deckNameReq, "Name the deck");
                if (deckName.isPresent()) {
                    // Set file to name to flashcard deck name
                    fc.setFlashcardDeckName(deckName.get());
                    try {
                        // Attempt to save
                        boolean saved = fc.saveFlashcardDeck(cardList, false);
                        if (saved) {
                            // Output Success message
                            CustomAlert.success(scene.getWindow(),"Flashcard deck has been saved");
                            if(alreadyExists) {
                                allDecks.clear();
                                allDecks.addAll(fc.getFileContents());
                                // Update deck table
                                decksTable.getItems().addAll(fc.getFileContents());
                                nav.goBack(contentLayer);
                            }
                            // Clear table and list
                            cardList.clear();
                            cardTable.refresh();
                        }
                    } catch (IOException e) {
                        // If not possible, display a suitable error message
                        CustomAlert.error(scene.getWindow(), "Could not save changes, try again later");
                        throw new RuntimeException(e);
                    }
                }
            }else{
                // Tell user they are writing to same file
                if(CustomAlert.confirm(scene.getWindow(), "Confirmation", "You are writing to the previous file, if this not ideal, cancel and try again")){
                    try {
                        boolean saved = fc.saveFlashcardDeck(cardList, true);
                        if(saved) {
                            cardList.clear();
                            cardTable.refresh();
                            CustomAlert.success(scene.getWindow(), "Changes have been saved, you will be returned to modification");
                            // Update deck table
                            allDecks.clear();
                            allDecks.addAll(fc.getFileContents());
                            decksTable.getItems().addAll(fc.getFileContents());
                            nav.goBack(contentLayer);
                        }
                    } catch (IOException e) {
                        CustomAlert.error(scene.getWindow(), "Could not save changes, try again later");
                        throw new RuntimeException(e);
                    }
                }else{
                    // Ask for confirmation they want to write into a new file
                    if(CustomAlert.confirm(scene.getWindow(), "Confirmation", "Would you like to write into a different file?")){
                        diffFile[0] = true;
                    }
                }
            }
        }else{
            // Remind user of constraints
            CustomAlert.error(scene.getWindow(), "Minimum of 0 and maximum of 50 cards in a deck");
        }
    }
    // Function which builds the table to display Flashcards
    public TableView<Flashcard> buildFlashcardTableWActions() {
        // Create a new tableview bound to observable list
        TableView<Flashcard> table = new TableView<>(cardList);
        // Create columns and link to the specific property of Flashcard.java
        // to populate the cell
        TableColumn<Flashcard, String> frontColumn = new TableColumn<>("Front");
        frontColumn.setCellValueFactory(new PropertyValueFactory<>("question"));
        TableColumn<Flashcard, String> backColumn = new TableColumn<>("Back");
        backColumn.setCellValueFactory(new PropertyValueFactory<>("answer"));
        TableColumn<Flashcard, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setMaxWidth(250);
        actionsColumn.setCellFactory(_ -> new TableCell<>() {
            final RectangleButtonBundle delete = ui.buildRectangularButtonBundle(80, 25, "Delete", "tableText", 0, 0);
            final RectangleButtonBundle edit = ui.buildRectangularButtonBundle(80, 25, "Edit", "tableText", 0, 0);
            final VBox actions = new VBox(5, edit.button(), delete.button());
            {
                // Delete - removed the card at this row from the list
                delete.button().setOnMouseClicked(_ -> {
                    Flashcard card = getTableView().getItems().get(getIndex());
                    if (CustomAlert.confirm(scene.getWindow(), "Deletion?", "Are you sure you want to delete?")) {
                        cardList.remove(card);
                    }
                });
                // Edit - changes current text field and removes from list so it can be resaved
                edit.button().setOnMouseClicked(_ -> {
                    Flashcard card = getTableView().getItems().get(getIndex());
                    if (CustomAlert.confirm(scene.getWindow(), "Deletion?", "Are you sure you want to edit?")) {
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
        // Add all columns to the table
        table.getColumns().add(actionsColumn);
        // Columns are resized to fill available width, last column will shrink or expand
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        // Set the text when there are no cards
        Label placeholderText = new Label("No cards yet");
        placeholderText.setStyle("-fx-text-fill: red;");
        table.setPlaceholder(placeholderText);
        return table;
    }

    // Procedure to add flashcards to the list
    private void addFlashcard(TextArea q, TextArea a){
        // Question and Answer constraints
        String qReq = "Questions can be 10-300 characters";
        String aReq = "Answers can be 1-600 characters";
        // Remove white space and get the text
        String question = q.getText().trim();
        String answer = a.getText().trim();
        // Check if valid
        boolean validQ = question.length() >= 10 && question.length() <= 300;
        boolean validA = !answer.isEmpty() && answer.length() <= 600;
        // Add to list
        if(validQ && validA){
            cardList.add(new Flashcard(question, answer));
            // Clear the fields for next input
            q.clear();
            a.clear();
        }else{
            // Display error
            CustomAlert.error(scene.getWindow(), qReq + "\n" + aReq);
        }
    }
    // Procedure to create the modification page
    private void buildModifyContent(Group contentLayer) throws IOException {
        // Table to display the deck names
        TableView<String> decksTable = new TableView<>();
        decksTable.getStyleClass().add("cardTable");
        decksTable.setMinHeight(600);
        TableColumn<String, String> deckNameColumn = new TableColumn<>("Deck Names");
        deckNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        TableColumn<String, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(_ -> new TableCell<>(){
            // Buttons in the actions column
            final RectangleButtonBundle rename = ui.buildRectangularButtonBundle(80, 25, "Rename", "tableText", 0, 0);
            final RectangleButtonBundle edit = ui.buildRectangularButtonBundle(80, 25, "Edit", "tableText", 0, 0);
            final RectangleButtonBundle delete = ui.buildRectangularButtonBundle(80, 25, "Delete", "tableText", 0, 0);
            final VBox buttons = new VBox(5, rename.button(), edit.button(), delete.button());
            {
                // Renaming the file logic
                rename.button().setOnMouseClicked(_->{
                    String deckName = getTableView().getItems().get(getIndex());
                    if(CustomAlert.confirm(scene.getWindow(),"Confirmation", "Are you sure you want to rename " + deckName + "?")){
                        Optional<String> newName = CustomAlert.input(scene.getWindow(), "Rename the deck",
                                "Enter a suitable new deck name", "Enter new deck name");
                        if (newName.isPresent()) {
                            fc.renameDeck(deckName, newName.get());
                            decksTable.getItems().set(getIndex(), newName.get());
                            CustomAlert.success(scene.getWindow(), "Successfully renamed deck " + deckName + " to " + newName.get());
                        }
                    }
                });
                // Deleting the flashcard file logic
                delete.button().setOnMouseClicked(_->{
                    String deckName = getTableView().getItems().get(getIndex());
                    if(CustomAlert.confirm(scene.getWindow(), "Confirmation", "Are you sure you want to delete " + deckName + "?")){
                        try {
                            fc.deleteDeck(deckName);
                            String selectedRow = getTableView().getSelectionModel().getSelectedItem();
                            getTableView().getItems().remove(selectedRow);
//                            cardsPreview.refresh();
                            CustomAlert.success(scene.getWindow(), "Successfully deleted deck " + deckName);
                        } catch (IOException e) {
                            CustomAlert.error(scene.getWindow(), "Unsuccessful, try again later");
                            throw new RuntimeException(e);
                        }
                    }
                });
                // Editing the flashcard deck
                edit.button().setOnMouseClicked(_->{
                    String deckName = getTableView().getItems().get(getIndex());
                    if(CustomAlert.confirm(scene.getWindow(), "Confirmation", "Are you sure you want to add to " + deckName + "?")){
                        fc.setFlashcardDeckName(deckName);
                        try {
                            cardList.setAll(fc.loadFlashcardDeck(deckName));
                        } catch (IOException e) {throw new RuntimeException(e);}
                        // Takes you to creation page
                        buildCreationContent(contentLayer, true);
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty){
                super.updateItem(item, empty);
                // Only show buttons if there is data
                setGraphic(empty ? null : buttons);
            }
        });
        decksTable.getColumns().addAll(List.of(deckNameColumn, actionsColumn));
        decksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        decksTable.getItems().addAll(fc.getFileContents());
        allDecks = new ArrayList<>(decksTable.getItems());
        HBox display = getPreviewDecksAndCards(fc, decksTable, scene.getWindow());
        nav.switchContent(contentLayer, display, getBackView(contentLayer));
    }
    // Procedure to create the organisation page
    private void buildOrganisationContent(Group contentLayer) throws IOException {
        TreeView<String> tree = buildTree();
        addDragAndDrop(tree);

        RectangleButtonBundle newFolder = ui.buildRectangularButtonBundle(300, 80, "Add Folder", "smallerButtonText", 0, 0);
        newFolder.button().setOnMouseClicked(_->addFolder(tree));
        RectangleButtonBundle delete = ui.buildRectangularButtonBundle(300, 80, "Delete Folder", "smallerButtonText", 0, 0);
        delete.button().setOnMouseClicked(_-> {
            try {
                deleteFolder(tree);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        HBox options = new HBox(20, newFolder.button(), delete.button());
        VBox treeViewVBox = new VBox(20, options, tree);

        TableView<Flashcard> cardsPreview = buildCardPreviewTable(700);
        cardsPreview.getItems().clear();

        // Listener to change the card preview based on selection
        tree.getSelectionModel().selectedItemProperty().addListener(((_, _, newValue) -> {
            if(newValue == null) return;
            try{
                String item = newValue.getValue();
                if(item.startsWith("\uD83D\uDCC1 ")) {
                    cardsPreview.getItems().clear();
                    Label phText = new Label("This is a folder");
                    phText.setStyle("-fx-text-fill: red;");
                    cardsPreview.setPlaceholder(phText);
                }
                if(item.startsWith("\uD83D\uDCC4 ")){
                    String fileNode = item.replace("\uD83D\uDCC4 ", "");
                    cardsPreview.getItems().setAll(fc.loadFlashcardDeck(fileNode));
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));


        HBox display = new HBox(20, treeViewVBox, cardsPreview);
        display.setLayoutX(125);
        display.setLayoutY(200);
        display.getStyleClass().add("display");
        nav.switchContent(contentLayer, display, getBackView(contentLayer));
    }
    // Function to build the tree which displays the folder for organisation
    private TreeView<String> buildTree(){
        // Create the initial TreeView
        TreeView<String> folderTreeView = new TreeView<>();
        folderTreeView.getStyleClass().add("folderTreeView");
        // Enables possibility of edit
        folderTreeView.setEditable(true);
        folderTreeView.setShowRoot(true);

        // Build existing tree copying directory structure
        TreeItem<String> folderStructure = fc.getUserDirectoryStructure(ac.getCurrentUser().getEmail());
        folderTreeView.setRoot(folderStructure);
        folderTreeView.getRoot().setExpanded(true);
        return folderTreeView;
    }
    // Procedure which will allow the dragging and dropping of files within the tree
    private void addDragAndDrop(TreeView<String> tree){
        // Creating custom cells with drag and drop feature
        tree.setCellFactory(_-> {
            // Create a new cell with item's string value
            TreeCell<String> cell = new TreeCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    // Show text if cell has content, otherwise leave it blank
                    setText(empty || item == null ? null : item);
                }
            };
            // User begins dragging
            cell.setOnDragDetected(e->{
                // Don't allow the moving of empty cells
                if(cell.isEmpty()) return;
                // Start a move drag operation on the cell
                Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                // Put cell's text into a clipboard to know what's being dragged
                ClipboardContent cc = new ClipboardContent();
                cc.putString(cell.getItem());
                db.setContent(cc);
                e.consume();
            });
            // User drags over cell
            cell.setOnDragOver(e ->{
                // Only allow the drag if it has a string and is a folder and not over itself
                if(e.getDragboard().hasString() && e.getGestureSource() != cell){
                    e.acceptTransferModes(TransferMode.MOVE);
                }
                e.consume();
            });
            // User has entered the drag area of a cell
            cell.setOnDragEntered(e->{
                // Change the styling of the cell to show it's a valid move
                if(e.getDragboard().hasString() && e.getGestureSource() != cell){
                    cell.setStyle("-fx-background-color: #6891de; -fx-background-radius: 6px;");
                }
            });
            // When the dragged item has left the cells area, remove the styling
            cell.setOnDragExited(_-> cell.setStyle(""));
            // User releases dragged item on cell
            cell.setOnDragDropped(e->{
                Dragboard db = e.getDragboard();
                // If there's no string, return;
                if(!db.hasString()) return;
                // Get item being dragged and item being dropped into
                TreeItem<String> draggedF = tree.getSelectionModel().getSelectedItem();
                TreeItem<String> droppingF = cell.getTreeItem();
                // Conditions to stop the file movement
                if(draggedF == null || droppingF == null) return;
                // If drop over same cell
                if(draggedF == droppingF) return;
                // If destination is already an ancestor of the dragged item
                if(isAncestor(draggedF, droppingF)) return;
                // if it is the root
                if(draggedF.getParent() == null) return;
                if(draggedF == tree.getRoot()) return;
                // If folder going into a file
                if(isFolder(draggedF) && isFile(droppingF)) return;
                // if item already in folder
                if (droppingF == draggedF.getParent()) return;
                String draggedName = draggedF.getValue().replace("\uD83D\uDCC1 ", "").replace("\uD83D\uDCC4 ", "");
                String droppingName = droppingF.getValue().replace("\uD83D\uDCC1 ", "").replace("\uD83D\uDCC4 ", "");
                try {
                    fc.move(draggedName, isFolder(draggedF), droppingName);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                // Remove from previous folder
                draggedF.getParent().getChildren().remove(draggedF);
                // Add to selected folder
                if(droppingF.getValue().startsWith("\uD83D\uDCC1 ")) {
                    droppingF.getChildren().add(draggedF);
                    // User can see change
                    droppingF.setExpanded(true);
                }else{
                    // Get parent of deck being dropped into
                    TreeItem<String> parent = droppingF.getParent();
                    // Find position of target deck
                    int idx = parent.getChildren().indexOf(droppingF);
                    // Insert one after target deck
                    parent.getChildren().add(idx+1, draggedF);
                }
                // Drop was successfully handled
                e.setDropCompleted(true);
                e.consume();
            });
            return cell;
        });
    }
    // Checks if tree item's ancestor is somewhere is above the item in the tree
    private boolean isAncestor(TreeItem<String> potentialAncestor, TreeItem<String> item){
        // Start at item's parent
        TreeItem<String> current = item.getParent();
        while (current != null){
            // if match found, block the drop
            if(current == potentialAncestor) return true;
            // move a level up if not
            current = current.getParent();
        }
        return false;
    }
    // Check if folder
    private boolean isFolder(TreeItem<String> potentialFolder){
        return potentialFolder.getValue().startsWith("\uD83D\uDCC1");
    }
    // Check if file
    private boolean isFile(TreeItem<String> potentialFile){
        return potentialFile.getValue().startsWith("\uD83D\uDCC4");
    }
    // Subroutine to add a new folder within the tree
    private void addFolder(TreeView<String> tree){
        TreeItem<String> selected = tree.getSelectionModel().getSelectedItem();
        // If nothing is selected, use the root, and if a deck is selected use its parent
        TreeItem<String> folder;
        if(selected == null){
            folder = tree.getRoot();
        } else if (isFile(selected)) {
            folder = selected.getParent();
        }else {
            folder = selected;
        }
        // Ask for folder name input
        Optional<String> folderName = CustomAlert.input(scene.getWindow(), "New Folder",
                "What do you want to name the folder?", "Enter Folder Name");
        String parentPath;
        if(folder == tree.getRoot()){
            // If target is root, use the user's folder path
            parentPath = fc.getUserFolderPath();
        }else{
            // Search for file path on disk
            String dirName = folder.getValue().replace("\uD83D\uDCC1 ", "");
            parentPath = fc.getFilePath(dirName, true);
        }
        int outcome;
        if(folderName.isPresent()){
            outcome = fc.createFolder(parentPath, folderName.get());
            switch(outcome){
                case 2 -> folder.getChildren().add(folder(folderName.get()));
                case 1 -> CustomAlert.error(scene.getWindow(), "Folder already exists");
                default -> CustomAlert.error(scene.getWindow(), "Did not work try again later");
            }
        }
    }
    // Subroutine to delete a folder
    private void deleteFolder(TreeView<String> tree) throws IOException {
        TreeItem<String> selected = tree.getSelectionModel().getSelectedItem();
        if(selected == null){
            CustomAlert.error(scene.getWindow(), "Pick something to delete");
            return;
        } else if (selected == tree.getRoot()) {
            CustomAlert.error(scene.getWindow(), "Cannot delete this folder");
            return;
        }else if (isFile(selected)) {
            CustomAlert.error(scene.getWindow(), "To delete files, go to the modification page");
            return;
        }
        boolean confirmed = CustomAlert.confirm(scene.getWindow(), "Confirmation of deletion",
                "Are you sure you would like to delete this folder? \n It will delete all contents");
        if(confirmed){
            boolean deleted = fc.deleteFolder(selected.getValue().replace("\uD83D\uDCC1 ", ""));
            CustomAlert.success(scene.getWindow(), "Folder successfully deleted");
            selected.getParent().getChildren().remove(selected);
        }
    }
    // Function to return folder node for the tree, starting expanded
    private TreeItem<String> folder(String folderName){
        TreeItem<String> folder = new TreeItem<>("\uD83D\uDCC1 " + folderName);
        folder.setExpanded(true);
        return folder;
    }
    // Function to return a deck node
    private TreeItem<String> file(String fileName){
        return new TreeItem<>("\uD83D\uDCC4 " + fileName);
    }
}
