package src;
import java.io.*;

public class Initializaer {
    private static final boolean TESTING_MODE = true;

    public static void main() { // must create: user file, encrypted vault subdirectory, decrypted vault subdirectory
        boolean itemsCreated = false;
        FileHandler.PROGRAM_PATH = System.getProperty("user.dir") + "\\";
        if (TESTING_MODE)
            FileHandler.PROGRAM_PATH += "testing\\";
        File encryptedVaultDir = new File(FileHandler.PROGRAM_PATH + FileHandler.ENCRYPTED_VAULT_EXT);
        if (!encryptedVaultDir.exists()) {
            encryptedVaultDir.mkdir();
            System.out.println("Subdirectory made: " + encryptedVaultDir.getName());
            itemsCreated = true;
        }
        File decryptedVaultDir = new File(FileHandler.PROGRAM_PATH + FileHandler.DECRYPTED_VAULT_EXT);
        if (!decryptedVaultDir.exists()) {
            decryptedVaultDir.mkdir();
            System.out.println("Subdirectory made: " + decryptedVaultDir.getName());
            itemsCreated = true;
        }
        File userFile = new File(FileHandler.PROGRAM_PATH + FileHandler.USER_FILE_EXT);
        if (!userFile.exists()) {
            FileHandler.writeUserFile("");
            System.out.println(userFile.getName() + " made");
            itemsCreated = true;
        }

        if (itemsCreated) {
            System.out.println("Initialization complete!");
            System.out.println("____________________________________________________________\n");
}}}
