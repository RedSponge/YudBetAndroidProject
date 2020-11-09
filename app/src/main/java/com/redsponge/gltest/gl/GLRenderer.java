package com.redsponge.gltest.gl;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.redsponge.gltest.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import static android.opengl.GLES30.*;

public class GLRenderer implements GLSurfaceView.Renderer {

    private static final String vertexCode = "" +
            "attribute vec4 a_position;" +
            "attribute vec4 a_color;" +
            "uniform mat4 u_projection;" +
            "varying vec4 v_color;" +
            "void main() {" +
                "gl_Position = u_projection * a_position;" +
                "v_color = a_color" +
            "}";
    private static final String fragmentCode = "precision mediump float;" +
            "varying vec4 v_color;" +
            "void main() {" +
            "gl_FragColor = v_color;" +
            "}";

    private ShaderProgram prog;
    private ShapeRenderer shapeRenderer;




    private float[] projectionMatrix;

    private FloatBuffer vboBuffer;
    private int vboId;

    private IntBuffer eboBuffer;
    private int eboId;

    private OtherClass oc;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.5f, 0.5f, 1, 1);
        oc = new OtherClass();
        oc.create();

        shapeRenderer = new ShapeRenderer();
        Matrix.orthoM(shapeRenderer.getProjectionMatrix(), 0, 0, 160, 0, 90, -1, 1);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        oc.render();

//        shapeRenderer.drawTriangle(30, 30, 60, 60, 90, 90);
    }
}
