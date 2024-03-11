package com.example.helloworld;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    //functions include: encrypt(String, String) -> byte[], decrypt(byte[], String) -> String,
    // getSalt() -> byte[], getHash(String password, byte[] saltBytesFromFile) -> String

    private static SecretKeySpec secretKey;
    private static byte[] key;

    //this generates an IV (Concatenate a generated one instead to the beginning of the encrypted file)
    public static IvParameterSpec generateIV() {
        int ivSize = 16;
        byte[] iv = new byte[ivSize];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        return ivParameterSpec;
    }


    public static EncryptedJpgInfo encrypt(byte[] toencrrypt, String key) throws Exception {

        // Generating IV - to be concatenated
        int ivSize = 16; //unsure if it needs to be 16 or 32, but it works
        byte[] iv = new byte[ivSize];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // This hashes the key to make the encryption even safer
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(key.getBytes("UTF-8"));
        byte[] keyBytes = new byte[16];
        System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        // Encrypt.
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(toencrrypt);

        return new EncryptedJpgInfo(encrypted, iv);
    }


    public static byte[] decrypt(byte[] encryptedBytes, byte[] ivBytes, String key) throws Exception {
        int keySize = 16;

        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);

        // Hash key again
        byte[] keyBytes = new byte[keySize];
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(key.getBytes());
        System.arraycopy(md.digest(), 0, keyBytes, 0, keyBytes.length);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        // Decrypts the
        Cipher cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decrypted = cipherDecrypt.doFinal(encryptedBytes);

        return decrypted;
    }


    //Holds data for IV and encrypted bytes from image and stores it in a .encrypted file
    public static class EncryptedJpgInfo {
        public byte[] ecryptedBytes;
        public byte[] ivBytes;

        public EncryptedJpgInfo(byte[] ecryptedBytes, byte[] ivBytes) {
            this.ecryptedBytes = ecryptedBytes;
            this.ivBytes = ivBytes;
        }
    }


    static byte[] getSalt() throws NoSuchAlgorithmException {
        //Secure random is more secure... I think?
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        //Create array for saltBytesFromFile
        byte[] salt = new byte[16];
        //Get a random saltBytesFromFile
        sr.nextBytes(salt);
        //return saltBytesFromFile
        return salt;
    }


    public static byte[] hashPassword(final char[] password, final byte[] salt, final int iterations, final int keyLength) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKey key = skf.generateSecret(spec);
            byte[] res = key.getEncoded();
            return res;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws Exception { //test main for AES - works
        AES aes = new AES();
        String password = "open";
        byte[] salt = aes.getSalt();
        int iterations = 1000;
        int keyLength = 512;
        byte[] hashedBytes = aes.hashPassword(password.toCharArray(), salt, iterations, keyLength);
        String hashed = javax.xml.bind.DatatypeConverter.printBase64Binary(hashedBytes);

    }
}