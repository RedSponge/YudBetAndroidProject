package com.redsponge.gltest.utils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.function.Function;

public class Utils {

    public static boolean isEmptyOrNull(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isBlankOrNull(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static float secondsFrom(long timestamp) {
        return (System.nanoTime() - timestamp) / 1000000000f;
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

    public static <A, B> B[] mapArray(A[] arr, Class<B> outputClass, Function<A, B> mapFunction) {
        B[] output = (B[]) Array.newInstance(outputClass, arr.length);
        for (int i = 0; i < arr.length; i++) {
            output[i] = mapFunction.apply(arr[i]);
        }
        return output;
    }

    public static <T> T readReferenceIfExists(DataSnapshot ref, Class<T> refType, T otherwise) {
        return ref.exists() ? ref.getValue(refType) : otherwise;
    }
}
