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

    public static float clamp(float min, float val, float max) {
        return Math.min(Math.max(min, val), max);
    }

    /**
     * Interpolation formulas from https://easings.net/
     */


    public static float easeOutInterpolation(float alpha) {
        final float c1 = 1.70158f;
        final float c3 = c1 + 1;

        return (float) (1 + c3 * Math.pow(alpha - 1, 3) + c1 * Math.pow(alpha - 1, 2));
    }

    public static float easeInInterpolation(float alpha) {
        final float c1 = 1.70158f;
        final float c3 = c1 + 1;

        return c3 * alpha * alpha * alpha - c1 * alpha * alpha;
    }
}
