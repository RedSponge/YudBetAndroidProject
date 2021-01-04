package com.redsponge.gltest.glscreen;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;

import com.redsponge.gltest.R;
import com.redsponge.gltest.gl.TextureBatch;
import com.redsponge.gltest.gl.projection.FitViewport;
import com.redsponge.gltest.gl.texture.Texture;

public class TestScreen extends Screen {

    private FitViewport viewport;
    private TextureBatch batch;
    private Texture texture;

    public TestScreen(Context context) {
        super(context);
    }

    @Override
    public void show() {
        viewport = new FitViewport(320, 180);
        viewport.centerCamera();

        batch = new TextureBatch();

        texture = new Texture(context.getResources(), R.drawable.icon);
    }

    @Override
    public void render(float delta) {

        GLES30.glClearColor(0.5f, 0, 0, 1.0f);
        GLES30.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().getCombinedMatrix());

        batch.begin();
        batch.draw(texture, 30, 30, 80, 80);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.resize(width, height);
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
