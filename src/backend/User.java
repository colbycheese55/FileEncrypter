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

    // public void changeCredentials(Scanner scan, UserManager userManager) {
    //     String oldName = username;
    //     System.out.println("Enter a new username (or nothing to leave unchanged)");
    //     String in = scan.nextLine().replace(" ", "");
    //     if (!in.equals(""))
    //             username = in;
    //     System.out.println("Enter a new password (or nothing to leave unchanged)");
    //     in = scan.nextLine();
    //     if (!in.equals(""))
    //         password = in;
    //     userManager.changeUserCredentials(oldName, this);
    // }
}
