package com.redsponge.gltest.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.redsponge.gltest.R;
import com.redsponge.gltest.gl.input.InputHandler;
import com.redsponge.gltest.gl.projection.FitViewport;
import com.redsponge.gltest.gl.projection.ScaleViewport;
import com.redsponge.gltest.gl.projection.Viewport;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import static android.opengl.GLES31.*;

public class GLRenderer implements GLSurfaceView.Renderer, InputHandler {

    private ShapeRenderer shapeRenderer;
    private TextureRenderer textureRenderer;
    private Viewport viewport;
    private float touchX, touchY;
    private Texture texture;

    private ArrayList<ShapeRenderer.Vertex> points;

    private Context context;

    public GLRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.5f, 0.5f, 1, 1);


        points = new ArrayList<>();
        shapeRenderer = new ShapeRenderer();
        textureRenderer = new TextureRenderer();
        viewport = new ScaleViewport(160, 90);
        viewport.centerCamera();

        texture = new Texture(context.getResources(), R.drawable.icon);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        viewport.resize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        viewport.apply();
//        shapeRenderer.setProjectionMatrix(viewport.getCamera().getCombinedMatrix());
//
//        shapeRenderer.drawVertexArray(points.toArray(new ShapeRenderer.Vertex[0]));

        glEnable(GL_BLEND);
        textureRenderer.setProjectionMatrix(viewport.getCamera().getCombinedMatrix());
        textureRenderer.render(texture);
        glDisable(GL_BLEND);
//        shapeRenderer.drawTriangle(40, 50, 10, 70, 100, 100);
    }

    public Viewport getViewport() {
        return viewport;
    }

    @Override
    public void onTouch(float x, float y) {
        this.touchX = x;
        this.touchY = y;

        Vector2 output = new Vector2();
        viewport.unproject(new Vector2(x, viewport.getScreenHeight() - y), output);
        points.add(new ShapeRenderer.Vertex(output.x, output.y, 1, 1, 1, 1));

    }

    @Override
    public void onDrag(float x, float y) {
        float dx = x - touchX;
        float dy = y - touchY;
//        Log.d("GLRenderer", "drag! " + dx + " " + dy);
        this.touchX = x;
        this.touchY = y;
    }

    @Override
    public void onRelease(float x, float y) {

    }
}
