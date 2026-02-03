package Controllers;

import Models.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;

public class AuthenticationController {
    // Stores most recently generated salt
    private byte[] salt;
    // Holds the application's current user
    private User currentUser;
    //getResourceAsStream() is for reading packaged resources, not for storing/editing data
    private final Path csvPath = Paths.get(System.getProperty("user.dir"), "files", "users.csv");
    // Registers a user
    public boolean registerUser(String email, String inpPassword, String recoveryWord) throws NoSuchAlgorithmException, IOException {
        // Check if email does not already exist within the CSV
        if (!doesEmailExist(email)) {
            // Create a new random salt, preventing identical hashes for same password
            generateSalt();
            // Hash password using SHA-512 and generated salt
            String hashedPassword = hashPassword(inpPassword, getSalt());
            // Store salt so it can be safely written into the CSV
            String saltBase64 = Base64.getEncoder().encodeToString(getSalt());
            // Create new user object
            User user = new User(email, hashedPassword, saltBase64, getDate(), recoveryWord);
            // Opens in append mode, won't overwrite the file
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvPath.toString(), true))) {
                bw.write(email + "," + hashedPassword + "," + saltBase64 + "," + user.getLastLogin() + "," + recoveryWord);
                bw.newLine();
            }
            currentUser = user;
            return true;
        }else{
            // Email already in CSV
            return false;
        }
    }
    // Logs in a user
    public boolean loginUser(String email, String inpPassword) throws NoSuchAlgorithmException, IOException {
        // Only attempt if email is found
        if (doesEmailExist(email)) {
            // temp will hold the user we find by email
            User temp = findUserByEmail(email);
            // Check if password matches,
            // Objects.requireNonNull(temp) prevents a NullPointerException without explanation, (if null, it throws immediately)
            boolean loggedIn = verifyPassword(inpPassword, Objects.requireNonNull(temp).getHashedPassword(), temp.getSaltBase64());
            if(loggedIn){
                currentUser = temp;
                return true;
            }
        }
        // Email not found
        return false;
    }
    // Reads users.csv and converts each row into a user object
    public ArrayList<User> convertCSVtoArrayList() throws IOException {
        // Ensure folders and CSV exist before reading
        ensureCSVExists();
        ArrayList<User> users = new ArrayList<>();
        // Read CSV line by line
        try(BufferedReader br = new BufferedReader(new FileReader(csvPath.toString()))) {
            String line;
            while((line = br.readLine())!=null){
                String[] temp = line.split(",");
                // Validate row lengths to avoid ArrayIndexOutOfBoundsException, throw an exception
                if (temp.length < 5) {
                    throw new IllegalArgumentException("Corrupted Row: " + line);
                }
                // Create a Models.User from CSV columns
                User newUser = new User(temp[0], temp[1], temp[2], temp[3], temp[4]);
                users.add(newUser);
            }
        } catch (Exception e) {
            quarantineCorruptedCSV(e);
            // Act as if there are no users if corrupted.
            return new ArrayList<>();
        }
        return users;
    }
    // Checks if a given email exists in the CSV
    public boolean doesEmailExist(String email) throws IOException {
        ArrayList<User> users = convertCSVtoArrayList();
        for (User user : users) {
            // Will not be case-sensitive
            if (user.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }
    // Finds and returns the Models.User object for given email
    public User findUserByEmail(String email) throws IOException {
        ArrayList<User> users = convertCSVtoArrayList();
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }
    // Hash password algorithm via SHA-512 (even though, others are better, I'll describe in write up); throws exception in case MessageDigest instance fails.
    public String hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        // Create a MessageDigest instance using the SHA-512 hashing algorithm
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        // Add the salt to the has function so same passwords can produce different hashes per user
        md.update(salt);
        // Hash the password bytes, giving a one-way hash
        byte[] hashedPassword = md.digest(password.getBytes());
        // Convert the raw hash bytes to a base 64 string so it can be stored safely in the .csv file
        return Base64.getEncoder().encodeToString(hashedPassword);
    }
    // Verify the password is correct, taking the user's input password comparing with hashed password.
    public boolean verifyPassword(String inpPassword, String hashedPassword, String saltBase64) throws NoSuchAlgorithmException {
        // Convert the string salt to a byte array
        byte[] salt = Base64.getDecoder().decode(saltBase64);
        // Create a MessageDirect instance using the same hashing algorithm as before
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        // Add the same salt which the hash password algorithm used
        md.update(salt);
        // hash the input password into a byte array
        byte[] hashedInputPassword = md.digest(inpPassword.getBytes());
        // convert the byte array into a string
        String newHash = Base64.getEncoder().encodeToString(hashedInputPassword);
        // true if the hashed input matches the hashed password
        return newHash.equals(hashedPassword);
    }
    // Reset user's password
    public boolean resetPassword(User temp, String newPassword) throws NoSuchAlgorithmException, IOException {
        // new salt ensures old hash cannot be used and increases security
        generateSalt();
        String hashedPassword = hashPassword(newPassword, getSalt());
        String saltBase64 = Base64.getEncoder().encodeToString(getSalt());
        // Load all users and update the matching one
        ArrayList<User> users = convertCSVtoArrayList();
        for(User u : users){
            if(u.getEmail().equals(temp.getEmail())){
                u.setPasswordHash(hashedPassword);
                u.setSaltBase64(saltBase64);
                break;
            }
        }
        // Rewrite the entire file, so updates persist
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvPath.toString()))) {
            for (User u : users) {
                //DEBUGGED VIA
//                String line = u.getEmail() + "," + u.getHashedPassword() + "," + u.getSaltBase64() + "," + u.getLastLogin() + "," + u.getRecoveryWord();
//                bw.write(line);
//                System.out.println(line);
                // writes row back into file
                bw.write(u.getEmail() + "," + u.getHashedPassword() + "," + u.getSaltBase64() + "," + u.getLastLogin() + "," + u.getRecoveryWord());
                bw.newLine();
            }
        }
        return true;
    }
    // Ensures parent folder and CSV exists
    private void ensureCSVExists() throws IOException {
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
    private void quarantineCorruptedCSV(Exception cause) throws IOException {
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
    public void generateSalt(){
        // Cryptographic secure random number gen (better than java.util.random)
        SecureRandom random = new SecureRandom();
        // 16 byte salt to ensure each password hash is unique
        byte[] salt = new byte[16];
        // generating random salt bytes and store them in the salt
        random.nextBytes(salt);
        this.salt = salt;
    }
    //Setters

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    // Getters for attributes
    public byte[] getSalt(){
        return this.salt;
    }
    public User getCurrentUser(){
        return this.currentUser;
    }
    public String getDate(){
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter expectedFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return time.format(expectedFormat);
    }
}
