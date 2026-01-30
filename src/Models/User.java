package Models;

// Represents a user account in the system
public class User {
    // Models.User's email address, used for login
    private String email;
    // Hashed version of passwor, cannot store pw as plain text
    private String hashedPassword;
    // Salt used during password hashing
    private String saltBase64;
    // Stores user's last login time as a string
    private String lastLogin;
    // Word used for account recovery
    private String recoveryWord;
    // Constructor initialises all user fields when a Models.User object is created
    public User(String email, String hashedPassword, String saltBase64, String date, String recoveryWord){
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.saltBase64 = saltBase64;
        this.lastLogin = date;
        this.recoveryWord = recoveryWord;
    }
    // Getters and Setters for attributes
    public void setEmail(String email){
        this.email = email;
    }
    // ERROR
//    public void setPasswordHash(String passwordHash){
//        this.hashedPassword = hashedPassword;
//    }
    public void setPasswordHash(String passwordHash){
        this.hashedPassword = passwordHash;
    }
    public void setSaltBase64(String salt){
        this.saltBase64 = salt;
    }
    public void setLastLogin(String lastLogin){
        this.lastLogin = lastLogin;
    }
    public void setRecoveryWord(String recoveryWord){
        this.recoveryWord = recoveryWord;
    }

    public String getEmail(){
        return this.email;
    }
    public String getHashedPassword(){
        return this.hashedPassword;
    }
    public String getSaltBase64() {
        return saltBase64;
    }
    public String getLastLogin(){
        return this.lastLogin;
    }
    public String getRecoveryWord(){
        return this.recoveryWord;
    }

}
