package src;
import java.util.*;

public class UserManager {
    public static final String USER_FILE_EXT = "USER_FILE.txt";  

    private HashMap<Integer, EncryptedUser> users;
    private String fileRawText;

    public UserManager() {
        users = new HashMap<Integer, EncryptedUser>();

        fileRawText = FileHandler.read(USER_FILE_EXT);
        if (fileRawText.equals(""))
            return;
        for (String rawUserText: fileRawText.split("\n")) {
            String[] components = rawUserText.split("\s");
            String rawText = "";
            if (components.length == 3) 
                rawText = components[2];
            EncryptedUser encryptedUser = new EncryptedUser(components[0], components[1], rawText);
            users.put(Integer.valueOf(components[0]), encryptedUser);
        }
    }

    // VERIFICATION METHODS
    public boolean hasUsername(String username) { // only called by User.getUsername()
        return users.containsKey(username.hashCode());
    }
    public boolean correctPassword(String username, String password) { // only used by User.getPassword()
        EncryptedUser user = users.get(username.hashCode());
        String decryptedUserName = Encryption.decrypt(user.encryptedName, password);
        return username.equals(decryptedUserName);
    }

    // User class EXCLUSIVE METHODS
    public void createNewUser(Scanner scan) {
        System.out.print("NEW USER CREATION: \nEnter username: ");
        String username = scan.nextLine();
        System.out.print("Enter password: ");
        String password = scan.nextLine();

        EncryptedUser newUser = new EncryptedUser(Integer.toString(username.hashCode()), Encryption.encrypt(username, password), "");
        users.put(username.hashCode(), newUser);
        regenerateRawText();

        System.out.println("User created, please re-authenticate\n");
    }
    public void changeUserCredentials(String oldUserName, User user) {
        users.remove(oldUserName.hashCode());
        int newHashedName = user.getName().hashCode();
        String newEncryptedName = Encryption.encrypt(user.getName(), user.getPassword());
        EncryptedUser newEncryptedUser = new EncryptedUser(Integer.toString(newHashedName), newEncryptedName, null);
        users.put(newHashedName, newEncryptedUser);
        updateUserFile(user);
        System.out.println("Successfully changed user credentials\n");
    }
    public HashMap<String, FileProfile> getAvailableFiles(String username, String password) {
        String rawText = users.get(username.hashCode()).rawText;
        String decryptedText = Encryption.decrypt(rawText, password);
        String[] entries = decryptedText.split("\n");
        HashMap<String, FileProfile> fileProfiles = new HashMap<String, FileProfile>();
        if (entries.length == 1 && decryptedText.equals(""))
            return fileProfiles;
        for (int i = 0; i < entries.length; i++) {
            FileProfile upNext = new FileProfile(entries[i]);
            fileProfiles.put(upNext.getName(), upNext);
        }
        return fileProfiles;
    }

    // HELPER METHODS
    public void updateUserFile(User user) {
        EncryptedUser encryptedUser = users.get(user.getName().hashCode());
        String rawText = Arrays.toString(user.fileProfiles.values().toArray());
        rawText = rawText.replace(", ", "\n").replace("[", "").replace("]", "");
        encryptedUser.rawText = Encryption.encrypt(rawText, user.getPassword());
        regenerateRawText();        
    }
    private void regenerateRawText() {
        String rawText = Arrays.toString(users.values().toArray());
        rawText = rawText.replace(", ", "\n").replace("[", "").replace("]", "");
        fileRawText = rawText;
        FileHandler.write(USER_FILE_EXT, fileRawText);
    }


    private class EncryptedUser {
        public String hashedName, encryptedName, rawText;

        public EncryptedUser(String hashedName, String encryptedName, String rawText) {
            this.hashedName = hashedName;
            this.encryptedName = encryptedName;
            this.rawText = rawText;
        }

        public String toString() {
            return hashedName + " " + encryptedName + " " + rawText;
        }
    }
}
