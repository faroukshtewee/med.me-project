package com.example.medmeproject.Dto;

import java.security.SecureRandom;

public class AuthUtils {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int CODE_LENGTH = 6;

    public static String generateAuthCode() {
        // Generates a 6-digit number (000000 to 999999)
        int min = (int) Math.pow(10, CODE_LENGTH - 1); // 100000
        int max = (int) Math.pow(10, CODE_LENGTH) - 1; // 999999
        return String.valueOf(RANDOM.nextInt(max - min + 1) + min);
    }
}
