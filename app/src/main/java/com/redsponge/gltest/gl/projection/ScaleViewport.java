package com.redsponge.gltest.gl.projection;

public class ScaleViewport extends Viewport {

    public ScaleViewport(float worldWidth, float worldHeight) {
        super(worldWidth, worldHeight);
    }

    @Override
    public void apply() {
        super.apply();
        camera.apply();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        updateGLViewport(0, 0, width, height);
    }
}
