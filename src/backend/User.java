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

    public static User authenticate(Scanner scan, UserManager userManager) {
        System.out.println("AUTHENTICATE: Enter your credentials");
        String username = getUsername(scan, userManager);
        String password = getPassword(username, userManager);
        HashMap<String, FileProfile> fileProfiles = userManager.getAvailableFiles(username, password);
        System.out.println("");
        return new User(username, password, fileProfiles);
    }

    private static String getUsername(Scanner scan, UserManager userManager) {
        System.out.print("Enter username: ");
        String entry = scan.nextLine();
        if (entry.equals("EXIT"))
            System.exit(0);
        if (entry.equals("NEW_USER")) {
            userManager.createNewUser(scan);
            return getUsername(scan, userManager);
        }
        if (userManager.hasUsername(entry))
            return entry;
        System.out.println("No such user exists. Try again!");
        return getUsername(scan, userManager);
    }
    private static String getPassword(String username, UserManager userManager) {
        System.out.print("Enter password (hidden): ");
        String entry = new String(System.console().readPassword());
        if (entry.equals("EXIT"))
            System.exit(0);
        if (userManager.correctPassword(username, entry))
            return entry;
        System.out.println("Wrong password. Try again!");
        return getPassword(username, userManager);
    }

    public String getName() {return username;}
    public String getPassword() {return password;}

    public void changeCredentials(Scanner scan, UserManager userManager) {
        String oldName = username;
        System.out.println("Enter a new username (or nothing to leave unchanged)");
        String in = scan.nextLine().replace(" ", "");
        if (!in.equals(""))
                username = in;
        System.out.println("Enter a new password (or nothing to leave unchanged)");
        in = scan.nextLine();
        if (!in.equals(""))
            password = in;
        userManager.changeUserCredentials(oldName, this);
    }
}
