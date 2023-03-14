package com.lawmobile.domain.utils;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHelper {
    private static final int INITIAL_POSITION_IN_BYTES = 13;


    @SuppressWarnings("NewApi")
    public static String hashPassword(String password, int saltSize, int iterations, int dkLen) throws NoSuchAlgorithmException, InvalidKeySpecException {

        // Generate a random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[saltSize];
        random.nextBytes(salt);

        // Hash the password
        byte[] subKey = hashPBKDF2WithHMACSHA256(password, salt, iterations, dkLen);

        // To base64 result
        byte[] outputBytes = new byte[INITIAL_POSITION_IN_BYTES + salt.length + subKey.length];
        writeNetworkByteOrder(outputBytes, 1, dkLen);
        writeNetworkByteOrder(outputBytes, 5, iterations);
        writeNetworkByteOrder(outputBytes, 9, saltSize);
        System.arraycopy(salt, 0, outputBytes, INITIAL_POSITION_IN_BYTES, salt.length);
        System.arraycopy(subKey, 0, outputBytes, INITIAL_POSITION_IN_BYTES + saltSize, subKey.length);
        return Base64.getEncoder().encodeToString(outputBytes);
    }

    /**
     * Validate password
     *
     * @param password       the password
     * @param hashedPassword the password
     * @return true if the password is valid
     */
    public static boolean validatePassword(String password, String hashedPassword) {
        try {
            // Hashed password to bytes
            @SuppressWarnings("NewApi") byte[] hashedPasswordBytes = Base64.getDecoder().decode(hashedPassword.getBytes(StandardCharsets.UTF_8));

            // Hash information
            int dkLen = readNetworkByteOrder(hashedPasswordBytes, 1);
            int iterations = readNetworkByteOrder(hashedPasswordBytes, 5);
            int saltSize = readNetworkByteOrder(hashedPasswordBytes, 9);

            // Salt
            byte[] salt = new byte[saltSize];
            System.arraycopy(hashedPasswordBytes, INITIAL_POSITION_IN_BYTES, salt, 0, saltSize);

            // Expected SubKey
            byte[] expectedSubKey = new byte[dkLen];
            System.arraycopy(hashedPasswordBytes, INITIAL_POSITION_IN_BYTES + saltSize, expectedSubKey, 0, dkLen);

            // Actual SubKey
            byte[] actualSubKey = hashPBKDF2WithHMACSHA256(password, salt, iterations, dkLen);

            // Array comparison
            return Arrays.equals(expectedSubKey, actualSubKey);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return false;
        }
    }

    /**
     * hash PBKDF2WithHmacSHA256
     *
     * @param password   the password
     * @param salt       the salt
     * @param iterations the function iterations
     * @param dkLen      the desired length of the derived key (bytes)
     * @return The derived key in Base64
     * @throws NoSuchAlgorithmException No Such Algorithm
     * @throws InvalidKeySpecException  Invalid Key Spec
     */
    public static byte[] hashPBKDF2WithHMACSHA256(String password, byte[] salt, int iterations, int dkLen) throws NoSuchAlgorithmException, InvalidKeySpecException {
        char[] chars = password.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, dkLen * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return skf.generateSecret(spec).getEncoded();
    }

    private static void writeNetworkByteOrder(byte[] buffer, int offset, int value) {
        buffer[offset + 0] = (byte) (value >> 24);
        buffer[offset + 1] = (byte) (value >> 16);
        buffer[offset + 2] = (byte) (value >> 8);
        buffer[offset + 3] = (byte) (value >> 0);
    }

    private static int readNetworkByteOrder(byte[] buffer, int offset) {
        return ((int) (buffer[offset + 0]) << 24) | (Byte.toUnsignedInt((buffer[offset + 1])) << 16) | (Byte.toUnsignedInt((buffer[offset + 2])) << 8) | (Byte.toUnsignedInt((buffer[offset + 3])));
    }


}
