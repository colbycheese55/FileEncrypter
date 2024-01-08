package src.backend;
import java.util.*;

public class UserManager{
    private HashMap<Integer, EncryptedUser> users;
    private String fileRawText;
    public ShareLog shareLog;
    public FileCountLog fileCounts;

    public UserManager() {
        users = new HashMap<Integer, EncryptedUser>();
        fileRawText = FileHandler.readUserFile();

        String[] sections = fileRawText.split("\\&\\&");
        // get default IV
        Encryption.setDefaultIV(sections[0]);
        // get current users
        if (sections.length >= 2)
            EncryptedUser.importUsers(sections[1], users);
        // get file count log
        if (sections.length >= 3)
            fileCounts = new FileCountLog(sections[2]);
        else
            fileCounts = new FileCountLog(null);
        // get share log
        if (sections.length >= 4)
            shareLog = new ShareLog(sections[3]);
        else
            shareLog = new ShareLog(null);
            
    }

    public boolean hasUsername(String username) { // only called by User.getUsername()
        return users.containsKey(username.hashCode());
    }

    public boolean correctPassword(String username, String password) { // only used by User.getPassword()
        EncryptedUser user = users.get(username.hashCode());
        String decryptedUserName = Encryption.decrypt(user.encryptedName, password);
        return username.equals(decryptedUserName);
    }

    public boolean createNewUser(String username, String password) {
        if (hasUsername(username))
            return false;
        EncryptedUser newUser = new EncryptedUser(Integer.toString(username.hashCode()), Encryption.encrypt(username, password), "");
        users.put(username.hashCode(), newUser);
        regenerateRawText();
        return true;
    }

    public void changeUserCredentials(String oldUserName, User user) {
        users.remove(oldUserName.hashCode());
        int newHashedName = user.getName().hashCode();
        String newEncryptedName = Encryption.encrypt(user.getName(), user.getPassword());
        EncryptedUser newEncryptedUser = new EncryptedUser(Integer.toString(newHashedName), newEncryptedName, null);
        users.put(newHashedName, newEncryptedUser);
        updateUserFile(user);
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
        String rawText = Encryption.getDefaultIV() + "&&";
        rawText += EncryptedUser.toString(users) + "&&";
        rawText += fileCounts.toString() + "&&";
        rawText += shareLog.toString() + "&&";
        fileRawText = rawText;
        FileHandler.writeUserFile(rawText);
    }


    private static class EncryptedUser {
        public String hashedName, encryptedName, rawText;

        public EncryptedUser(String hashedName, String encryptedName, String rawText) {
            this.hashedName = hashedName;
            this.encryptedName = encryptedName;
            this.rawText = rawText;
        }

        public String toString() {
            return hashedName + " " + encryptedName + " " + rawText;
        }

        public static void importUsers(String in, HashMap<Integer, EncryptedUser> users) {
            if (in.equals(""))
                return;
            for (String rawUserText: in.split("\n")) {
                String[] components = rawUserText.split("\s");
                String rawText = "";
                if (components.length == 3) 
                    rawText = components[2];
                EncryptedUser encryptedUser = new EncryptedUser(components[0], components[1], rawText);
                users.put(Integer.valueOf(components[0]), encryptedUser);
        }}

        public static String toString(HashMap<Integer, EncryptedUser> users) {
            String rawText = Arrays.toString(users.values().toArray());
            return rawText.replace(", ", "\n").replace("[", "").replace("]", "");
}}}