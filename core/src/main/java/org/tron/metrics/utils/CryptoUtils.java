package org.tron.metrics.utils;


import android.os.Build;
import android.util.Base64;

import org.tron.common.utils.LogUtils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES encryption utility class
 */
public class CryptoUtils {

    // Basic constants
    private static final String ALGORITHM = "AES";
    private static final int KEY_LENGTH = 256;

    // PBKDF2 constants
    private static final int PBKDF2_ITERATIONS = 64;
    private static final int PBKDF2_KEY_LENGTH = 256;
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String SALT_SUFFIX = "123";


    // Format constants
    private static final String SEPARATOR = ":";

    // API level check
    private static final boolean SUPPORTS_STRONG_RANDOM = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;


    public static String generateKeyFromTs(String ts, String seed) {
        if (ts == null || ts.isEmpty()) {
            return null;
        }
        String salt = ts + SALT_SUFFIX;

        return generateKey(seed, salt);
    }

    public static String generateKey(String seed, String salt) {
        if (salt == null || salt.isEmpty()) {
            return null;
        }
        try {
            byte[] saltByte = salt.getBytes(StandardCharsets.UTF_8);
            PBEKeySpec spec = new PBEKeySpec(
                    seed.toCharArray(),
                    saltByte,
                    PBKDF2_ITERATIONS,
                    PBKDF2_KEY_LENGTH
            );

            SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();

            return android.util.Base64.encodeToString(keyBytes, android.util.Base64.NO_WRAP);
        } catch (Exception e) {
            LogUtils.e("Failed to generate key from ts: " + e.getMessage(), e);
            return null;
        }
    }

    public static String encrypt(String data) {
        if (data == null || data.isEmpty()) {
            return "";
        }

        try {
            // Generate random key
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(KEY_LENGTH);
            SecretKey secretKey = keyGenerator.generateKey();
            String key = Base64.encodeToString(secretKey.getEncoded(), Base64.NO_WRAP);

            String encrypted = encryptInternal(data, key, null);
            if (encrypted.isEmpty()) {
                return "";
            }
            // Return format: "key:encryptedData" for easy decryption
            return key + SEPARATOR + encrypted;
        } catch (Exception e) {
            LogUtils.e("Failed to encrypt data: " + e.getMessage(), e);
            return "";
        }
    }

    public static String encrypt(String data, String key) {
        return encryptInternal(data, key, null);
    }


    private static String encryptInternal(String data, String key, Object mode) {
        if (data == null || data.isEmpty() || key == null || key.isEmpty()) {
            return "";
        }

        try {
            byte[] keyBytes = Base64.decode(key, Base64.DEFAULT);

            // Generate random IV for security (CBC mode uses 16 bytes)
            byte[] ivBytes = new byte[16];
            if (SUPPORTS_STRONG_RANDOM) {
                SecureRandom.getInstanceStrong().nextBytes(ivBytes);
            } else {
                new SecureRandom().nextBytes(ivBytes);
            }

            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Combine IV and encrypted data for return
            java.nio.ByteBuffer byteBuffer = java.nio.ByteBuffer.allocate(ivBytes.length + encryptedBytes.length);
            byteBuffer.put(ivBytes);
            byteBuffer.put(encryptedBytes);
            byte[] combinedBytes = byteBuffer.array();

            return Base64.encodeToString(combinedBytes, Base64.NO_WRAP);
        } catch (Exception e) {
            LogUtils.e("Failed to encrypt data: " + e.getMessage(), e);
            return "";
        }
    }

    public static String decrypt(String encryptedDataWithKey) {
        // Decrypt data that was encrypted with encrypt(String data)
        if (encryptedDataWithKey == null || encryptedDataWithKey.isEmpty()) {
            return "";
        }

        String[] parts = encryptedDataWithKey.split(SEPARATOR);
        if (parts.length != 2) {
            return "";
        }

        String key = parts[0];
        String encryptedData = parts[1];

        return decryptInternal(encryptedData, key, null);
    }

    public static String decrypt(String encryptedData, String key) {
        return decryptInternal(encryptedData, key, null);
    }

    public static String decryptWithTsAndSignature(String encryptedData, String ts, String signature) {
        if (encryptedData == null || encryptedData.isEmpty() ||
                ts == null || ts.isEmpty() ||
                signature == null || signature.isEmpty()) {
            LogUtils.e("Invalid parameters for decryption");
            return "";
        }
        try {
            String key = generateKeyFromTs(ts, signature);
            if (key == null || key.isEmpty()) {
                LogUtils.e("Failed to generate key from ts and signature");
                return "";
            }
            return decryptInternal(encryptedData, key, null);
        } catch (Exception e) {
            LogUtils.e("Failed to decrypt with ts and signature: " + e.getMessage(), e);
            return "";
        }
    }

    public static String decrypt(String encryptedData, String password, String salt) {
        if (encryptedData == null || encryptedData.isEmpty() ||
                salt == null || salt.isEmpty() ||
                password == null || password.isEmpty()) {
            LogUtils.e("Invalid parameters for decryption");
            return "";
        }
        try {
            String key = generateKey(password, salt);
            if (key == null || key.isEmpty()) {
                LogUtils.e("Failed to generate key from password and salt");
                return "";
            }
            return decryptInternal(encryptedData, key, null);
        } catch (Exception e) {
            LogUtils.e("Failed to decrypt with password and salt: " + e.getMessage(), e);
            return "";
        }
    }


    private static String decryptInternal(String encryptedData, String key, Object mode) {
        if (encryptedData == null || encryptedData.isEmpty() || key == null || key.isEmpty()) {
            return "";
        }

        try {
            byte[] keyBytes = Base64.decode(key, Base64.DEFAULT);
            byte[] combinedBytes = Base64.decode(encryptedData, Base64.DEFAULT);

            // Extract IV and encrypted data from combined bytes (CBC mode uses 16 bytes IV)
            int ivLength = 16;
            if (combinedBytes.length < ivLength) {
                LogUtils.e("Invalid encrypted data length");
                return "";
            }

            byte[] ivBytes = new byte[ivLength];
            byte[] encryptedBytes = new byte[combinedBytes.length - ivLength];

            System.arraycopy(combinedBytes, 0, ivBytes, 0, ivLength);
            System.arraycopy(combinedBytes, ivLength, encryptedBytes, 0, encryptedBytes.length);

            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            LogUtils.e("Failed to decrypt data: " + e.getMessage(), e);
            return "";
        }
    }

}