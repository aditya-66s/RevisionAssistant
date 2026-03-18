package Controllers;

import Components.CustomAlert;
import Models.Flashcard;
import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FlashcardController extends Controller {
    // Holds the string to the user's directory path
    private final String userFolderPath;
    // Hold then name of the flashcard deck file
    private String flashcardDeckName;
    private final String email;
    private final Path wrongAnswersPath;

    // Constructor to set the userFolderPath
    public FlashcardController(String email) {
        this.email = email;
        // Sets the path to the user folder
        this.userFolderPath = Paths.get(System.getProperty("user.dir"),"userFolders", email).toString();
        this.wrongAnswersPath = Paths.get(userFolderPath, "wrongAnswers.csv");
    }

    public String getFlashcardDeckName() {
        return flashcardDeckName;
    }
    public String getUserFolderPath() {
        return userFolderPath;
    }

    public void setFlashcardDeckName(String flashcardDeckName) {
        this.flashcardDeckName = flashcardDeckName;
    }
    // Retrieves path for the FCDeck
    private String getFCDeckPath() throws IOException {
        // If flashcard deck is stored directly in the user folder, then return it
         File rootFolder = new File(userFolderPath);
         return searchForCSV(rootFolder, flashcardDeckName);
    }
    //  Find the csv in the user folder
    private String searchForCSV(File searchDirectory, String deckName){
        File[] contents = searchDirectory.listFiles();
        // If no file, return null
        if (contents==null) return null;
        for(File f : contents){
            // For each file object in the array check, if directory
            // Else check if name matches
            if(f.isDirectory()){
                // Recurse into sub-folder
                String result = searchForCSV(f, deckName);
                // Return path
                if(result!=null) return result;
            } else if (f.getName().equals(deckName + ".csv")) {
                // Return path
                return f.getAbsolutePath();
            }
        }
        return null;
    }

    // Check if file and folder exist
    private void ensureFileExists(boolean adding) throws IOException {
        // Create a Folder if not already there
        Files.createDirectories(Paths.get(userFolderPath));
        // If not adding to a file then
        if (!adding){
            String deckPath = userFolderPath + "/" + flashcardDeckName + ".csv";
            // Create the file, if doesnt exist
            Path path = Paths.get(deckPath);
            if (!Files.exists(path)) {
                Files.createFile(path);
            } else {
                // If exists then add a random number to keep uniqueness
                int random = (int) (Math.random() * 100);
                flashcardDeckName = flashcardDeckName + random;
            }
        }
    }
    // Function to write the flashcard deck into .csv
    public boolean saveFlashcardDeck(List<Flashcard> cards, boolean adding) throws IOException {
        ensureFileExists(adding);
        String path = getFCDeckPath();
        if (path == null) path = userFolderPath + "/" + flashcardDeckName + ".csv";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (Flashcard c : cards) {
                // Add speech marks, replace speech marks inside
                bw.write("\"" // Opening speech marks
                        + c.getQuestion().replace("\"", "\"\"") // Get rid of any speech marks within by adding another
                        .replace("\n", "\\n") // Replace new lines with \n so readline does not break mid-card
                        + "\",\"" // Closing speech marks with comma
                        + c.getAnswer().replace("\"", "\"\"").replace("\n", "\\n") + "\"");
                bw.newLine();
            }
        }
        return true;
    }

    // Function to read in flashcards from a .csv into an array list
    public ArrayList<Flashcard> loadFlashcardDeck(String deckName) throws IOException {
        ArrayList<Flashcard> cards = new ArrayList<>();
        // Set deck to get from parameter
        this.flashcardDeckName = deckName;
        String path = getFCDeckPath();
        if (path == null) return cards;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            readData(br, cards, false);
        }catch (IOException e){
            // if not available, corrupt
            quarantineCorruptedCSV(e, Path.of(getFCDeckPath()));
        }
        return cards;
    }
    // Function to read in flashcards from a .csv into an array list
    public ArrayList<Flashcard> loadFCDeckFromPath(String path, boolean isWrongAnswers) throws IOException {
        ArrayList<Flashcard> cards = new ArrayList<>();
        // Set deck to get from parameter
        if (path == null) return cards;
        if(!new File(path).exists()) return cards;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            readData(br, cards, isWrongAnswers);
        }catch (IOException e){
            // if not available, corrupt
            quarantineCorruptedCSV(e, Path.of(getFCDeckPath()));
        }
        return cards;
    }
    // Re-usable method convert csv to flashcard object list
    private void readData(BufferedReader br, ArrayList<Flashcard> cards, boolean isWrongAnswers) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            // Split at ,
            String[] data = line.split("\",\"");
            if(data.length == 2){
                // Replace speech marks with empty space
                String question = data[0].replace("\"", "").replace("\\n", "\n");
                String answer = data[1].replace("\"", "").replace("\\n", "\n");
                // Add a new flashcard object
                cards.add(new Flashcard(question, answer));
            }
        }
        // Error indication to user, if file can't be read
        if(cards.isEmpty() && !isWrongAnswers) CustomAlert.error(javafx.stage.Window.getWindows().getFirst(),
                "Problems reading file, might not be of correct format");
    }
    // Return array list of all decks linked to an account
    public ArrayList<String> getFileContents() {
        ArrayList<String> deckNames = new ArrayList<>();
        File userFolder = new  File(userFolderPath);
        if (!userFolder.exists()){
            return  deckNames;
        }
        getAllCSV(userFolder, deckNames);
        return deckNames;
    }
    // Recursively search through folder and subfolders
    // to add all csv files to the arraylist
    private void getAllCSV(File folder, ArrayList<String> deckNames){
        File[] contents = folder.listFiles();
        if(contents == null) return;
        // Loop for each file
        for(File file : contents){
            // If folder, then recurse into the directory
            if(file.isDirectory()){
                getAllCSV(file, deckNames);
            } else if (file.getName().endsWith(".csv")) {
                // Add to the array list
                deckNames.add(file.getName().replace(".csv", ""));
            }
        }
    }
    // Function to change the name of a flashcard deck
    public boolean renameDeck(String oldName, String newName) {
       this.flashcardDeckName = oldName;
        try{
            String oldPath = getFCDeckPath();
            if(oldPath == null) return false;
            // Write into same folder
            Path oldNamePath = Paths.get(oldPath);
            // Joins directory with new filename to build destination
            Path newNamePath = oldNamePath.getParent().resolve(newName + ".csv");
            Files.move(oldNamePath, newNamePath, StandardCopyOption.REPLACE_EXISTING);
            // update after successful rename
            this.flashcardDeckName = newName;
            return true;
        }catch (IOException e){
            return false;
        }
    }

    // Procedure to delete the deck user selects
    public void deleteDeck(String deckName) throws IOException {
        // Set deck name so getFCDeckPath can search
        this.flashcardDeckName = deckName;
        // get path
        String path = getFCDeckPath();
        if (path == null) return;
        // delete from path
        Files.delete(Paths.get(path));
    }

    // Function to return the full directory structure for the current user
    public TreeItem<String> getUserDirectoryStructure(String email) {
        File userDirectory = new File(userFolderPath);
        // create root node of tree with first user folder
        TreeItem<String> root = new TreeItem<>("\uD83D\uDCC1 " + email);
        // Populate tree with remaining contents
        buildStructureTree(root, userDirectory);
        return root;
    }
    // Recursively search a directory and add its content to the tree
    private void buildStructureTree(TreeItem<String> node, File parentDirectory) {
        // Place all files and folders inside the parent directory into an array
        File[] allUserFiles = parentDirectory.listFiles();
        // If there are no files stop here
        if(allUserFiles == null) return;
        // Loop through all the contents
        for(File f : allUserFiles) {
            System.out.println("Found: " + f.getName() + " isDirectory: " + f.isDirectory());
            if(f.isDirectory()) {
                // If it's a folder, create a folder node with the prefix and file name
                TreeItem<String> folderNode = new TreeItem<>("\uD83D\uDCC1 " + f.getName());
                // Recursively go into the subfolder to add its contents
                buildStructureTree(folderNode, f);
                // add current node as a child to the current node
                node.getChildren().add(folderNode);
            }else if (f.getName().endsWith(".csv")) {
                // csv file storing deck info
                // Add as a leaf node to a tree, replace .csv with prefix and the file name
                node.getChildren().add(new TreeItem<>("\uD83D\uDCC4 " + f.getName().replace(".csv","")));
            }
        }
        // All other files which are not a folder or ending in .csv are ignored
    }
    // Returns a file/folder path
    public String getFilePath(String name, boolean isFolder) {
        File userDirectory = new File(userFolderPath);
        return searchForPath(userDirectory, name, isFolder);
    }
    // Recursively search user's directory for the file or file
    private String searchForPath(File directory, String name, boolean isFolder) {
        // Get all files and folders inside current folder
        File[] allUserFiles = directory.listFiles();
        if(allUserFiles == null) return null;
        // Loop through all items in directory
        for(File f : allUserFiles) {
            if(f.isDirectory()) {
                // If looking for folder, and name matches return the path
                if(isFolder && f.getName().equals(name)) return f.getAbsolutePath();
                // Recursively search the directory, go into it
                String filePath = searchForPath(f, name, isFolder);
                // If it's within the sub folder, return the path
                if(filePath != null) return filePath;
                // If looking for file, return the path if file
            }else if(!isFolder && f.getName().equals(name + ".csv")) return f.getAbsolutePath();
        }
        // If not found
        return null;
    }
    // Moving file/folder to a new destination
    public boolean move(String fName, boolean isFolder, String destinationFolder) throws IOException {
        // Find path of file or folder being moved
        String fPath = getFilePath(fName, isFolder);
        if(fPath == null) return false;
        // Find destination's path
        String dPath;
        if(destinationFolder.equals(email)){
            dPath = userFolderPath;
        }else{
            dPath = getFilePath(destinationFolder, true);
        }
        if(dPath == null) return false;
        // Create File objects
        File initial = new  File(fPath);
        File end = new  File(dPath + "/" + initial.getName());
        // switch file locations
        Files.move(initial.toPath(), end.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return true;
    }
    // Function to create a new directory
    public int createFolder(String parentPath, String folderName){
        // Create file object with specified path
        File newFolder = new File(parentPath + "/" + folderName);
        if(newFolder.exists()){
            return 1; // if exists return 1
        }else{
            if(newFolder.mkdir()){
                return 2; // if added return 2
            }else{
                return 3; // anything else add 3 return 3
            }
        }
    }
    // Function deleting a folder
    public boolean deleteFolder(String folderName) throws IOException {
        // get the path
        String path = getFilePath(folderName, true);
        // if null return not deleted
        if(path == null) return false;
        // Walk through all files and subfolders from given path
        Files.walk(Path.of(path))
                // Sort them in reverse order so they are deleted before parent directories
                .sorted(Comparator.reverseOrder())
                // Convert each path to an object
                .map(Path::toFile)
                // Delete each file
                .forEach(File::delete);
        return true;
    }

    // Procedure which allows the user to import a csv file into the system
    public boolean importCSV(Window owner) throws IOException {
        // Open the file chooser, filtered to csv files only
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        // Store user's chosen file
        File file = fileChooser.showOpenDialog(owner);
        // If user doesn't choose a file, don't do anything
        if(file == null) return false;
        // Check size limits of flashcard deck
        ArrayList<Flashcard> cardsList = loadFCDeckFromPath(file.getAbsolutePath(), false);
        if(!validateCSV(file ,"flashcard")){
            CustomAlert.error(owner, "Invalid file format");
            return false;
        }
        if(cardsList.isEmpty() || cardsList.size() > 60){
            CustomAlert.error(owner, "A deck needs a minimum of 1 card and can have a maximum of 60 cards");
            return false;
        }
        // Check if user is importing flashcard deck from another user, or one which already exists within their folder
        String allUserFolders = Paths.get(System.getProperty("user.dir"),"userFolders").toString();
        if(file.getCanonicalPath().startsWith(new File(allUserFolders).getCanonicalPath())){
            CustomAlert.error(owner, "Not allowed to import from this directory");
            return false;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String importDeck = file.getName().replace(".csv", "");
            // Set the file name
            setFlashcardDeckName(importDeck);
            // Use previously initiated array list
            readData(br, cardsList, false);
            // Use default save method
            saveFlashcardDeck(cardsList, true);
        }
        return true;
    }

    // Procedure to export a user-chosen flashcard deck to CSV or HTML, at a location of their choosing
    public boolean export(Window owner, boolean isHTML) throws IOException {
        CustomAlert.info(owner, "Choose the deck to export, can only be from this directory (and sub-directories of this one)");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export CSV File");
        fileChooser.setInitialDirectory(new File(userFolderPath));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        // Ask user where to save the file
        File chosenDeck = fileChooser.showOpenDialog(owner);
        System.out.println("Chosen deck: " + chosenDeck);
        CustomAlert.info(owner, "Choose the location to save the deck");
        File userFolder = new File(userFolderPath);
        // If cancelled return false
        if(chosenDeck == null){
            return false;
        }
        // If outside user directory, return false
        // Canonical path -> fully resolved path
        if(!chosenDeck.getCanonicalPath().startsWith(userFolder.getCanonicalPath())) return false;
        // Get file name
        String exportDeck = chosenDeck.getName().replace(".csv", "");
        // Ask user to choose where to save the file
        FileChooser locationChooser = new FileChooser();
        locationChooser.setTitle("Choose Save Location");
        // Set initial save file name to current file name
        if(!isHTML){
            locationChooser.setInitialFileName(chosenDeck.getName());
            // Filter for only .csv
            locationChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        }
        if(isHTML){
            locationChooser.setInitialFileName(chosenDeck.getName().replace("csv","html"));
            // Filter for only .html
            locationChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HTML Files", "*.html"));
        }

        File chosenLocation = locationChooser.showSaveDialog(owner);

        if(chosenLocation == null) return false;
        if(!isHTML) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(chosenLocation))) {
                for (Flashcard card : loadFlashcardDeck(exportDeck)) {
                    bw.write("\"" + card.getQuestion().replace("\"", "\"\"").replace("\n", "\\n")
                            + "\",\"" + card.getAnswer().replace("\"", "\"\"").replace("\n", "\\n") + "\"");
                    bw.newLine();
                }
            }
        }else{
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(chosenLocation))) {
                writeHTML(bw, exportDeck);
            }
        }
        return true;
    }

    // Procedure to write HTML File
    private void writeHTML(BufferedWriter bw, String exportDeck) throws IOException {
        bw.write("<!DOCTYPE html>");
        bw.newLine();
        bw.write("<html><head><title>" + exportDeck + "</title><style>");
        bw.newLine();
        bw.write("body{font-family: Arial, sans-serif; margin: 40px;}");
        bw.newLine();
        bw.write("h1{color: black; text-align: center;}");
        bw.newLine();
        bw.write("table{width: 100%; border-collapse: collapse; margin-top: 20px;}");
        bw.newLine();
        bw.write("th{background-color: black; color: white; padding: 40px; text-align: center; border-right: 1px solid black;}");
        bw.newLine();
        bw.write("td{padding: 50px; border-bottom: 1px solid black; text-align: center; border-right: 1px solid black; word-wrap: break-word; max-width: 250px;}");
        bw.newLine();
        bw.write("tr:nth-child(even){background-color: #f0f0f0;}");
        bw.newLine();
        bw.write("</style></head><body>");
        bw.newLine();
        bw.write("<h1>" + exportDeck + "</h1>");
        bw.newLine();
        bw.write("<table><tr><th>Question</th><th>Answer</th></tr>");
        for(Flashcard card : loadFlashcardDeck(exportDeck)){
            bw.write("<tr><td>" + card.getQuestion() + "</td><td>" + card.getAnswer() + "</td></tr>");
        }
        bw.write("</table></body></html>");
    }

    // Procedure to save user's wrong answers to WrongAnswers.csv for the WrongAnswers mode
    public void saveWrongAnswers(Flashcard card) throws IOException {
        ArrayList<Flashcard> cards = loadWrongAnswers();
        // Check if already exists
        boolean exists = cards.stream().anyMatch(
                c -> c.getQuestion().equals(card.getQuestion()) && c.getAnswer().equals(card.getAnswer())
        );
        if(exists) return;
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(wrongAnswersPath.toFile(), true))){
            // Add speech marks, replace speech marks inside
            bw.write("\"" // Opening speech marks
                    + card.getQuestion().replace("\"", "\"\"") // Get rid of any speech marks within by adding another
                    .replace("\n", "\\n") // Replace new lines with \n so readline does not break mid-card
                    + "\",\"" // Closing speech marks with comma
                    + card.getAnswer().replace("\"", "\"\"").replace("\n", "\\n"));
            bw.newLine();
        }
    }

    // load wrong answers into an array list
    public ArrayList<Flashcard> loadWrongAnswers() throws IOException {
        ArrayList<Flashcard> cards = loadFCDeckFromPath(wrongAnswersPath.toFile().getCanonicalPath(), true);
        return cards;
    }


}
