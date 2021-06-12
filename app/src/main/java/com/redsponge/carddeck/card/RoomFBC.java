    package com.redsponge.carddeck.card;

    import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.redsponge.carddeck.gl.Vector2;
import com.redsponge.carddeck.utils.ChildEventAdapter;
import com.redsponge.carddeck.utils.Pair;
import com.redsponge.carddeck.utils.SynchronizedList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class RoomFBC implements Iterable<PileFBC> {

    private final DatabaseReference reference;

    private final SynchronizedList<String> pileOrder;

    private final SynchronizedList<CardData> cardList;
    private final ConcurrentHashMap<String, PileFBC> pileMap;

    private final ConcurrentHashMap<String, SynchronizedList<String>> playerCardsMap;

    private final String roomName;

    public RoomFBC(DatabaseReference reference) {
        this.reference = reference;
        this.roomName = reference.getKey();

        this.cardList = new SynchronizedList<>(reference.child(Constants.CARDS_REFERENCE), CardData.class);
        this.pileOrder = new SynchronizedList<>(reference.child(Constants.PILE_ORDER_REFERENCE), String.class);
        this.playerCardsMap = new ConcurrentHashMap<>();

        this.pileMap = new ConcurrentHashMap<>();

        syncPiles();
        syncHands();
    }

    private void syncHands() {
        reference.child(Constants.ROOM_PLAYERS_REFERENCE).addChildEventListener(new ChildEventAdapter() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String prevChildName) {
                if(playerCardsMap.containsKey(dataSnapshot.getKey())) {
                    Log.w("RoomFBC", "Tried to re-add player with key " + dataSnapshot.getKey());
                } else {
                    playerCardsMap.put(dataSnapshot.getKey(), new SynchronizedList<>(dataSnapshot.child(Constants.PLAYER_CARDS_REFERENCE).getRef(), String.class));
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if(!playerCardsMap.containsKey(dataSnapshot.getKey())) {
                    Log.w("RoomFBC", "Tried to re-remove player with key " + dataSnapshot.getKey());
                } else {
                    playerCardsMap.get(dataSnapshot.getKey()).detach();
                    playerCardsMap.remove(dataSnapshot.getKey());
                }
                super.onChildRemoved(dataSnapshot);
            }
        });
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

    public void updatePiles(String selectedPile) {
        for (PileFBC value : pileMap.values()) {
            value.updateDrawnPosition(value.getReference().getKey().equals(selectedPile));
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

        top.setX(bottom.getX());
        top.setY(bottom.getY());
        top.setDrawnX(bottom.getDrawnX());
        top.setDrawnY(bottom.getDrawnY());

        pileOrder.removeValue(bottom.getReference().getKey());
        bottom.delete();
    }

    public void spreadCards() {
        reference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                List<Pair<Vector2, String>> piles = new ArrayList<>(); // <Position, Card>

                Random rnd = new Random();


                for (PileFBC pile : pileMap.values()) {
                    for(int i = 0; i < pile.getSize(); i++) {
                        int x = rnd.nextInt((int) (320 * 1.5f));
                        int y = rnd.nextInt((int) (180 * 1.5f));

                        piles.add(new Pair<>(new Vector2(x, y), pile.getCardId(i)));
                    }
                }

                mutableData.child(Constants.PILES_REFERENCE).setValue(null);
                mutableData.child(Constants.PILE_ORDER_REFERENCE).setValue(null);


                for (int i = 0; i < piles.size(); i++) {
                    System.out.println("Adding pile " + piles.get(i));
                    MutableData pileRef = mutableData.child(Constants.PILES_REFERENCE).child(i + "");
                    pileRef.child(Constants.CARDS_REFERENCE).child("0").setValue(piles.get(i).second);
                    pileRef.child(Constants.TRANSFORM_REFERENCE).setValue(new PileData(piles.get(i).first.x, piles.get(i).first.y, 0));

                    mutableData.child(Constants.PILE_ORDER_REFERENCE).child(i + "").setValue(i + "");
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                System.out.println("Error: " + databaseError + " commited: " + b);
            }
        });
    }

    public boolean isPileLoaded(String pile) {
        return pileMap.containsKey(pile);
    }

    public void addPileToPlayerHand(String playerUid, String pile) {
        if(!pileMap.containsKey(pile) || !playerCardsMap.containsKey(playerUid)) return;

        playerCardsMap.get(playerUid).addAll(pileMap.get(pile).getCardList());
        pileOrder.removeValue(pile);
        pileMap.get(pile).getReference().removeValue();
        pileMap.remove(pile);
    }

    public SynchronizedList<String> getPlayerHand(String playerUid) {
        return playerCardsMap.get(playerUid);
    }

    public boolean isCardLoaded(String card) {
        return cardList.indexOf(card) != -1;
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
