    package com.redsponge.carddeck.card;

    import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
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

    //region Listener variables

    private ChildEventAdapter roomPlayerListAdapter;
    private ChildEventAdapter roomPileListAdapter;

    //endregion

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
        reference.child(Constants.ROOM_PLAYERS_REFERENCE).addChildEventListener(roomPlayerListAdapter = new ChildEventAdapter() {
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
        reference.child(Constants.PILES_REFERENCE).addChildEventListener(roomPileListAdapter = new ChildEventAdapter() {
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
        List<Pair<Vector2, String>> newPiles = new ArrayList<>(); // <Position, Card>

        Random rnd = new Random();

        for (PileFBC pile : pileMap.values()) {
            int marginX = 30;
            int marginY = 40;
            for (int i = 0; i < pile.getSize() - 1; i++) {
                int x = rnd.nextInt((int) (320 * 1.5f) - marginX * 2) + marginX;
                int y = rnd.nextInt((int) (180 * 1.5f) - marginY * 2) + marginY;
                newPiles.add(new Pair<>(new Vector2(x, y), pile.getCardId(i)));
            }

            if(pile.getSize() > 1) {
                String topCard = pile.getCardId(pile.getSize() - 1);
                pile.getCardList().clear();
                pile.getCardList().add(topCard);
            }

            int x = rnd.nextInt((int) (320 * 1.5f) - marginX * 2) + marginX;
            int y = rnd.nextInt((int) (180 * 1.5f) - marginY * 2) + marginY;
            pile.setX(x);
            pile.setY(y);
        }

        for (int i = 0; i < newPiles.size(); i++) {
            DatabaseReference pileRef = reference.child(Constants.PILES_REFERENCE).push();
            pileRef.child(Constants.CARDS_REFERENCE).push().setValue(newPiles.get(i).second);
            pileRef.child(Constants.TRANSFORM_REFERENCE).setValue(new PileData(newPiles.get(i).first.x, newPiles.get(i).first.y, 0));

            pileOrder.add(pileRef.getKey());
        }
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

    public String getRoomName() {
        return roomName;
    }

    public void detach() {
        reference.child(Constants.PILES_REFERENCE).removeEventListener(roomPileListAdapter);
        reference.child(Constants.ROOM_PLAYERS_REFERENCE).removeEventListener(roomPlayerListAdapter);
        pileMap.forEachValue(pileMap.size(), PileFBC::detach);
        pileMap.clear();
        pileOrder.detach();
        cardList.detach();
    }

    /**
     * Removes a player and creates a pile of their in-hand cards if they had any
     * @param uid The player's UID
     */
    public void removePlayer(String uid) {
        if(playerCardsMap.get(uid).size() > 0) {
            SynchronizedList<String> playerCards = playerCardsMap.get(uid);
            DatabaseReference pileRef = newPileRef();
            pileRef.child(Constants.TRANSFORM_REFERENCE).setValue(new PileData(200, 100, 0));
            playerCards.forEach(s -> {
                pileRef.child(Constants.CARDS_REFERENCE).push().setValue(s);
            });
            pileOrder.add(pileRef.getKey());
        }

        reference.child(Constants.ROOM_PLAYERS_REFERENCE).child(uid).removeValue();
    }

    public int getPlayerAmount() {
        return playerCardsMap.size();
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
