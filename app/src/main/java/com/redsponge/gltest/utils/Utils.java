package com.redsponge.gltest.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Utils {

    public static boolean isEmptyOrNull(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isBlankOrNull(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static int tryParseInt(String num, int def) {
        try {
            return Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
            byte[] afterSha = shaDigest.digest(password.getBytes(StandardCharsets.UTF_8));
            String b64String = Base64.getEncoder().encodeToString(afterSha);

            return b64String;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Utils() {}

}
