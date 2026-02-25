package Controllers;

import Models.Flashcard;
import javafx.scene.control.TreeItem;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class FlashcardController extends Controller {
    // Holds the string to the user's directory path
    private final String userFolderPath;
    // Hold then name of the flashcard deck file
    private String flashcardDeckName;
    private String email;

    // Constructor to set the userFolderPath
    public FlashcardController(String email) {
        this.email = email;
        this.userFolderPath = Paths.get(System.getProperty("user.dir"),"userFolders", email).toString();
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
    // Create path for the FCDeck
    private String getFCDeckPath() throws IOException {
        // If flashcard deck is stored directly in the user folder, then return it
         File rootFolder = new File(userFolderPath);
         return searchForCSV(rootFolder, flashcardDeckName);
    }
    //  Find the csv in the user folder
    private String searchForCSV(File searchDirectory, String deckName){
        File[] contents = searchDirectory.listFiles();
        if (contents==null) return null;
        for(File f : contents){
            if(f.isDirectory()){
                String result = searchForCSV(f, deckName);
                if(result!=null) return result;
            } else if (f.getName().equals(deckName + ".csv")) {
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
        String path = getFCDeckPath();
        if (path == null) return cards;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
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
}
