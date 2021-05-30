    package com.redsponge.carddeck.card;

    import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.redsponge.carddeck.utils.ChildEventAdapter;
import com.redsponge.carddeck.utils.SynchronizedList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RoomFBC implements Iterable<PileFBC> {

    private final DatabaseReference reference;

    private final SynchronizedList<String> pileOrder;

    private final SynchronizedList<CardData> cardList;
    private final ConcurrentHashMap<String, PileFBC> pileMap;

    private final String roomName;

    public RoomFBC(DatabaseReference reference) {
        this.reference = reference;
        this.roomName = reference.getKey();

        this.cardList = new SynchronizedList<>(reference.child(Constants.CARDS_REFERENCE), CardData.class);
        this.pileOrder = new SynchronizedList<>(reference.child(Constants.PILE_ORDER_REFERENCE), String.class);

        this.pileMap = new ConcurrentHashMap<>();

        syncPiles();
    }

    private void syncPiles() {
        reference.child(Constants.PILES_REFERENCE).addChildEventListener(new ChildEventAdapter() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String prevChildName) {
                if(pileMap.containsKey(dataSnapshot.getKey())) {
                    Log.w("RoomFBC", "Tried to re-add pile with key " + dataSnapshot.getKey());
                } else {
                    pileMap.put(dataSnapshot.getKey(), new PileFBC(RoomFBC.this, dataSnapshot.getRef()));
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if(!pileMap.containsKey(dataSnapshot.getKey())) {
                    Log.w("RoomFBC", "Tried to re-remove pile with key " + dataSnapshot.getKey());
                } else {
                    pileMap.remove(dataSnapshot.getKey());
                }
            }
        });
    }

    public void updatePiles() {
        for (PileFBC value : pileMap.values()) {
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
            if(!pileMap.containsKey(pileKey)) return false;
        }
        return true;
    }

    public PileFBC getPile(String pileKey) {
        return pileMap.get(pileKey);
    }

    public DatabaseReference newPileRef() {
        return reference.child(Constants.PILES_REFERENCE).push();
    }

    public void addPileToOrder(String key) {
        pileOrder.add(key);
    }

    public void pushPileToFront(String pileKey) {
        pileOrder.removeValue(pileKey);
        pileOrder.add(pileKey);
    }

    public void mergePiles(String bottomPile, String topPile) {
        PileFBC bottom = pileMap.get(bottomPile);
        PileFBC top = pileMap.get(topPile);

        List<String> cardsToAdd = new ArrayList<>();
        for (String card : bottom.getCardList()) {
            cardsToAdd.add(card);
        }
        top.getCardList().addAll(cardsToAdd);
        pileOrder.removeValue(bottom.getReference().getKey());
        bottom.delete();
    }

    public boolean isPileLoaded(String pile) {
        return pileMap.containsKey(pile);
    }

    public class PileIterator implements Iterator<PileFBC> {
        private int i = 0;

        @Override
        public boolean hasNext() {
            return i < pileOrder.size();
        }

        @Override
        public PileFBC next() {
            return pileMap.get(pileOrder.get(i++));
        }
    }
}
