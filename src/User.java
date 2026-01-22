import java.util.Date;

public class User {
    private String email;
    private String passwordHash;
    private Date lastLogin;

    public User(String email, String passwordHash){
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public void setEmail(String email){
        this.email = email;
    }
    public void setPasswordHash(String passwordHash){
        this.passwordHash = passwordHash;
    }
    public void setLastLogin(Date lastLogin){
        this.lastLogin = lastLogin;
    }

    public String getEmail(){
        return this.email;
    }
    public String getPasswordHash(){
        return this.passwordHash;
    }
    public Date getLastLogin(){
        return this.lastLogin;
    }
}
