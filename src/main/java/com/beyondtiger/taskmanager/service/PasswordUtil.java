package com.beyondtiger.taskmanager.service;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {

    private static final int ITERATIONS = 210000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    public static String hash(String password) throws Exception {

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(
            password.toCharArray(), salt, ITERATIONS, KEY_LENGTH
        );

        SecretKeyFactory factory =
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hash = factory.generateSecret(spec).getEncoded();

        return ITERATIONS + ":" +
            Base64.getEncoder().encodeToString(salt) + ":" +
            Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verify(String password, String storedHash)
            throws Exception {

        String[] parts = storedHash.split(":");

        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[2]);

        KeySpec spec = new PBEKeySpec(
            password.toCharArray(), salt, iterations, expectedHash.length * 8
        );

        SecretKeyFactory factory =
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] actualHash = factory.generateSecret(spec).getEncoded();

        if (actualHash.length != expectedHash.length) {
            return false;
        }

        int difference = 0;
        for (int i = 0; i < actualHash.length; i++) {
            difference |= actualHash[i] ^ expectedHash[i];
        }

        return difference == 0;
    }
}
