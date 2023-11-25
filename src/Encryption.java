package src;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.GeneralSecurityException;
import java.security.spec.*;
import java.util.*;

public class Encryption {
    private static String DEFAULT_INITIALIZATION_VECTOR = "bOcaFVdcPGgDw3A82k/4bw==";
    public static String getDefaultIV() {return DEFAULT_INITIALIZATION_VECTOR;}
    public static void setDefaultIV(String IV) {DEFAULT_INITIALIZATION_VECTOR = IV;}

    // ENCRYPTION
    public static byte[] encrypt(byte[] message, String password, String initializationVector) {
        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, password, initializationVector);
            byte[] encrypted = cipher.doFinal(message);
            return encrypted;
        } 
        catch (Exception e) {e.printStackTrace();}
        return null;
    }
    public static String encrypt(String message, String password, String initializationVector) {
        byte[] out = encrypt(message.getBytes(), password, initializationVector);
        if (out == null) return null;
        return Base64.getEncoder().encodeToString(out);
    }
    public static String encrypt(String message, String password) {return encrypt(message, password, DEFAULT_INITIALIZATION_VECTOR);}

    // DECRYPTION
    public static byte[] decrypt(byte[] code, String password, String initializationVector) {
        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE, password, initializationVector);
            byte[] decrypted = cipher.doFinal(code);
            return decrypted;
        } 
        catch (BadPaddingException bpe) {/*bpe.printStackTrace();*/}
        catch (Exception e) {e.printStackTrace();}
        return null;
    }
    public static String decrypt(String code, String password, String initializationVector) {
        byte[] out = decrypt(Base64.getDecoder().decode(code), password, initializationVector);
        if (out == null) return null;
        return new String(out);
    }
    public static String decrypt(String code, String password) {return decrypt(code, password, DEFAULT_INITIALIZATION_VECTOR);}

    // OTHER METHODS
    public static Cipher getCipher(int mode, String password, String initializationVector) {
        try {
            IvParameterSpec IV = new IvParameterSpec(Base64.getDecoder().decode(initializationVector));
            SecretKeySpec KEY = getKeyFromPassword(password);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(mode, KEY, IV);
            return cipher;
        } 
        catch (GeneralSecurityException e) {e.printStackTrace();}
        return null;
    }
    private static SecretKeySpec getKeyFromPassword(String password) {
        try {
            String salt = "salt";
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 128); // AES-128
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            SecretKey secretKey= factory.generateSecret(spec);
            return new SecretKeySpec(secretKey.getEncoded(), "AES");
        } 
        catch (Exception e) {e.printStackTrace();}
        return null;
    }
    public static String generateSecureToken() {
        Random random = new Random();
        byte[] arr = new byte[16];
        random.nextBytes(arr);
        return Base64.getEncoder().encodeToString(arr);
    }
    public static String hashName(String fileName, String IV) {
        String hash = encrypt(fileName, fileName, IV);
        hash = hash.replace("/", ".");
        return "file_" + hash + "_.bin";
    }    
}