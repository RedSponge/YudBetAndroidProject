package com.redsponge.gltest.gl.projection;

import android.opengl.Matrix;

import com.redsponge.gltest.gl.Vector2;

public class Camera {

    private Vector2 position;
    private float rotation;
    private float[] projectionMatrix;
    private float[] viewMatrix;
    private float[] combinedMatrix;
    private float[] invertedCombinedMatrix;

    public Camera(float worldWidth, float worldHeight) {
        this.position = new Vector2(0, 0);
        this.rotation = 0;
        this.viewMatrix = new float[16];
        this.projectionMatrix = new float[16];
        this.combinedMatrix = new float[16];
        this.invertedCombinedMatrix = new float[16];
        updateOrtho(worldWidth, worldHeight);
    }

    public void updateOrtho(float width, float height) {
        Matrix.orthoM(projectionMatrix, 0, -width / 2, width / 2, -height / 2, height / 2, -1, 1);
    }

    public void apply() {
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.rotateM(viewMatrix, 0, rotation, 0, 0, 1);
        Matrix.translateM(viewMatrix, 0, -position.x, -position.y, 0);

        Matrix.setIdentityM(combinedMatrix, 0);
        Matrix.multiplyMM(combinedMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        Matrix.invertM(invertedCombinedMatrix, 0, combinedMatrix, 0);
    }

    public float[] getProjectionMatrix() {
        return projectionMatrix;
    }

    public float[] getViewMatrix() {
        return viewMatrix;
    }

    public float[] getCombinedMatrix() {
        return combinedMatrix;
    }

    public float[] getInvertedCombinedMatrix() {
        return invertedCombinedMatrix;
    }

    public Vector2 getPosition() {
        return position;
    }
}
