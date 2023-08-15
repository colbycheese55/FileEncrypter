package src;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.spec.*;
import java.util.*;

public class Encryption {
    public static final String DEFAULT_INITIALIZATION_VECTOR = "bOcaFVdcPGgDw3A82k/4bw==";

    public static String encrypt(String message, String password, String initializationVector) {
        try {
            IvParameterSpec IV = new IvParameterSpec(Base64.getDecoder().decode(initializationVector));
            SecretKeySpec KEY = getKeyFromPassword(password);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, KEY, IV);
            byte[] encrypted = cipher.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String decrypt(String code, String password, String initializationVector) {
        try {
            IvParameterSpec IV = new IvParameterSpec(Base64.getDecoder().decode(initializationVector));
            SecretKeySpec KEY = getKeyFromPassword(password);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, KEY, IV);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(code));
            return new String(decrypted);
        } 
    catch (BadPaddingException bpe) {/*bpe.printStackTrace();*/}
        catch (Exception e) {e.printStackTrace();}
        return null;
    }

    public static String encrypt(String message, String password) {return encrypt(message, password, DEFAULT_INITIALIZATION_VECTOR);}
    public static String decrypt(String code, String password) {return decrypt(code, password, DEFAULT_INITIALIZATION_VECTOR);}

    private static SecretKeySpec getKeyFromPassword(String password) {
        try {
            String salt = "salt";
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 128); // AES-128
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            SecretKey secretKey= factory.generateSecret(spec);
            return new SecretKeySpec(secretKey.getEncoded(), "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String generateNewIV() {
        Random random = new Random();
        byte[] arr = new byte[16];
        random.nextBytes(arr);
        return Base64.getEncoder().encodeToString(arr);
    }
    public static String hashName(String fileName, String IV) {
        String hash = encrypt(fileName, fileName, IV);
        hash = hash.replace("/", ".");
        return "file_" + hash + "_.txt";
    }
}