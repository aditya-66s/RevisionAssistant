package Controllers;

import Components.ScreenManager;
import Models.Settings;
import Models.User;
import Screens.AuthScreen;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
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

public class  AuthenticationController extends Controller {
    // Create a SettingsController object
    private final SettingsController sc = new SettingsController();
    // Stores most recently generated salt
    private byte[] salt;
    //getResourceAsStream() is for reading packaged resources, not for storing/editing data
    private final Path csvPath = Paths.get(System.getProperty("user.dir"), "files", "users.csv");
    // Encryption / Decryption
    // Hardcoded 16-byte AES key -> 16 characters exactly for AES-128
    private static final String encryptionKey = "RevAssistant2026";
    // Encrypt a string and returns base 66 encrypted string
    private String encrypt(String line){
        try{
            // Create an AES secret key using the predefined encryption key
            SecretKey key = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), "AES");
            // Create and configure the cipher for encryption with padding
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            // Convert the text into bytes and encrypt it
            byte[] encrypted = cipher.doFinal(line.getBytes(StandardCharsets.UTF_8));
            // Convert encrypted bytes to base 64 so it can be saved safely as text
            return Base64.getEncoder().encodeToString(encrypted);
        }catch(Exception e){
            throw new RuntimeException("Encryption did not work: " + e.getMessage());
        }
    }

    // Decrypt a base 64 encoded string to plain text
    private String decrypt(String line){
        try{
            // Create an AES secret key using the predefined encryption key
            SecretKey key = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), "AES");
            // Create and configure the cipher for encryption with padding
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            // Decode the Base64 string back to plain text
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(line));
            return new String(decrypted, StandardCharsets.UTF_8);
        }catch(Exception e){
            throw new RuntimeException("Decryption did not work: " + e.getMessage());
        }
    }
    // Read encrypted users.csv, decrypt and returns the line in an ArrayList
    private ArrayList<String> getDecryptedLines() throws IOException{
        ArrayList<String> lines = new ArrayList<>();
        // Read all content
        byte[] fileBytes = Files.readAllBytes(csvPath);
        // If file is empty, return empty list
        if(fileBytes.length == 0) return lines;
        // Decrypt the entire file
        String decrypted = decrypt(new String(fileBytes, StandardCharsets.UTF_8).trim());
        // Iterate through and add to ArrayList
        for(String line : decrypted.split("\n")){
            if(!line.isBlank()) lines.add(line);
        }
        return lines;
    }
    // Encrypt the given content and write to users.csv
    private void writeEncrypted(String text) throws IOException{
        // get encrypted text
        String encrypted = encrypt(text);
        // Write to the file
        Files.writeString(csvPath, encrypted);
    }
    // Registers a user
    public boolean registerUser(String email, String inpPassword, String recoveryWord, SettingsController sc) throws NoSuchAlgorithmException, IOException {
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
            // Load existing users and add new and then rewrite entire encrypted files
            ArrayList<User> users = convertCSVtoArrayList();
            users.add(user);
            rewriteUsersCSV(users);
            // On register the system's current user will be set to the temp user
            // Settings will be loaded and the SettingsController set into the ScreenManager class
            setCurrentUser(user);
            sc.createDefaultSettings(email);
            sc.setCurrentSettings(sc.loadSettings(email));
            setSettingsControllertoSM(sc);
            // Write stats into stats.csv for new user
            new StatsController().createDefaultUserStats(email);
            return true;
        }else{
            // Email already in CSV
            return false;
        }
    }
    // Logs in a user
    public boolean loginUser(String email, String inpPassword, SettingsController sc) throws NoSuchAlgorithmException, IOException {
        // Only attempt if email is found
        if (doesEmailExist(email)) {
            // temp will hold the user we find by email
            User temp = findUserByEmail(email);
            if (temp == null) {
                return false;
            }
            // Check if password matches,
            // Objects.requireNonNull(temp) prevents a NullPointerException without explanation, (if null, it throws immediately)
            boolean loggedIn = verifyPassword(inpPassword, Objects.requireNonNull(temp).getHashedPassword(), temp.getSaltBase64());
            if(loggedIn){
                // On login the system's current user will be set to the temp user
                // Settings will be loaded and the SettingsController set into the ScreenManager class
                setCurrentUser(temp);
                Settings userSettings = sc.loadSettings(email);
                sc.setCurrentSettings(userSettings);
                setSettingsControllertoSM(sc);
                // Write default stats if not saved already, via a temporary object
//                if(!new StatsController().isUserStatsSaved(email)) new StatsController().createDefaultUserStats(email);
                return true;
            }
        }
        // Email not found
        return false;
    }
    // Reads users.csv and converts each row into a user object
    public ArrayList<User> convertCSVtoArrayList() throws IOException {
        // Ensure folders and CSV exist before reading
        ensureCSVExists(csvPath);
        ArrayList<User> users = new ArrayList<>();
        // Read CSV line by line
        try {
            ArrayList<String> lines = getDecryptedLines();
            for (String line : lines) {
                String[] temp = line.split(",");
                if(temp.length < 5) throw new IllegalArgumentException("Corrupted Row: " + line);
                users.add(new User(temp[0], temp[1], temp[2], temp[3], temp[4]));
            }
        } catch (Exception e) {
            quarantineCorruptedCSV(e, csvPath);
            // Act as if there are no users if corrupted.
            return new ArrayList<>();
        }
        return users;
    }
    // Will be used to update CSV when user makers changes
    private void rewriteUsersCSV(ArrayList<User> users) throws IOException {
        StringBuilder sb = new StringBuilder();
        // Rewrite the entire file, so updates persist
        for (User u : users) {
            sb.append(u.getEmail()).append(",").append(u.getHashedPassword()).append(",").append(u.getSaltBase64()).append(",")
                    .append(u.getLastLogin()).append(",").append(u.getRecoveryWord()).append("\n");
        }
        writeEncrypted(sb.toString());
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
        // Write into file
        rewriteUsersCSV(users);
        return true;
    }
    // Change a user's email
    public boolean changeEmail(User temp, String newEmail) throws IOException {
        ArrayList<User> users = convertCSVtoArrayList();
        // Load all users and update the matching one
        for(User u : users){
            if(u.getEmail().equals(temp.getEmail())){
                u.setEmail(newEmail);
                break;
            }
        }
        // Write into file
        rewriteUsersCSV(users);
        // Update the stats.csv and settings.csv, replace old email with new
        sc.updateEmail(temp.getEmail(), newEmail);
        new StatsController().updateEmail(temp.getEmail(), newEmail);
        // Rename the Sessions csv
        Path oldSession = Paths.get(System.getProperty("user.dir"), "files", "sessions", temp.getEmail() + ".csv");
        Path newSession = Paths.get(System.getProperty("user.dir"), "files", "sessions", newEmail + ".csv");
        if(Files.exists(oldSession)) Files.move(oldSession, newSession, StandardCopyOption.REPLACE_EXISTING);
        // Rename the decks folder
        Path oldFolder = Paths.get(System.getProperty("user.dir"), "userFolders", temp.getEmail());
        Path newFolder = Paths.get(System.getProperty("user.dir"), "userFolders", newEmail);
        if(Files.exists(oldFolder)) Files.move(oldFolder, newFolder, StandardCopyOption.ATOMIC_MOVE);
        // Update current user in memory
        temp.setEmail(newEmail);
        setCurrentUser(temp);
        return true;
    }
    // Change a user's recovery word
    public boolean changeRecoveryWord(User temp, String newRecoveryWord) throws IOException {
        ArrayList<User> users = convertCSVtoArrayList();
        for(User u : users){
            if(u.getEmail().equals(temp.getEmail())){
                u.setRecoveryWord(newRecoveryWord);
                break;
            }
        }
        // Write into file
        rewriteUsersCSV(users);
        return true;
    }
