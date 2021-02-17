package com.redsponge.gltest.auth;

import java.util.regex.Pattern;

public class AuthUtils {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("[a-zA-Z_-][a-zA-Z0-9_]*");
    private static final int MIN_NAME_LENGTH = 3;

    public static boolean isValidName(String name) {
        return name.length() >= MIN_NAME_LENGTH && USERNAME_PATTERN.matcher(name).matches();
    }

    public static boolean isValidPassword(String password) {
        return true;
    }

}
