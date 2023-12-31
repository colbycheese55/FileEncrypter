package src.backend;
import java.util.*;

public class Vault {
    private User user;
    private UserManager userManager;

    public Vault(UserManager userManager, User user) {
        this.userManager = userManager;
        this.user = user;  
        // TODO fix
        //Sharing.checkEntries(user, userManager);
        String[] invalidFiles = FileHandler.openVault(user);
        if (invalidFiles.length > 0)
            removeInvalidFiles(invalidFiles);
        userManager.updateUserFile(user);
    }
    

    // MENU OPTIONS
    public String listFiles() {
        String list = Arrays.toString(user.fileProfiles.keySet().toArray());
        list = list.replace(", ", "\n").replace("[", "").replace("]", "");
        return list;
    }
    // private void shareWithAnotherUser(String[] entries) {
    //     if (entries.length != 2) {
    //         System.out.println("NO SHARING FLAG GIVEN");
    //         return;
    //     }
    //     Sharing.share(entries[1], user, scan, userManager);
    // }
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

    // HELPER METHOD
    private void removeInvalidFiles(String[] list) {
        for (String upNext: list)
            user.fileProfiles.remove(upNext);
        userManager.updateUserFile(user);
        System.out.println("MISSING FILES: the following files are missing from the encrypted vault and have been removed from the user's availability list");
        System.out.println(Arrays.toString(list));
    }
}
