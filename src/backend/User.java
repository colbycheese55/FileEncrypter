package src.backend;
import java.util.*;

public class User {
    private String username, password;
    public HashMap<String, FileProfile> fileProfiles;

    public User(String username, String password, HashMap<String, FileProfile> fileProfiles) {
        this.username = username;
        this.password = password;
        this.fileProfiles = fileProfiles;
    }

    public static User authenticate(String username, String password, UserManager userManager) {
        if (!userManager.hasUsername(username) || !userManager.correctPassword(username, password))
            return null;
        HashMap<String, FileProfile> fileProfiles = userManager.getAvailableFiles(username, password);
        return new User(username, password, fileProfiles);
    }

    public String getName() {return username;}
    public String getPassword() {return password;}

    public boolean changeCredentials(String newUsername, String newPassword, UserManager userManager) {
        String oldName = username;
        if (!oldName.equals(newUsername) && userManager.hasUsername(newUsername))
            return false;
        username = newUsername;
        password = newPassword;
        userManager.changeUserCredentials(oldName, this);
        return true;
    }
}