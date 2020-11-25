package com.redsponge.gltest.gl;

import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.redsponge.gltest.gl.input.InputHandler;
import com.redsponge.gltest.gl.projection.FitViewport;
import com.redsponge.gltest.gl.projection.ScaleViewport;
import com.redsponge.gltest.gl.projection.Viewport;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import static android.opengl.GLES31.*;

public class GLRenderer implements GLSurfaceView.Renderer, InputHandler {

    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private float touchX, touchY;
    private Vector2 testPos;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.5f, 0.5f, 1, 1);

        testPos = new Vector2(100, 100);
        shapeRenderer = new ShapeRenderer();
        viewport = new ScaleViewport(160, 90);
        viewport.centerCamera();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        viewport.resize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        

        viewport.apply();
        shapeRenderer.setProjectionMatrix(viewport.getCamera().getCombinedMatrix());

        shapeRenderer.drawTriangle(40, 50, 10, 70, testPos.x, testPos.y);
    }

    public Viewport getViewport() {
        return viewport;
    }

    @Override
    public void onTouch(float x, float y) {
        this.touchX = x;
        this.touchY = y;

        viewport.unproject(new Vector2(x, viewport.getWorldHeight() - y), testPos);
    }

    @Override
    public void onDrag(float x, float y) {
        float dx = x - touchX;
        float dy = y - touchY;

        viewport.unproject(new Vector2(x, viewport.getScreenHeight() - y), testPos);
//        Log.d("GLRenderer", "drag! " + dx + " " + dy);
        this.touchX = x;
        this.touchY = y;
    }

    @Override
    public void onRelease(float x, float y) {

    }
}
