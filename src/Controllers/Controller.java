package Controllers;

import Models.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Controller {
    // Holds the application's current user
    private User currentUser;

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
            Path backup = csvPath.resolveSibling("user_corrupted" + timestamp + ".csv");
            // Move corrupted file to back up location
            // REPLACE_EXISTING ensures there is no conflict
            Files.move(csvPath, backup, StandardCopyOption.REPLACE_EXISTING);
        }
        // Create an empty users.csv so the system can continue
        Files.createFile(csvPath);
    }

    // Getters
    public User getCurrentUser(){
        return currentUser;
    }

    // Setters
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
