package com.redsponge.gltest.card;

import com.redsponge.gltest.gl.Vector2;

public class CardDisplay {

    private float x;
    private float y;
    private float width;
    private float height;
    private boolean isFlipped;
    private boolean isChosen;

    public CardDisplay(float x, float y) {
        this.x = x;
        this.y = y;
        this.width = 16 * 2;
        this.height = 24 * 2;
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

    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean flipped) {
        isFlipped = flipped;
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

    public boolean isChosen() {
        return isChosen;
    }

    public void setChosen(boolean chosen) {
        isChosen = chosen;
    }

    public boolean contains(Vector2 point) {
        return x < point.x && point.x < x + width
            && y < point.y && point.y < y + height;
    }
}
