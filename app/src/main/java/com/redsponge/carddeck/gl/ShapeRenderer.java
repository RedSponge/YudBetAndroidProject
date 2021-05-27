package com.redsponge.carddeck.gl;

import android.opengl.Matrix;

import com.redsponge.carddeck.R;
import com.redsponge.carddeck.gl.utils.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES30.GL_ARRAY_BUFFER;
import static android.opengl.GLES30.GL_DYNAMIC_DRAW;
import static android.opengl.GLES30.GL_FLOAT;
import static android.opengl.GLES30.GL_TRIANGLES;
import static android.opengl.GLES30.GL_TRIANGLE_FAN;
import static android.opengl.GLES30.glBindBuffer;
import static android.opengl.GLES30.glBindVertexArray;
import static android.opengl.GLES30.glBufferData;
import static android.opengl.GLES30.glBufferSubData;
import static android.opengl.GLES30.glDrawArrays;
import static android.opengl.GLES30.glEnableVertexAttribArray;
import static android.opengl.GLES30.glGenBuffers;
import static android.opengl.GLES30.glGenVertexArrays;
import static android.opengl.GLES30.glVertexAttribPointer;

public class ShapeRenderer {

    private int vbo, ebo, vao;
    private ShaderProgram shader;
    private float[] projection;

    public static final int FLOAT_SIZE = 4;
    private static final int MAX_VERTICES = 1024;
    private static final int MAX_INDICES = MAX_VERTICES * 3;

    public ShapeRenderer() {
        projection = new float[16];
        Matrix.orthoM(projection, 0, 0, 320, 0, 180, -1, 1);

        int[] fetchers = new int[2];
        glGenBuffers(fetchers.length, fetchers, 0);
        vbo = fetchers[0];
        ebo = fetchers[1];

        glGenVertexArrays(1, fetchers, 0);
        vao = fetchers[0];

        shader = createDefaultShader();

        glBindVertexArray(vao);

        FloatBuffer vboData = BufferUtils.allocateFloatBuffer(Vertex.getSize() * MAX_VERTICES / FLOAT_SIZE);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, Vertex.getSize() * MAX_VERTICES, vboData, GL_DYNAMIC_DRAW);

        IntBuffer eboData = BufferUtils.allocateIntBuffer(MAX_INDICES);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

        int posLocation = shader.getAttributeLocation("a_position");
        int colLocation = shader.getAttributeLocation("a_color");

        glVertexAttribPointer(posLocation, 2, GL_FLOAT, false, FLOAT_SIZE * 6, 0);
        glVertexAttribPointer(colLocation, 4, GL_FLOAT, false, FLOAT_SIZE * 6, FLOAT_SIZE * 2);

        glEnableVertexAttribArray(posLocation);
        glEnableVertexAttribArray(colLocation);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public static ShaderProgram createDefaultShader() {
        return new ShaderProgram(RawReader.readRawFile(R.raw.vertex), RawReader.readRawFile(R.raw.fragment));
    }

    public void drawVertexArray(Vertex[] arr) {
        float[] verts = vertexArrayToFloats(arr);
        FloatBuffer fb = BufferUtils.allocateFloatBuffer(verts);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, verts.length * FLOAT_SIZE, fb);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        render(arr.length, GL_TRIANGLE_FAN);
    }

    public void setProjectionMatrix(float[] projectionMatrix) {
        System.arraycopy(projectionMatrix, 0, projection, 0, projectionMatrix.length);
    }

    private float[] vertexArrayToFloats(Vertex[] arr) {
        float[] output = new float[arr.length * Vertex.getSize() / FLOAT_SIZE];
        for(int i = 0; i < arr.length; i++) {
            vertexToFloats(output, i * 6, arr[i]);
        }
        return output;
    }

    private void vertexToFloats(float[] output, int offset, Vertex vertex) {
        output[offset    ] = vertex.x;
        output[offset + 1] = vertex.y;
        output[offset + 2] = vertex.r;
        output[offset + 3] = vertex.g;
        output[offset + 4] = vertex.b;
        output[offset + 5] = vertex.a;
    }

    public void drawTriangle(float x1, float y1, float x2, float y2, float x3, float y3) {
        final float[] verts = new float[] {
                x1, y1, 1, 1, 0, 1,
                x2, y2, 0, 1, 0, 1,
                x3, y3, 0, 0, 1, 1
        };
        FloatBuffer fb = BufferUtils.allocateFloatBuffer(verts);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, verts.length * FLOAT_SIZE, fb);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        render(3, GL_TRIANGLES);
    }

    public void render(int vertexCount, int mode) {
        shader.bind();
        shader.setUniformMat4("u_projection", projection);
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glDrawArrays(mode, 0, vertexCount);
    }

    public static class Vertex {
        private float x, y;
        private float r, g, b, a;

        public Vertex(float x, float y, float r, float g, float b, float a) {
            this.x = x;
            this.y = y;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }

        public static int getSize() {
            return 6 * FLOAT_SIZE;
        }

        @Override
        public String toString() {
            return "Vertex{" +
                    "x=" + x +
                    ", y=" + y +
                    ", r=" + r +
                    ", g=" + g +
                    ", b=" + b +
                    ", a=" + a +
                    '}';
        }
    }
}
