package src.backend;
import java.io.*;

public class Initializer {
    public static String main(String path) { // must create: user file, encrypted vault subdirectory, decrypted vault subdirectory
        String text = "";
        FileHandler.PROGRAM_PATH = path;
        File encryptedVaultDir = new File(FileHandler.PROGRAM_PATH + FileHandler.ENCRYPTED_VAULT_EXT);
        if (!encryptedVaultDir.exists()) {
            encryptedVaultDir.mkdir();
            text += "Subdirectory made: " + encryptedVaultDir.getName() + "\n";
        }
        File decryptedVaultDir = new File(FileHandler.PROGRAM_PATH + FileHandler.DECRYPTED_VAULT_EXT);
        if (!decryptedVaultDir.exists()) {
            decryptedVaultDir.mkdir();
            text += "Subdirectory made: " + decryptedVaultDir.getName() + "\n";
        }
        File userFile = new File(FileHandler.PROGRAM_PATH + FileHandler.USER_FILE_EXT);
        if (!userFile.exists()) {
            String defaultIV = Encryption.generateSecureToken();
            FileHandler.writeUserFile(defaultIV + "&&");
            text += userFile.getName() + " made \n";
        }

        if (text.equals(""))
            text = null;
        else
            text += "Initialization complete!";

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {FileHandler.closeVault();}});

        return text;
}}
