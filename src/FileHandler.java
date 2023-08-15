package src;
import java.io.*;
import java.util.*;

public class FileHandler {
    public static String PROGRAM_PATH = "";
    public static String ENCRYPTED_VAULT_EXT = "encrypted-storage\\";
    public static String DECRYPTED_VAULT_EXT = "unlocked-storage\\";

    public static String read(String relativePath) {
        try {
            File file = new File(PROGRAM_PATH + relativePath);
            Scanner reader = new Scanner(file);
            String output = "";
            if (reader.hasNextLine()) // first line
                output = reader.nextLine();
            while (reader.hasNextLine()) // subsequent lines
                output += "\n" + reader.nextLine();
            reader.close();
            return output;
        }
    catch (FileNotFoundException e) {/*System.out.println("File not found!");*/}
    return null;
    }
    public static void write(String relativePath, String contents) {
        try {
            File file = new File(PROGRAM_PATH + relativePath);
            FileWriter writer = new FileWriter(file);
            writer.write(contents);
            writer.close();
        }
        catch (IOException e) {e.printStackTrace();}
    }
    public static String[] openVault(User user) {
        ArrayList<String> invalidFileNames = new ArrayList<String>();

        for (Map.Entry<String, FileProfile> upNext: user.fileProfiles.entrySet()) {
            String decryptedFileName = upNext.getValue().getName();
            String fileIV = upNext.getValue().getIV();
            String encryptedFileName = Encryption.hashName(decryptedFileName, fileIV);
            String encryptedFileContents = read(ENCRYPTED_VAULT_EXT + encryptedFileName);
            if (encryptedFileContents == null) {
                invalidFileNames.add(decryptedFileName);
                continue;
            }
            String decryptedFileContents = Encryption.decrypt(encryptedFileContents, upNext.getValue().getKey(), fileIV);
            write(DECRYPTED_VAULT_EXT + decryptedFileName, decryptedFileContents);
        }
        return invalidFileNames.toArray(new String[0]);
    }
    public static void closeVault() {
        for (String upNext: getFilenamesAtPath(DECRYPTED_VAULT_EXT))
            delete(DECRYPTED_VAULT_EXT + upNext);
    } 
    public static String[] getFilenamesAtPath(String relativePath) {
        File directory = new File(PROGRAM_PATH + relativePath);
        return directory.list();
    }
    public static void delete(String relativePath) {
        File file = new File(PROGRAM_PATH + relativePath);
        file.delete();
    }
}


