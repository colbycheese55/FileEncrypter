package src.backend;
import java.util.*;

public class Vault {
    private User user;
    private UserManager userManager;
    private String[] missingFiles;

    public Vault(UserManager userManager, User user) {
        this.userManager = userManager;
        this.user = user;  
        this.missingFiles = FileHandler.openVault(user);
        userManager.updateUserFile(user);
    }
    

    // MENU OPTIONS
    public String listFiles() {
        String list = Arrays.toString(user.fileProfiles.keySet().toArray());
        list = list.replace(", ", "\n").replace("[", "").replace("]", "");
        return list;
    }
    public void saveFiles(boolean addNewFiles) {
        ProgressBar progressBar = new ProgressBar("Saving Files: ", ProgressBar.DEFAULT_LENGTH);
        String[] fileNames = FileHandler.getFilenamesAtPath(FileHandler.DECRYPTED_VAULT_EXT);
        
        for (int i = 0; i < fileNames.length; i++) {
            String nextFileName = fileNames[i];
            if (addNewFiles && !user.fileProfiles.containsKey(nextFileName)) {
                String key = Encryption.generateSecureToken();
                String IV = Encryption.generateSecureToken();
                FileProfile newFileProfile = new FileProfile(nextFileName, key, IV);
                user.fileProfiles.put(nextFileName, newFileProfile);
                userManager.fileCounts.changeFileCount(nextFileName, newFileProfile.getIV(), "+1");
            }
            else if (FileHandler.hasFileBeenModified(nextFileName) == false)
                continue;
            if (addNewFiles || user.fileProfiles.containsKey(nextFileName)) {
                FileProfile fileProfile = user.fileProfiles.get(nextFileName);
                FileHandler.encryptFile(fileProfile.getName(), fileProfile.getKey(), fileProfile.getIV());
            }
            progressBar.update((double) i / fileNames.length);
        }
        userManager.updateUserFile(user);
        progressBar.complete();
    }
    public void removeFiles(boolean deleteFromAllUsers) {
        ArrayList<String> toRemove = new ArrayList<String>();
        for (FileProfile upNext: user.fileProfiles.values()) // adding names from the user's fileprofiles
            toRemove.add(upNext.getName());
        for (String upNext: FileHandler.getFilenamesAtPath(FileHandler.DECRYPTED_VAULT_EXT)) // removing names from the list from the open vault
            toRemove.remove(upNext);
        for (String upNext: toRemove) { // removing fileprofiles from the user
            String IV = user.fileProfiles.get(upNext).getIV();
            if (deleteFromAllUsers) {
                userManager.fileCounts.changeFileCount(upNext, IV, "delete");;
            }
            else 
                userManager.fileCounts.changeFileCount(upNext, IV, "-1");
            user.fileProfiles.remove(upNext);
        }
        userManager.updateUserFile(user);
    }
    public String getVaultInfo() {
        return user.getName() + " is signed in, with " + user.fileProfiles.size() + " file(s) available";
    }
    public String trySharedFiles(String password) {
        ArrayList<String> sharedFiles = userManager.shareLog.getEntriesForUser(user.getName());
        String out = "";
        for (int i = 0; i < sharedFiles.size(); i++) {
            String decFileProfile = Encryption.decrypt(sharedFiles.get(i), password);
            if (decFileProfile != null) {
                FileProfile fileProfile = new FileProfile(decFileProfile);
                user.fileProfiles.put(fileProfile.getName(), fileProfile);
                FileHandler.decryptFile(fileProfile.getName(), fileProfile.getKey(), fileProfile.getIV());
                sharedFiles.remove(i);
                i--;
                out += fileProfile.getName() + "\n";
        }}
        userManager.shareLog.removeOldEntries(sharedFiles, user.getName());
        userManager.updateUserFile(user);
        return out;
    }
    public int numSharedFiles() {
        ArrayList<String> sharedFiles = userManager.shareLog.getEntriesForUser(user.getName());
        return sharedFiles.size();
    }

    public void shareNow(User otherUser, String[] filesToShare) {
        for (String fileName: filesToShare) {
            FileProfile fileProfile = user.fileProfiles.get(fileName);
            otherUser.fileProfiles.put(fileName, fileProfile);
            userManager.fileCounts.changeFileCount(fileName, fileProfile.getIV(), "+1");;
        }
        userManager.updateUserFile(otherUser);
    }
    
    public void shareEncrypted(String receivingUserName, String[] filesToShare, String password) {
        for (String fileName: filesToShare) {
            FileProfile fileProfile = user.fileProfiles.get(fileName);
            userManager.shareLog.addEntry(fileProfile, receivingUserName, password);
            userManager.fileCounts.changeFileCount(fileName, fileProfile.getIV(), "+1");
        }
        userManager.updateUserFile(user);
    }


    public User getUser() {return user;}
    public UserManager getUserManager() {return userManager;}
    public String getMissingFiles() {
        String out = "";
        for (String file: this.missingFiles)
            out += file + "\n";
        if (out.equals(""))
            return null;
        return out;
    }
}
