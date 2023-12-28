package src;

import java.util.*;

public class Sharing {
    public static void checkEntries(User user, Scanner scan, UserManager userManager) {
        ArrayList<String> sharedFiles = userManager.shareLog.getEntriesForUser(user.getName());
        if (sharedFiles.size() == 0)
            return;
        int count = tryEntries(user, sharedFiles, user.getName());
        if (count > 0)
            System.out.println("Unlocked " + count + " shared file(s), " + sharedFiles.size() + " remain locked\n");
        userManager.shareLog.removeOldEntries(sharedFiles, user.getName());
        if (sharedFiles.size() == 0) {
            userManager.shareLog.removeOldEntries(sharedFiles, user.getName());
            return;
        }
        System.out.println("Enter any One Time Passwords (OTPs) to unlock up to " + sharedFiles.size() + " shared files(s). Enter \"EXIT\" to stop");
        String in = scan.nextLine();
        while (sharedFiles.size() != 0 && !in.replace(" ", "").equals("EXIT")) {
            count = tryEntries(user, sharedFiles, in);
            System.out.println(count + " file(s) were unlocked by that OTP, " + sharedFiles.size() + " file(s) remain");
            if (sharedFiles.size() > 0) {
                System.out.println("Enter another OTP (or \"EXIT\")");
                in = scan.nextLine();
        }}
        System.out.print("\n");
        userManager.shareLog.removeOldEntries(sharedFiles, user.getName());
    }

    private static int tryEntries(User user, ArrayList<String> sharedFiles, String password) {
        int startingSize = sharedFiles.size();
        for (int i = 0; i < sharedFiles.size(); i++) {
            String decFileProfile = Encryption.decrypt(sharedFiles.get(i), password);
            if (decFileProfile != null) {
                FileProfile fileProfile = new FileProfile(decFileProfile);
                user.fileProfiles.put(fileProfile.getName(), fileProfile);
                sharedFiles.remove(i);
                i--;
                System.out.println("Unlocked file: " + fileProfile.getName());
        }}
        return startingSize - sharedFiles.size();
    }

    public static void share(String flag, User user, Scanner scan, UserManager userManager) {
        switch (flag) {
            case "n":
                shareNow(user, scan, userManager);
                break;
            case "u":
                shareEncrypted(user, scan, userManager, false);
                break;
            case "o":
                shareEncrypted(user, scan, userManager, true);
                break;
            default:
                System.out.println("UNKNOWN SHARING FLAG USED");
        }
        userManager.updateUserFile(user);
    }

    private static void shareNow(User user, Scanner scan, UserManager userManager) {
        System.out.println("Delete any items from your open vault you do NOT wish to share, THEN authenticate the user you are sharing the files with");
        User otherUser = User.authenticate(scan, userManager);
        String[] filesToShare = FileHandler.getFilenamesAtPath(FileHandler.DECRYPTED_VAULT_EXT);
        for (String fileName: filesToShare) {
            FileProfile fileProfile = user.fileProfiles.get(fileName);
            otherUser.fileProfiles.put(fileName, fileProfile);
            userManager.fileCounts.changeFileCount(fileName, fileProfile.getIV(), "+1");;
        }
        FileHandler.closeVault();
        FileHandler.openVault(user);
        userManager.updateUserFile(otherUser);
        System.out.println("Successfully shared " + filesToShare.length + " file(s) with " + otherUser.getName() + ". " + user.getName() + " is still logged in\n");
    }
    
    private static void shareEncrypted(User user, Scanner scan, UserManager userManager, boolean OTPstatus) {
        System.out.println("Delete any items from your open vault you do NOT wish to share, THEN enter the user you are sharing the files with");
        String receivingUserName = getUsername(scan, userManager);
        String[] filesToShare = FileHandler.getFilenamesAtPath(FileHandler.DECRYPTED_VAULT_EXT);
        String password = receivingUserName;
        if (OTPstatus == true) {
            System.out.println("Enter a One Time Password (OTP) for these shared file(s)");
            password = scan.nextLine();
        }
        for (String fileName: filesToShare) {
            FileProfile fileProfile = user.fileProfiles.get(fileName);
            userManager.shareLog.addEntry(fileProfile, receivingUserName, password);
            userManager.fileCounts.changeFileCount(fileName, fileProfile.getIV(), "+1");
        }
        FileHandler.openVault(user);
        System.out.println("Successfully shared " + filesToShare.length + " file(s) with " + receivingUserName + ". " + user.getName() + " is still logged in\n");
    }

    
    private static String getUsername(Scanner scan, UserManager userManager) {
        System.out.print("Enter the receiving user's username: ");
        String entry = scan.nextLine();
        if (entry.equals("EXIT"))
            System.exit(0);
        if (userManager.hasUsername(entry))
            return entry;
        System.out.println("No such user exists. Try again!");
        return getUsername(scan, userManager);
    }
}
