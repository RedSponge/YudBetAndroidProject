package com.redsponge.carddeck.card;

public class CardData {

    private boolean isFlipped;
    private String type;

    public CardData() {
        this(0, 0, "spade", 2);
    }

    public CardData(float x, float y, String suit, int number) {
        this.type = suit + number;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean flipped) {
        isFlipped = flipped;
    }

    public void set(CardData value) {
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
