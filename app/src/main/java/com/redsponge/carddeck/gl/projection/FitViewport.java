package com.redsponge.carddeck.gl.projection;

public class FitViewport extends Viewport {

    private final float whRatio;

    public FitViewport(float worldWidth, float worldHeight) {
        super(worldWidth, worldHeight);
        this.whRatio = worldWidth / worldHeight;
    }

    @Override
    public void apply() {
        super.apply();
        camera.apply();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        int usedWidth, usedHeight;

        if(width > height) {
            usedHeight = height;
            usedWidth = (int) (whRatio * height);
        } else {
            usedWidth = width;
            usedHeight = (int) (width / whRatio);
        }

        int offsetX = (width - usedWidth) / 2;
        int offsetY = (height - usedHeight) / 2;

        updateGLViewport(offsetX, offsetY, usedWidth, usedHeight);
    }
}
