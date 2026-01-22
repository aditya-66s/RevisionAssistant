import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;


public class AuthenticationController {
    private byte[] salt;


    public void registerUser(String email, String inpPassword) throws NoSuchAlgorithmException, IOException {
        // make this a function, Search through CSV find email, if no then continue, if yes return error message
        generateSalt();
        String hashedPassword = hashPassword(inpPassword,getSalt());
        String saltBase64 = Base64.getEncoder().encodeToString(getSalt());
        User user = new User(email, hashedPassword, saltBase64, getDate());
        // Opens in append mode, won't overwrite the file
        try (FileWriter addUser = new FileWriter("file:src/resources/files/users.csv", true)) {
            addUser.write(user.getEmail() + "," + user.getHashedPassword() + "," + user.getSaltBase64() + "," + user.getLastLogin());
        }
    }
    public ArrayList<User> convertCSVtoArrayList() throws IOException {
        ArrayList<User> users = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader("file:src/resources/files/users.csv"))){
            String line;
            while((line = br.readLine())!=null){
                String[] temp = line.split(",");
                users.add(new User(temp[0], temp[1], temp[2], temp[3]));
            }
        };
        return users;
    }
    public boolean doesEmailExist(){

        return false;
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

    public void generateSalt(){
        // Cryptographic secure random number gen (better than java.util.random)
        SecureRandom random = new SecureRandom();
        // 16 byte salt to ensure each password hash is unique
        byte[] salt = new byte[16];
        // generating random salt bytes and store them in the salt
        random.nextBytes(salt);
        this.salt = salt;
    }
    public byte[] getSalt(){
        return this.salt;
    }
    public String getDate(){
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter expectedFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return time.format(expectedFormat);
    }
}
