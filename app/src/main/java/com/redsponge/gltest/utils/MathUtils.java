package com.redsponge.gltest.utils;

import java.util.Random;

public class MathUtils {

    private static final Random rnd = new Random();

    public static float lerp(float current, float target, float alpha) {
        return (1 - alpha) * current + alpha * target;
    }

    public static int random(int min, int max) {
        return rnd.nextInt(max - min + 1)  + min;
    }
}
