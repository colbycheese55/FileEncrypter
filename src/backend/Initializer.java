package src.backend;
import java.io.*;

public class Initializer {
    public static void main(String path) { // must create: user file, encrypted vault subdirectory, decrypted vault subdirectory
        boolean itemsCreated = false;
        FileHandler.PROGRAM_PATH = path;
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
            String defaultIV = Encryption.generateSecureToken();
            FileHandler.writeUserFile(defaultIV + "&&");
            System.out.println(userFile.getName() + " made");
            itemsCreated = true;
        }

        if (itemsCreated) {
            System.out.println("Initialization complete!");
            System.out.println("____________________________________________________________\n");
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {FileHandler.closeVault();}});
}}
