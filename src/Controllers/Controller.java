package Controllers;

import Models.Flashcard;
import Models.Settings;
import Models.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Controller {
    // Holds the application's current user
    private User currentUser;
    private Settings currentSettings;
    private  Settings draftSettings;


    // Ensures parent folder and CSV exists
    public void ensureCSVExists(Path csvPath) throws IOException {
        // Create a parent directory if it doesn't exist
        if(!Files.exists(csvPath.getParent())){
            Files.createDirectories(csvPath.getParent());
        }
        // Create CSV file if it doesn't exist
        if(!Files.exists(csvPath)){
            Files.createFile(csvPath);
        }
    }
    // Quarantining Corrupted files, creating a replacement
    public void quarantineCorruptedCSV(Exception cause, Path csvPath) throws IOException {
        // Ensure parent folder exists (do NOT recreate the file before moving it)
        if (!Files.exists(csvPath.getParent())) {
            Files.createDirectories(csvPath.getParent());
        }
        // Only quarantine if the corrupted file actually exists
        if(Files.exists(csvPath)){
            // Generate a timestamp so each corrupted back-up has a unique filename
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss"));
            // Create a new path in same directory
            // Keep the corrupted file for recovery or debugging
            Path backup = csvPath.resolveSibling("corrupted" + timestamp + ".csv");
            // Move corrupted file to back up location
            // REPLACE_EXISTING ensures there is no conflict
            Files.move(csvPath, backup, StandardCopyOption.REPLACE_EXISTING);
        }
        // Create an empty users.csv so the system can continue
        Files.createFile(csvPath);
    }
    // function to validate if the csv is suitable for import
    public boolean validateCSV(File source, String fileName) throws  IOException{
        try(BufferedReader br = new BufferedReader(new FileReader(source))){
            String line;
            while ((line = br.readLine()) != null) {
                if(fileName.equals("flashcard")) {
                    // Split at ,
                    String[] data = line.split("\",\"");
                    if(data.length != 2) return false;
                    // Replace speech marks with empty space
                    String question = data[0].replace("\"", "").replace("\\n", "\n");
                    String answer = data[1].replace("\"", "").replace("\\n", "\n");
                    if(isNumeric(question) || isNumeric(answer)) return false;
                }else{
                    String[] data = line.split(",");
                    if(fileName.equals("stats.csv") && data.length >= 11) return false;
                    if(fileName.equals("settings.csv") && data.length != 7) return false;
                }
            }
        }
        return true;
    }
    // Check if the value is numeric
    private boolean isNumeric(String s) {
        try {Double.parseDouble(s);return true;}
        catch (NumberFormatException e) {
            return false;}
    }
    // Getters
    public User getCurrentUser(){
        return currentUser;
    }
    public String getEmail(){
        return currentUser.getEmail();
    }
    public Settings getCurrentSettings(){
        return currentSettings;
    }

    // Setters
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
    public void setCurrentSettings(Settings currentSettings) {
        this.currentSettings = currentSettings;
    }
    public Settings getDraftSettings() {
        return draftSettings;
    }

    public void setDraftSettings(Settings settings) {
        this.draftSettings = settings;
    }
}
