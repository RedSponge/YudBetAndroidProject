package com.redsponge.carddeck.card;


public class CardData {

    private boolean isFlipped;
    private String type;

    public CardData() {
        this(false, "spade", 2);
    }

    public CardData(boolean isFlipped, String type) {
        this.isFlipped = isFlipped;
        this.type = type;
    }

    public CardData(boolean isFlipped, String suit, int number) {
        this(isFlipped, suit + number);
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean flipped) {
        isFlipped = flipped;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public CardData set(CardData value) {
        this.isFlipped = value.isFlipped;
        this.type = value.type;
        return this;
    }


    public CardData cpy() {
        return new CardData(isFlipped, type);
    }
}
