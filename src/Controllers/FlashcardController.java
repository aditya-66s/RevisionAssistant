package Controllers;

import Models.Flashcard;
import javafx.scene.control.TreeItem;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FlashcardController extends Controller {
    // Holds the string to the user's directory path
    private final String userFolderPath;
    // Hold then name of the flashcard deck file
    private String flashcardDeckName;

    // Constructor to set the userFolderPath
    public FlashcardController(String email) {
        this.userFolderPath = Paths.get(System.getProperty("user.dir"),"userFolders", email).toString();
    }

    public String getFlashcardDeckName() {
        return flashcardDeckName;
    }

    public void setFlashcardDeckName(String flashcardDeckName) {
        this.flashcardDeckName = flashcardDeckName;
    }
    // Create path for the FCDeck
    private String getFCDeckPath() throws IOException {
        // If flashcard deck is stored directly in the user folder, then return it
        File rootFile = new File(userFolderPath + "/" + flashcardDeckName + ".csv");
        if (rootFile.exists()) return rootFile.getPath();
        File userDirectory = new File(userFolderPath);
        // Add subfolders into a file array
        File[] subfolders = userDirectory.listFiles(File::isDirectory);
        // Search through subfolders
        if (subfolders!=null){
            for(File folder : subfolders){
                // Build the path and return if it exists there
                File f = new File(folder.getPath() + "/" + flashcardDeckName + ".csv");
                if (f.exists()) return f.getPath();
            }
        }
        return rootFile.getPath();
    }
    // Check if file and folder exist
    private void ensureFileExists(boolean adding) throws IOException {
        // Create a Folder if not already there
        Files.createDirectories(Paths.get(userFolderPath));
        // If not adding to a file then
        if (!adding){
            // Create the file, if doesnt exist
            if (!Files.exists(Paths.get(getFCDeckPath()))) {
                Files.createFile(Paths.get(getFCDeckPath()));
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
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(getFCDeckPath()))) {
            for (Flashcard c : cards) {
                // Add speech marks, replace speech marks inside
                bw.write("\"" + c.getQuestion().replace("\"", "\"\"") + "\",\"" + c.getAnswer().replace("\"", "\"\"") + "\"");
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
        try (BufferedReader br = new BufferedReader(new FileReader(getFCDeckPath()))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split at ,
                String[] data = line.split("\",\"");
                if(data.length == 2){
                    // Replace speech marks with empty space
                    String question = data[0].replace("\"", "");
                    String answer = data[1].replace("\"", "");
                    // Add a new flashcard object
                    cards.add(new Flashcard(question, answer));
                }
            }
        }catch (IOException e){
            // if not available, corrupt
            quarantineCorruptedCSV(e, Path.of(getFCDeckPath()));
        }
        return cards;
    }
    // Return array list of all decks linked to an account
    public ArrayList<String> getFlashcardDecks() throws IOException {
        ArrayList<String> deckNames = new ArrayList<>();
        File userFolder = new  File(userFolderPath);
        if (!userFolder.exists()) {
            return  deckNames;
        }
        for(File f : Objects.requireNonNull(userFolder.listFiles())){
            if(f.getName().endsWith(".csv")){
                deckNames.add(f.getName().replace(".csv",""));
            }
        }
        return deckNames;
    }

    // Function to change the name of a flashcard deck
    public boolean renameDeck(String oldName, String newName) {
        Path oldPath = Paths.get(userFolderPath + "/" +  oldName + ".csv");
        Path newPath = Paths.get(userFolderPath + "/" +  newName + ".csv");
        try{
            Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
            return true;
        }catch (IOException e){
            return false;
        }
    }

    // Procedure to delete the deck user selects
    public void deleteDeck(String deckName) throws IOException {
        Files.delete(Paths.get(userFolderPath + "/" +  deckName + ".csv"));
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
    public void setFileName(){

    }
}
