package com.redsponge.carddeck.card;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.redsponge.carddeck.utils.ChildEventAdapter;
import com.redsponge.carddeck.utils.SynchronizedList;

import java.util.HashMap;
import java.util.Iterator;

public class RoomFBC implements Iterable<PileFBC> {

    private final DatabaseReference reference;

    private final SynchronizedList<String> pileOrder;

    private final SynchronizedList<CardData> cardList;
    private final HashMap<String, PileFBC> pileList;

    private final String roomName;

    public RoomFBC(DatabaseReference reference) {
        this.reference = reference;
        this.roomName = reference.getKey();

        this.cardList = new SynchronizedList<>(reference.child(Constants.CARDS_REFERENCE), CardData.class);
        this.pileOrder = new SynchronizedList<>(reference.child(Constants.PILE_ORDER_REFERENCE), String.class);

        this.pileList = new HashMap<>();

        syncPiles();
    }

    private void syncPiles() {
        reference.child(Constants.PILES_REFERENCE).addChildEventListener(new ChildEventAdapter() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String prevChildName) {
                if(pileList.containsKey(dataSnapshot.getKey())) {
                    Log.w("RoomFBC", "Tried to re-add pile with key " + dataSnapshot.getKey());
                } else {
                    pileList.put(dataSnapshot.getKey(), new PileFBC(RoomFBC.this, dataSnapshot.getRef()));
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if(!pileList.containsKey(dataSnapshot.getKey())) {
                    Log.w("RoomFBC", "Tried to re-remove pile with key " + dataSnapshot.getKey());
                } else {
                    pileList.remove(dataSnapshot.getKey());
                }
            }
        });
    }

    public void updatePiles() {
        for (PileFBC value : pileList.values()) {
            value.updateDrawnPosition();
        }
    }

    public void setCardFlip(String key, boolean flipped) {
        CardData newData = cardList.get(key).cpy();
        newData.setFlipped(flipped);
        cardList.set(key, newData);
    }

    public CardData getCard(String key) {
        return cardList.get(key);
    }

    @NonNull
    @Override
    public Iterator<PileFBC> iterator() {
        return new PileIterator();
    }

    public void setPileSelection(String pileKey, long pileSelectionTime) {
        reference.child(Constants.PILES_REFERENCE).child(Constants.TRANSFORM_REFERENCE).child("chosenTime").setValue(pileSelectionTime);
    }

    public boolean isLoaded() {
        for (String pileKey : pileOrder) {
            if(!pileList.containsKey(pileKey)) return false;
        }
        return true;
    }

    public PileFBC getPile(String pileKey) {
        return pileList.get(pileKey);
    }

    public class PileIterator implements Iterator<PileFBC> {
        private int i = 0;

        @Override
        public boolean hasNext() {
            return i < pileOrder.size();
        }

        @Override
        public PileFBC next() {
            return pileList.get(pileOrder.get(i++));
        }
    }
}
