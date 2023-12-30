package src.backend;
import java.util.*;

public class Vault {
    private Scanner scan;
    private User user;
    private UserManager userManager;

    public Vault(Scanner scan, UserManager userManager, User user) {
        this.scan = scan;
        this.userManager = userManager;
        this.user = user;  
        Sharing.checkEntries(user, scan, userManager);
        String[] invalidFiles = FileHandler.openVault(user);
        if (invalidFiles.length > 0)
            removeInvalidFiles(invalidFiles);
        userManager.updateUserFile(user);
    }

    public void mainMenu() {
        printInstructions();
        while (true) {
            String[] entryComponents = scan.nextLine().replace(" ", "").split("-", 2);
            if (entryComponents.length == 2)
                handleFileFlags(entryComponents[1]);
            switch (entryComponents[0]) {
                case "list": // list available files
                    listFiles();
                    break;
                case "share": // share files with another user
                    shareWithAnotherUser(entryComponents);
                    break;
                case "info": // print info about the user
                    System.out.println(getVaultInfo());
                    break;
                case "print": // re-print instructions
                    printInstructions();
                    break;
                case "change": // change user credentials
                    user.changeCredentials(scan, userManager);
                    break;
                case "re": // register
                    break;
                case "log": // logout
                    FileHandler.closeVault();
                    return;
                case "quit": // quit the program
                    System.exit(0);
                default:
                    System.out.println("UNKNOWN ENTRY. TRY AGAIN. ENTER \"print\" TO SEE INSTRUCTIONS OR \"quit\" TO EXIT THE PROGRAM.");
            }}}
    private void handleFileFlags(String flags) {
        for (char upNext: flags.toCharArray()) {
            switch (upNext) {
                case 'a':
                    saveFiles(true);
                    break;
                case 's':
                    saveFiles(false);
                    break;
                case 'r':
                    removeFiles(false);
                    break;
                case 'd':
                    removeFiles(true);
                    break;
    }}}
    

    // MENU OPTIONS
    private void listFiles() {
        System.out.println("Available File(s): (" + user.fileProfiles.size() + ")");
        System.out.println(Arrays.toString(user.fileProfiles.keySet().toArray()) + "\n");
    }
    private void shareWithAnotherUser(String[] entries) {
        if (entries.length != 2) {
            System.out.println("NO SHARING FLAG GIVEN");
            return;
        }
        Sharing.share(entries[1], user, scan, userManager);
    }
    private void saveFiles(boolean addNewFiles) {
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
    private void removeFiles(boolean deleteFromAllUsers) {
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
    private String getVaultInfo() {
        return user.getName() + " is signed in, with " + user.fileProfiles.size() + " file(s) available";
    }
    private void printInstructions() {
        System.out.println("VAULT MENU: What do you wish to do? (list) list available files, (share) share a file, (info) show user info, (re) register, (log) logout, (quit) quit, (print) re-print instructions, (change) change user credentials");
        System.out.println("Quit, logout, and refresh flags: (s) save preexisting files, (a) save and add new files, (r) remove files for user, (d) delete files for all users\n");
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
