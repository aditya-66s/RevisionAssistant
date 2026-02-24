package Controllers;

import Models.Flashcard;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FlashcardController extends Controller {
    private final String userFolderPath;
    private String flashcardDeckName;


    public FlashcardController(String email) {
        this.userFolderPath = Paths.get(System.getProperty("user.dir"), "files", "userFolders", email).toString();
    }

    public String getFlashcardDeckName() {
        return flashcardDeckName;
    }

    public void setFlashcardDeckName(String flashcardDeckName) {
        this.flashcardDeckName = flashcardDeckName;
    }

    private String getFCDeckPath(){
        return  userFolderPath + "/" + flashcardDeckName + ".csv";
    }

    private void ensureFileExists(boolean adding) throws IOException {
        // Create a Folder if not already there
        Files.createDirectories(Paths.get(userFolderPath));
        if (!adding){
            if (!Files.exists(Paths.get(getFCDeckPath()))) {
                Files.createFile(Paths.get(getFCDeckPath()));
            } else {
                int random = (int) (Math.random() * 100);
                flashcardDeckName = flashcardDeckName + random;
            }
    }
    }

    public boolean saveFlashcardDeck(List<Flashcard> cards, boolean adding) throws IOException {
        ensureFileExists(adding);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(getFCDeckPath()))) {
            for (Flashcard c : cards) {
                // Add speech marks, replace speech amrks inside
                bw.write("\"" + c.getQuestion().replace("\"", "\"\"") + "\",\"" + c.getAnswer().replace("\"", "\"\"") + "\"");
                bw.newLine();
            }
        }
        return true;
    }


    public List<Flashcard> loadFlashcardDeck(String deckName) throws IOException {
        List<Flashcard> cards = new ArrayList<>();
        this.flashcardDeckName  = deckName;
        try (BufferedReader br = new BufferedReader(new FileReader(getFCDeckPath()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\",\"");
                if(data.length == 2){
                    String question = data[0].replace("\"", "");
                    String answer = data[1].replace("\"", "");
                    cards.add(new Flashcard(question, answer));
                }
            }
        }catch (IOException e){
            quarantineCorruptedCSV(e, Path.of(getFCDeckPath()));
        }
        return cards;
    }

    public List<String> getFlashcardDecks() throws IOException {
        List<String> deckNames = new ArrayList<>();
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

    //
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

    public void deleteDeck(String deckName) throws IOException {
        Files.delete(Paths.get(userFolderPath + "/" +  deckName + ".csv"));
    }
}