//    // Will be used to update CSV when user makers changes
//    private void rewriteUsersCSV(ArrayList<User> users) throws IOException {
//        // Rewrite the entire file, so updates persist
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvPath.toString()))) {
//            for (User u : users) {
//                // writes row back into file
//                bw.write(u.getEmail() + "," + u.getHashedPassword() + "," + u.getSaltBase64() + "," + u.getLastLogin() + "," + u.getRecoveryWord());
//                bw.newLine();
//            }
//        }
//    }
    // System Logout
    public void logout(){
        if (getCurrentUser() != null){
            getCurrentUser().setLastLogin(getDate());
        }
        // add a save data method, will be added when more data as been added to system
        // System has no current user, so data cannot be accessed
        setCurrentUser(null);
        // User cannot find a way to go back into Main Menu after logging out
        ScreenManager.clearHistory();
        // Switch to Log in/Registration Screen
        setSettingsControllertoSM(sc);
        ScreenManager.switchScene(new AuthScreen(new AuthenticationController(), sc).create());
    }
    private void generateSalt(){
        // Cryptographic secure random number gen (better than java.util.random)
        SecureRandom random = new SecureRandom();
        // 16 byte salt to ensure each password hash is unique
        byte[] salt = new byte[16];
        // generating random salt bytes and store them in the salt
        random.nextBytes(salt);
        this.salt = salt;
    }
    //Setters
    // Getters for attributes
    public byte[] getSalt(){
        return this.salt;
    }
    public String getDate(){
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter expectedFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return time.format(expectedFormat);
    }
    private void setSettingsControllertoSM(SettingsController sc){
        ScreenManager.setSC(sc);
    }

    public SettingsController getSettingsController(){
        return sc;
    }
}
