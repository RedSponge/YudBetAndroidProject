package com.redsponge.gltest.card;

import com.google.firebase.database.Exclude;
import com.redsponge.gltest.gl.Vector2;
import com.redsponge.gltest.utils.MathUtils;

public class CardDisplay {

    private float x;
    private float y;

    @Exclude
    private float drawnX;

    @Exclude
    private float drawnY;

    @Exclude
    private float drawnScale;

    @Exclude
    private float width;
    @Exclude
    private float height;

    private boolean isFlipped;
    private long chosenTime;
    private String type;

    public CardDisplay() {
        this(0, 0, "spade", 2);
    }

    public CardDisplay(float x, float y, String suit, int number) {
        this.x = x;
        this.y = y;
        this.width = 16 * 2;
        this.height = 24 * 2;
        this.type = suit + number;
        this.chosenTime = 0;
    }

    public void updateDrawnPos(float delta) {
        drawnX = MathUtils.lerp(drawnX, x, 0.2f);
        drawnY = MathUtils.lerp(drawnY, y, 0.2f);
        if(Math.abs(drawnX - x) < 0.1f) {
            drawnX = x;
        }
        if(Math.abs(drawnY - y) < 0.1f) {
            drawnY = y;
        }

        float drawnScaleTarget = isChosen() ? 1.2f : 1;
        drawnScale = MathUtils.lerp(drawnScale, drawnScaleTarget, 0.2f);
        if(Math.abs(drawnScale - drawnScaleTarget) < 0.1f) {
            drawnScale = drawnScaleTarget;
        }
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

    public long getChosenTime() {
        return chosenTime;
    }

    public void setChosenTime(long chosenTime) {
        this.chosenTime = chosenTime;
    }

    public boolean isChosen() {
        return (System.currentTimeMillis() - chosenTime) / 1000f < Constants.MAX_CHOICE_TIME;
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
        this.chosenTime = value.chosenTime;
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
