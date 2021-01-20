package com.redsponge.gltest.card;

import com.google.firebase.database.Exclude;
import com.redsponge.gltest.gl.Vector2;

public class CardDisplay {

    private float x;
    private float y;

    @Exclude
    private float width;
    @Exclude
    private float height;

    private boolean isFlipped;
    private boolean isChosen;
    private String type;

    public CardDisplay() {
        this(0, 0);
    }

    public CardDisplay(float x, float y) {
        this.x = x;
        this.y = y;
        this.width = 16 * 2;
        this.height = 24 * 2;
        this.type = "suit1";
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

    public void set(CardDisplay value) {
        this.x = value.x;
        this.y = value.y;
        this.width = value.width;
        this.height = value.height;
        this.isChosen = value.isChosen;
        this.isFlipped = value.isFlipped;
        this.type = value.type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
