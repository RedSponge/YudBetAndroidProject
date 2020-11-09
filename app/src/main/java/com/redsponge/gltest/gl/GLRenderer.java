package com.redsponge.gltest.gl;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import static android.opengl.GLES20.*;

public class GLRenderer implements GLSurfaceView.Renderer {

    private String vertexCode = "attribute vec4 a_position;" +
            "" +
            "void main() {" +
            "gl_Position = a_position;" +
            "}";
    private String fragmentCode = "void main() {" +
            "gl_FragColor = vec4(1, 0, 0, 1);" +
            "}";

    private ShaderProgram prog;

    private float[] triangle = {
        -.5f, -.5f,
            .5f, -.5f,
            0, .5f
    };

    private float[] projectionMatrix;

    private FloatBuffer vboBuffer;
    private int vboId;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.5f, 0.5f, 1, 1);
        prog = new ShaderProgram(vertexCode, fragmentCode);

        projectionMatrix = new float[16];

        Matrix.setIdentityM(projectionMatrix, 0);
        Matrix.orthoM(projectionMatrix, 0, 0, 160, 0, 90, -1, 1);

        IntBuffer vboIdFetcher = IntBuffer.allocate(1);
        glGenBuffers(1, vboIdFetcher);
        vboId = vboIdFetcher.get(0);

        ByteBuffer buff = ByteBuffer.allocateDirect(triangle.length * 4);
        buff.order(ByteOrder.nativeOrder());
        vboBuffer = buff.asFloatBuffer();

        vboBuffer.put(triangle);
        vboBuffer.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, 6 * 4, vboBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        prog.bind();
        int posLocation = prog.getAttributeLocation("a_position");
        glEnableVertexAttribArray(posLocation);
        glVertexAttribPointer(posLocation, 2, GL_FLOAT, false, 4 * 2, 0);

//        prog.setUniformMat4("u_projection", projectionMatrix);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glDrawArrays(GL_TRIANGLES, 0, 3);

        glDisableVertexAttribArray(posLocation);
    }
}
