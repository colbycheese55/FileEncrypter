package src;
import java.io.*;
import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

public class FileHandler {
    public static String PROGRAM_PATH = "";
    public static final String ENCRYPTED_VAULT_EXT = "encrypted-storage\\";
    public static final String DECRYPTED_VAULT_EXT = "unlocked-storage\\";
    public static final String USER_FILE_EXT = "USER_FILE.txt";
    public static final int BUFFER_SIZE = 1024;

    // FILE IN / OUT (with encryption as needed)
    public static void encryptFile(String fileName, String password, String IV) {
        File encryptedFile = new File(PROGRAM_PATH + ENCRYPTED_VAULT_EXT + Encryption.hashName(fileName, IV));
        File decryptedFile = new File(PROGRAM_PATH + DECRYPTED_VAULT_EXT + fileName);
        Cipher cipher = Encryption.getCipher(Cipher.ENCRYPT_MODE, password, IV);

        try (
            FileInputStream inStream = new FileInputStream(decryptedFile); 
            FileOutputStream fos = new FileOutputStream(encryptedFile, false);
            CipherOutputStream outStream = new CipherOutputStream(fos, cipher)) {
            byte[] buffer = new byte[BUFFER_SIZE];

            while (true) {
                int bytesRead = inStream.read(buffer);
                if (bytesRead == -1) break;
                if (bytesRead != BUFFER_SIZE) buffer = Arrays.copyOf(buffer, bytesRead);
                outStream.write(buffer);
                buffer = new byte[BUFFER_SIZE];
            }

            inStream.close();
            outStream.close();

        } catch(IOException e) {e.printStackTrace();}
    }
    public static boolean decryptFile(String fileName, String password, String IV) {
        File encryptedFile = new File(PROGRAM_PATH + ENCRYPTED_VAULT_EXT + Encryption.hashName(fileName, IV));
        if (!encryptedFile.exists())
            return false;
        File decryptedFile = new File(PROGRAM_PATH + DECRYPTED_VAULT_EXT + fileName);
        Cipher cipher = Encryption.getCipher(Cipher.DECRYPT_MODE, password, IV);

        try (
            FileInputStream inStream = new FileInputStream(encryptedFile); 
            FileOutputStream fos = new FileOutputStream(decryptedFile, false);
            CipherOutputStream outStream = new CipherOutputStream(fos, cipher)) {
            byte[] buffer = new byte[BUFFER_SIZE];

            while (true) {
                int bytesRead = inStream.read(buffer);
                if (bytesRead == -1) break;
                if (bytesRead != BUFFER_SIZE) buffer = Arrays.copyOf(buffer, bytesRead);
                outStream.write(buffer);
                buffer = new byte[BUFFER_SIZE];
            }

            inStream.close();
            outStream.close();

        } catch(IOException e) {e.printStackTrace();}
        return true;
    }
    public static String readUserFile() {
        try {
            File file = new File(PROGRAM_PATH + USER_FILE_EXT);
            Scanner reader = new Scanner(file);
            String output = "";
            if (reader.hasNextLine()) // first line
                output = reader.nextLine();
            while (reader.hasNextLine()) // subsequent lines
                output += "\n" + reader.nextLine();
            reader.close();
            return output;
        } catch (FileNotFoundException e) {System.out.println("File not found!");}
        return null;
    }
    public static void writeUserFile(String in) {
        try {
            File file = new File(PROGRAM_PATH + USER_FILE_EXT);
            FileWriter writer = new FileWriter(file);
            writer.write(in);
            writer.close();
        }
        catch (IOException e) {e.printStackTrace();}
    }


    // HELPER METHODS
    public static String[] openVault(User user) {
        ProgressBar progressBar = new ProgressBar("Opening the Vault: ", ProgressBar.DEFAULT_LENGTH);
        ArrayList<String> missingFileNames = new ArrayList<String>();
        ArrayList<FileProfile> fileList = new ArrayList<FileProfile>(user.fileProfiles.values());
        
        for (int i = 0; i < fileList.size(); i++) {
            FileProfile fileProfile = fileList.get(i);
            boolean success = decryptFile(fileProfile.getName(), fileProfile.getKey(), fileProfile.getIV());
            if (success == false)
                missingFileNames.add(fileProfile.getName());
            progressBar.update((double) i / fileList.size());
        }
        progressBar.complete();
        return missingFileNames.toArray(new String[0]);
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


