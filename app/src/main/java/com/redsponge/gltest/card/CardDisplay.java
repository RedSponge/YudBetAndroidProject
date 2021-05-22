package com.redsponge.gltest.card;

import com.google.firebase.database.Exclude;
import com.redsponge.gltest.gl.Vector2;
import com.redsponge.gltest.utils.MathUtils;

public class CardDisplay {

    private boolean isFlipped;
    private String type;

    public CardDisplay() {
        this(0, 0, "spade", 2);
    }

    public CardDisplay(float x, float y, String suit, int number) {
        this.type = suit + number;
    }

    public void updateDrawnPos(float delta) {

    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean flipped) {
        isFlipped = flipped;
    }

    public boolean contains(Vector2 point) {
        return true;
    }

    public void set(CardDisplay value) {
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
