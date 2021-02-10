package com.redsponge.gltest.utils;

public class MathUtils {

    public static float lerp(float current, float target, float alpha) {
        return (1 - alpha) * current + alpha * target;
    }

}
