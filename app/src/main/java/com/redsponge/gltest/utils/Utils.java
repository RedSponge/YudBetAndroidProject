package com.redsponge.gltest.utils;

public class Utils {

    public static boolean isEmptyOrNull(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isBlankOrNull(String s) {
        return s == null || s.trim().isEmpty();
    }

    private Utils() {}

}
