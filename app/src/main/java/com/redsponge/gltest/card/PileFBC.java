package com.redsponge.gltest.card;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.redsponge.gltest.utils.Listeners;
import com.redsponge.gltest.utils.MathUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PileFBC implements Iterable<CardFBC> {

    private final RoomFBC roomIn;
    private final List<String> cardList;
    private final DatabaseReference ref;
    private final ValueEventListener listener;

    private float drawnX, drawnY, drawnScale;

    private PileData drawData;

    public PileFBC(RoomFBC roomIn, DatabaseReference ref) {
        this(roomIn, ref, null);
    }

    public PileFBC(RoomFBC roomIn, DatabaseReference ref, List<String> initialCards) {
        this.roomIn = roomIn;
        this.cardList = new ArrayList<>();
        this.ref = ref;
        this.drawData = new PileData();
        if(initialCards != null) cardList.addAll(initialCards);

        ref.child(Constants.TRANSFORM_REFERENCE).addValueEventListener(Listeners.value(data -> drawData = data.getValue(PileData.class)));

        ref.child(Constants.CARDS_REFERENCE).addValueEventListener(listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                synchronized (roomIn) {
                    cardList.clear();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        cardList.add(child.getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public synchronized CardFBC popTopCard() {
        CardFBC card = getTopCard();
        cardList.remove(0);
        pushUpdate();
        return card;
    }

    public void updateDrawnPosition() {
        drawnX = MathUtils.lerp(drawnX, drawData.getX(), 0.2f);
        drawnY = MathUtils.lerp(drawnY, drawData.getY(), 0.2f);
        if (Math.abs(drawnX - drawData.getX()) < 0.1f) {
            drawnX = drawData.getX();
        }
        if (Math.abs(drawnY - drawData.getY()) < 0.1f) {
            drawnY = drawData.getY();
        }

        float drawnScaleTarget = drawData.isChosen() ? 1.2f : 1;
        drawnScale = MathUtils.lerp(drawnScale, drawnScaleTarget, 0.2f);
        if (Math.abs(drawnScale - drawnScaleTarget) < 0.1f) {
            drawnScale = drawnScaleTarget;
        }
    }

    public PileData getData() {
        return drawData;
    }

    public void pushUpdate() {
        synchronized (roomIn) {
            ref.child(Constants.CARDS_REFERENCE).setValue(cardList);
            ref.child(Constants.TRANSFORM_REFERENCE).setValue(drawData);
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

    @NonNull
    @Override
    public Iterator<CardFBC> iterator() {
        return new Iterator<CardFBC>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < cardList.size();
            }

            @Override
            public CardFBC next() {
                return roomIn.getCard(cardList.get(i++));
            }
        };
    }

    public CardFBC getTopCard() {
        return roomIn.getCard(cardList.get(0));
    }

    public void detach() {
        ref.removeEventListener(listener);
    }

    public List<String> getCardOrder() {
        return cardList;
    }

    public DatabaseReference getReference() {
        return ref;
    }

    public boolean hasTopCard() {
        return cardList.size() > 0 && roomIn.getCard(cardList.get(0)) != null;
    }
}
