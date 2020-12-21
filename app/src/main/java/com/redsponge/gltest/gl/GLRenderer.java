package com.redsponge.gltest.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.redsponge.gltest.R;
import com.redsponge.gltest.gl.input.InputHandler;
import com.redsponge.gltest.gl.projection.ScaleViewport;
import com.redsponge.gltest.gl.projection.Viewport;
import com.redsponge.gltest.gl.texture.Texture;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import static android.opengl.GLES31.*;

public class GLRenderer implements GLSurfaceView.Renderer, InputHandler {

    private ShapeRenderer shapeRenderer;
    private TextureRenderer textureRenderer;
    private TextureBatch texBatch;
    private Viewport viewport;
    private float touchX, touchY;
    private Texture texture;

    private ArrayList<ShapeRenderer.Vertex> points;

    private Context context;
    private long lastTime;

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

        texBatch = new TextureBatch();

        texture = new Texture(context.getResources(), R.drawable.icon);
        lastTime = System.currentTimeMillis();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        viewport.resize(width, height);
    }

    private float x = 10;

    @Override
    public void onDrawFrame(GL10 gl) {
        long now = System.currentTimeMillis();
        float delta = (now - lastTime) / 1000f;
        lastTime = now;

        glClear(GL_COLOR_BUFFER_BIT);

        viewport.apply();
//        shapeRenderer.setProjectionMatrix(viewport.getCamera().getCombinedMatrix());
//
//        shapeRenderer.drawVertexArray(points.toArray(new ShapeRenderer.Vertex[0]));

        texBatch.setProjectionMatrix(viewport.getCamera().getCombinedMatrix());
        x += delta * 20;

        glEnable(GL_BLEND);
//        textureRenderer.setProjectionMatrix(viewport.getCamera().getCombinedMatrix());
        texBatch.render(texture, 10, 10, 20, 20);
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
