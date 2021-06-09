package com.redsponge.carddeck.card;

import com.google.firebase.database.DatabaseReference;
import com.redsponge.carddeck.utils.Listeners;
import com.redsponge.carddeck.utils.MathUtils;
import com.redsponge.carddeck.utils.SynchronizedList;

public class PileFBC {

    private final RoomFBC roomIn;
    private SynchronizedList<String> cardList;
    private float drawnX, drawnY, drawnScale;

    private final DatabaseReference ref;

    private PileData data;

    public PileFBC(RoomFBC roomIn, DatabaseReference ref) {
        this.roomIn = roomIn;
        this.cardList = new SynchronizedList<>(ref.child(Constants.CARDS_REFERENCE), String.class);
        this.data = new PileData();

        this.ref = ref;

        this.drawnScale = 1;
        this.drawnX = -100;
        this.drawnY = -100;

        ref.child(Constants.TRANSFORM_REFERENCE).addValueEventListener(Listeners.value(data -> {
            this.data = data.getValue(PileData.class);
            if(this.data != null) {
                if (drawnX < 0) drawnX = this.data.getX();
                if (drawnY < 0) drawnY = this.data.getY();
            }
        }));
    }

    public void updateDrawnPosition(boolean isChosen) {
        if(drawnX < 0 || drawnY < 0) return;

        drawnX = MathUtils.lerp(drawnX, data.getX(), 0.2f);
        drawnY = MathUtils.lerp(drawnY, data.getY(), 0.2f);
        if (Math.abs(drawnX - data.getX()) < 0.1f) {
            drawnX = data.getX();
        }
        if (Math.abs(drawnY - data.getY()) < 0.1f) {
            drawnY = data.getY();
        }

        float drawnScaleTarget = (isChosen || data.isChosen()) ? 1.2f : 1;
        drawnScale = MathUtils.lerp(drawnScale, drawnScaleTarget, 0.2f);
        if (Math.abs(drawnScale - drawnScaleTarget) < 0.1f) {
            drawnScale = drawnScaleTarget;
        }
    }

    public void setData(PileData data) {
        ref.child(Constants.TRANSFORM_REFERENCE).setValue(data);
    }

    public PileData getData() {
        return data;
    }

    public void setChosenTime(long chosenTime) {
        ref.child(Constants.TRANSFORM_REFERENCE).child("chosenTime").setValue(chosenTime);
    }

    public long getChosenTime() {
        return data.getChosenTime();
    }

    public void setX(float x) {
        ref.child(Constants.TRANSFORM_REFERENCE).child("x").setValue(x);
    }

    public float getX() {
        return data.getX();
    }

    public void setY(float y) {
        ref.child(Constants.TRANSFORM_REFERENCE).child("y").setValue(y);
    }

    public float getY() {
        return data.getY();
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

    public DatabaseReference getReference() {
        return ref;
    }

    public boolean overlaps(PileFBC other) {
        PileData otherData = other.getData();
        return false;
    }

    public boolean hasTopCard() {
        return cardList.size() > 0;
    }

    public int getSize() {
        return cardList.size();
    }

    public String getCardId(int idx) {
        return cardList.get(idx);
    }

    public CardData getCard(int idx) {
        return roomIn.getCard(cardList.get(idx));
    }

    public SynchronizedList<String> getCardList() {
        return cardList;
    }

    public void delete() {
        ref.removeValue();
    }
}
