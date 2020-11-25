package com.redsponge.gltest.gl.projection;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.redsponge.gltest.gl.Vector2;

public abstract class Viewport {

    protected float worldWidth;
    protected float worldHeight;
    protected Camera camera;

    protected float screenWidth, screenHeight;

    private float[] viewportRecord;

    public Viewport(float worldWidth, float worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.camera = new Camera(worldWidth, worldHeight);
        this.screenWidth = 0;
        this.screenHeight = 0;
        this.viewportRecord = new float[4];
    }

    public abstract void apply();
    public void resize(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    public void updateGLViewport(int x, int y, int width, int height) {
        GLES20.glViewport(x, y, width, height);
        this.viewportRecord[0] = x;
        this.viewportRecord[1] = y;
        this.viewportRecord[2] = width;
        this.viewportRecord[3] = height;
    }

    public void centerCamera() {
        camera.getPosition().x = worldWidth / 2f;
        camera.getPosition().y = worldHeight / 2f;
    }
    public Camera getCamera() {
        return camera;
    }

    public float getWorldWidth() {
        return worldWidth;
    }

    public float getWorldHeight() {
        return worldHeight;
    }

    public void project(Vector2 worldCoords, Vector2 output) {
        float[] result = new float[] {
                worldCoords.x, worldCoords.y, 0, 1
        };

        Matrix.multiplyMV(result, 0, camera.getCombinedMatrix(), 0, result, 0);

        output.x = (result[0] + 1) / 2f * viewportRecord[2] + viewportRecord[0];
        output.y = (result[1] + 1) / 2f * viewportRecord[3] + viewportRecord[1];
    }

    public void unproject(Vector2 screenCoords, Vector2 output) {

        float[] result = new float[] {
                ((screenCoords.x  - viewportRecord[0]) / viewportRecord[2]) * 2 - 1, ((screenCoords.y - viewportRecord[1]) / viewportRecord[3]) * 2 - 1, 0, 1
        };

        Matrix.multiplyMV(result, 0, camera.getInvertedCombinedMatrix(), 0, result, 0);
        output.x = result[0];
        output.y = result[1];
    }

    public float getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(float screenWidth) {
        this.screenWidth = screenWidth;
    }

    public float getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(float screenHeight) {
        this.screenHeight = screenHeight;
    }
}
