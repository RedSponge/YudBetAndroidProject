package com.redsponge.carddeck.card;

import com.google.firebase.database.Exclude;
import com.redsponge.carddeck.gl.Vector2;

public class PileData {

    private float x, y, width, height;

    private long chosenTime;
    private Vector2 tmpVec;

    public PileData() {
        this(0, 0, Constants.CARD_WIDTH, Constants.CARD_HEIGHT);
    }

    public PileData(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.chosenTime = 0;
        this.tmpVec = new Vector2();
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
                '}';
    }

    public boolean contains(Vector2 point) {
        return x - width / 2f < point.x && point.x < x + width / 2f
            && y - height / 2f < point.y && point.y < y + height / 2f;
    }

    @Exclude
    public boolean isChosen() {
        return (System.nanoTime() - chosenTime) / 1000000000f < 0.1f;
    }

    @Exclude
    public Vector2 getCenter() {
        return tmpVec.set(x, y);
    }
}
