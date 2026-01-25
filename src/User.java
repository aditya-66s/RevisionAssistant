import java.util.Date;

public class User {
    private String email;
    private String hashedPassword;
    private String saltBase64;
    private String lastLogin;
    public String resetToken;
    public long resetTokenExpiryTime;

    public User(String email, String hashedPassword, String saltBase64, String date){
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.saltBase64 = saltBase64;
        this.lastLogin = date;
    }

    public void setEmail(String email){
        this.email = email;
    }
    public void setPasswordHash(String passwordHash){
        this.hashedPassword = hashedPassword;
    }
    private void setSaltBase64(String salt){
        this.saltBase64 = salt;
    }
    public void setLastLogin(String lastLogin){
        this.lastLogin = lastLogin;
    }
    public void setResetToken(String resetToken){
        this.resetToken = resetToken;
    }
    public void setResetTokenExpiryTime(long resetTokenExpiryTime){
        this.resetTokenExpiryTime = resetTokenExpiryTime;
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
    public String getResetToken(){
        return this.resetToken;
    }
    public long getResetTokenExpiryTime() {
        return resetTokenExpiryTime;
    }
}
