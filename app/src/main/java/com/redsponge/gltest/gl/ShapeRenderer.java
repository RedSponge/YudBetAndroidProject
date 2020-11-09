package com.redsponge.gltest.gl;

import android.graphics.Color;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import static android.opengl.GLES30.*;
public class ShapeRenderer {


    private float[] projectionMatrix;
    private ShaderProgram shader;

    private FloatBuffer vbo;
    private int vboId;

//    private int vaoId;

    private int color = Color.WHITE;

    private static final int FLOAT_SIZE = 4;

    public ShapeRenderer() {
        this.projectionMatrix = new float[16];
        Matrix.setIdentityM(projectionMatrix, 0);
        Matrix.orthoM(projectionMatrix, 0, 0, 160, 0, 90, -1, 1);

        shader = createDefaultShader();

        int[] fetcher = new int[1];
        glGenBuffers(1, fetcher, 0);
        vboId = fetcher[0];

        float[] test = new float[] {
               -0.5f, -0.5f,
                0.5f, -0.5f,
                0, 0.5f
        };

        ByteBuffer bb = ByteBuffer.allocateDirect(test.length * FLOAT_SIZE);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(test);
        fb.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, test.length * FLOAT_SIZE, fb, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

//        glGenVertexArrays(1, fetcher, 0);
//        vaoId = fetcher[0];

//        glBindVertexArray(vaoId);

//        int posLocation = shader.getAttributeLocation("a_position");
//        int colLocation = shader.getAttributeLocation("a_color");
//
//        glVertexAttribPointer(posLocation, 2, GL_FLOAT, false, 6 * GL_FLOAT, 0);
//        glVertexAttribPointer(colLocation, 4, GL_FLOAT, false, 6 * GL_FLOAT, 2 * GL_FLOAT);
//
//        glEnableVertexAttribArray(posLocation);
//        glEnableVertexAttribArray(colLocation);

//        glBindVertexArray(0);
    }

    public void drawTriangle(float x1, float y1, float x2, float y2, float x3, float y3) {
        float r = Color.red(color) / 255f;
        float g = Color.green(color) / 255f;
        float b = Color.blue(color) / 255f;
        float a = Color.alpha(color) / 255f;
        float[] vboData = {
                x1, y1, 0, r, g, b, a,
                x2, y2, 0, r, g, b, a,
                x3, y3, 0, r, g, b, a
        };

//        ByteBuffer buff = ByteBuffer.allocateDirect(vboData.length * FLOAT_SIZE);
//        buff.order(ByteOrder.nativeOrder());
//        FloatBuffer fb = buff.asFloatBuffer();
//
//        fb.position(0);
//        fb.put(vboData);
//        fb.position(0);

        shader.bind();
        shader.setUniformMat4("u_projection", projectionMatrix);

        glBindBuffer(GL_ARRAY_BUFFER, vboId);

        int posLocation = shader.getAttributeLocation("a_position");
//        int colLocation = shader.getAttributeLocation("a_color");

        glVertexAttribPointer(posLocation, 2, GL_FLOAT, false, 2 * GL_FLOAT, 0);
//        glVertexAttribPointer(colLocation, 4, GL_FLOAT, false, 6 * GL_FLOAT, 2 * GL_FLOAT);

        glEnableVertexAttribArray(posLocation);
//        glEnableVertexAttribArray(colLocation);
//        glBindVertexArray(vaoId);

        glDrawArrays(GL_TRIANGLES, 0, 3);

//        glDisableVertexAttribArray(posLocation);
//        glDisableVertexAttribArray(colLocation);
    }


    public static ShaderProgram createDefaultShader() {
        String defaultVertexShader =
                "attribute vec4 a_position;\n" +
                "uniform mat4 u_projection;\n" +
                "varying vec4 v_color;\n" +
                "void main() {\n" +
                    "gl_Position = u_projection * a_position;\n" +
                "}\n";

        String defaultFragmentShader =
                "precision mediump float;" +
                "void main() {" +
                    "gl_FragColor = vec4(1, 0, 0, 1);" +
                "}";

        return new ShaderProgram(defaultVertexShader, defaultFragmentShader);
    }

    public float[] getProjectionMatrix() {
        return projectionMatrix;
    }
}
