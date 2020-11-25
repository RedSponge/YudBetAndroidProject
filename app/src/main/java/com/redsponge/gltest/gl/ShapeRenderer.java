package com.redsponge.gltest.gl;

import android.opengl.Matrix;

import com.redsponge.gltest.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES30.*;

public class ShapeRenderer {

    private int vbo, ebo, vao;
    private ShaderProgram shader;
    private float[] projection;
    public static final int FLOAT_SIZE = 4;
    private static final int MAX_VERTICES = 1024;

    public ShapeRenderer() {
        projection = new float[16];
        Matrix.orthoM(projection, 0, 0, 320, 0, 180, -1, 1);

        int[] fetchers = new int[1];
        glGenBuffers(fetchers.length, fetchers, 0);
        vbo = fetchers[0];

        glGenVertexArrays(1, fetchers, 0);
        vao = fetchers[0];

        shader = createDefaultShader();

        glBindVertexArray(vao);

        ByteBuffer bb = ByteBuffer.allocateDirect(Vertex.getSize() * MAX_VERTICES);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer vboData = bb.asFloatBuffer();
//        vboData.put(verts);
        vboData.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, Vertex.getSize() * 3, vboData, GL_DYNAMIC_DRAW);

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
        ByteBuffer bb = ByteBuffer.allocateDirect(verts.length * FLOAT_SIZE);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(verts);
        fb.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, verts.length * FLOAT_SIZE, fb);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        render();
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
        ByteBuffer bb = ByteBuffer.allocateDirect(verts.length * FLOAT_SIZE);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(verts);
        fb.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, verts.length * FLOAT_SIZE, fb);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        render();
    }

    public void render() {
        shader.bind();
        shader.setUniformMat4("u_projection", projection);
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glDrawArrays(GL_TRIANGLES, 0, 3);
    }

    private static class Vertex {
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
    }
}
