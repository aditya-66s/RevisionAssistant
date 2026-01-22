import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AuthenticationController {
    private byte[] salt;

    // Hash password algorithm via SHA-512 (even though, others are better, I'll describe in write up); throws exception in case MessageDigest instance fails.
    public String hashPassword(String password) throws NoSuchAlgorithmException {
        // Generate the salt via the generate salt method
        generateSalt();
        // Create a MessageDigest instance using the SHA-512 hashing algorithm
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        // Add the salt to the has function so same passwords can produce different hashes per user
        md.update(getSalt());
        // Hash the password bytes, giving a one-way hash
        byte[] hashedPassword = md.digest(password.getBytes());
        // Convert the raw hash bytes to a base 64 string so it can be stored safely in the .csv file
        return Base64.getEncoder().encodeToString(hashedPassword);
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
}
