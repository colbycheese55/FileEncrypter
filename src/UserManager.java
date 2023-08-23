package src;
import java.util.*;
import java.util.Map.Entry;

public class UserManager{
    private HashMap<Integer, EncryptedUser> users;
    //private HashMap<String, Integer> fileCounts;
    private String fileRawText;

    public UserManager() {
        users = new HashMap<Integer, EncryptedUser>();
        //fileCounts = new HashMap<String, Integer>();
        fileRawText = FileHandler.readUserFile();

        String[] sections = fileRawText.split("\\&\\&");
        Encryption.setDefaultIV(sections[0]);
        if (sections.length >= 2)
            EncryptedUser.importUsers(sections[1], users);
        // if (sections.length >= 3)
        //     FileCount.importFileCounts(sections[2], fileCounts);
            
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
        if (hasUsername(username)) {
            System.out.println("User already exists! Try again with a different username.");
            createNewUser(scan);
            return;
        }
        if (username.equals("NEW_USER") || username.equals("EXIT")) {
            System.out.println("Invalid username! Try again with a different username.");
            createNewUser(scan);
            return;
        }
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
        String rawText = Encryption.getDefaultIV() + "&&";
        rawText += EncryptedUser.toString(users) + "&&";
        //rawText += FileCount.toString(fileCounts) + "&&";
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
        }
    }

    // private static class FileCount {
    //     public static void importFileCounts(String in, HashMap<String, Integer> fileCounts) {
    //         for (String item: in.split("\n")) {
    //             String[] components = item.split("\s");
    //             fileCounts.put(components[0], Integer.valueOf(components[1]));
    //     }}
    //     public static String toString(HashMap<String, Integer> fileCounts) {
    //         String out = "";
    //         for (Entry<String, Integer> entry: fileCounts.entrySet())
    //             out += entry.getKey() + " " + entry.getValue() + "\n";
    //         return out;
    //         }
    // }

    // private static class SharedFile {
    //     public String encryptedFileProfile;
    //     public int receivingUser;
    //     public boolean OTP; // 1 time password

    //     public SharedFile(String encryptedFileProfile, int receivingUser, boolean OTP) { // TODO
    //         this.encryptedFileProfile = encryptedFileProfile;
    //         this.receivingUser = receivingUser;
    //         this.OTP = OTP;
    //     }
    //     public SharedFile(FileProfile fileProfile, String receivingUser, String password, boolean OTP) {
    //         this.encryptedFileProfile = Encryption.encrypt(fileProfile.toString(), password);
    //         this.receivingUser = receivingUser.hashCode();
    //         this.OTP = OTP;
    //     }

    //     public String toString() {
    //         return receivingUser + "|" + encryptedFileProfile + "|" + OTP;
    //     }
    //     public static void importSharedFiles(String in, HashMap<Integer, Set<SharedFile>> sharedFiles) {
    //         for (String item: in.split("\n")) {
    //             String[] components = item.split("\s");
    //             Set<SharedFile> files = sharedFiles.get(Integer.valueOf(components[0]));
    //             if (files == null)
    //                 files = new HashSet<SharedFile>();
    //             SharedFile newSharedFile = new SharedFile(components[1], Integer.valueOf(components[0]), Boolean.valueOf(components[2]));
    //             files.add(newSharedFile);
    //         }
    //     }
    //     public String toString(HashMap<Integer, Set<SharedFile>> sharedFiles) {
    //         String out = "";
    //         for (Set<SharedFile> files: sharedFiles.values()) {
    //             for (SharedFile sharedFile: files)
    //                 out += sharedFile.toString() + "\n";
    //         }
    //         return out;
    //     }
    // }


    // public void changeFileCount(String fileName, String IV, String change) {
    //     String fileNameHash = Encryption.hashName(fileName, IV);
    //     switch (change) {
    //         case "+1":
    //             if (!fileCounts.containsKey(fileNameHash))
    //                 fileCounts.put(fileNameHash, 0);
    //             fileCounts.put(fileNameHash, fileCounts.get(fileNameHash) + 1);
    //             break;
    //         case "-1":
    //             fileCounts.put(fileNameHash, fileCounts.get(fileNameHash) - 1);
    //             if (fileCounts.get(fileNameHash) <= 0)
    //                 fileCounts.remove(fileNameHash);
    //             break;
    //         case "delete":
    //             fileCounts.remove(fileNameHash);
    //             break;
    //         default:
    //             throw new IllegalArgumentException();
    //     }
    //     regenerateRawText();
    // }
}
