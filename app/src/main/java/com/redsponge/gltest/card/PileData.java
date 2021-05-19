package com.redsponge.gltest.card;

import com.redsponge.gltest.gl.Vector2;

public class PileData {

    private float x, y, width, height;
    private float drawnX, drawnY;
    private float drawnScale;
    private long chosenTime;

    public PileData() {
        this(0, 0, 16 * 2, 24 * 2, 0, 0, 1);
    }

    public PileData(float x, float y, float width, float height, float drawnX, float drawnY, float drawnScale) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.drawnX = drawnX;
        this.drawnY = drawnY;
        this.drawnScale = drawnScale;
        this.chosenTime = 0;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getDrawnX() {
        return drawnX;
    }

    public void setDrawnX(float drawnX) {
        this.drawnX = drawnX;
    }

    public float getDrawnY() {
        return drawnY;
    }

    public void setDrawnY(float drawnY) {
        this.drawnY = drawnY;
    }

    public float getDrawnScale() {
        return drawnScale;
    }

    public void setDrawnScale(float drawnScale) {
        this.drawnScale = drawnScale;
    }

    public long getChosenTime() {
        return chosenTime;
    }

    public void setChosenTime(long chosenTime) {
        this.chosenTime = chosenTime;
    }

    @Override
    public String toString() {
        return "PileDrawData{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", drawnX=" + drawnX +
                ", drawnY=" + drawnY +
                ", drawnScale=" + drawnScale +
                '}';
    }

    public boolean contains(Vector2 point) {
        return x < point.x && point.x < x + width
            && y < point.y && point.y < y + height;
    }

    public boolean isChosen() {
        return (System.nanoTime() - chosenTime) / 1000000000f < 0.1f;
    }
}
