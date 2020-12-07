package com.redsponge.gltest.gl.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public final class BufferUtils {

    public static final int FLOAT_SIZE = 4;
    public static final int INT_SIZE = 4;

    public static FloatBuffer allocateFloatBuffer(int length) {
        ByteBuffer bb = ByteBuffer.allocateDirect(length * FLOAT_SIZE);
        bb.order(ByteOrder.nativeOrder());
        return bb.asFloatBuffer();
    }

    public static FloatBuffer allocateFloatBuffer(float[] buffer) {
        FloatBuffer fb = allocateFloatBuffer(buffer.length);
        fb.put(buffer);
        fb.position(0);
        return fb;
    }

    public static IntBuffer allocateIntBuffer(int length) {
        ByteBuffer bb = ByteBuffer.allocateDirect(length * INT_SIZE);
        bb.order(ByteOrder.nativeOrder());
        return bb.asIntBuffer();
    }

    public static IntBuffer allocateIntBuffer(int[] buffer) {
        IntBuffer ib = allocateIntBuffer(buffer.length);
        ib.put(buffer);
        ib.position(0);
        return ib;
    }
}
