package com.redsponge.gltest.card;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.redsponge.gltest.utils.Listeners;
import com.redsponge.gltest.utils.ChildEventAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RoomFBC implements Iterable<PileFBC> {

    private final DatabaseReference reference;

    private final Map<String, CardFBC> displayConnectorMap;
    private final LinkedList<String> pileOrderList;

    private final Map<String, PileFBC> pileMap;

    private final String roomName;
    private int maxPlayers;

    public RoomFBC(DatabaseReference reference) {
        this.reference = reference;
        this.roomName = reference.getKey();
        this.displayConnectorMap = new HashMap<>();

        this.pileOrderList = new LinkedList<>();
        this.pileMap = new HashMap<>();

        reference.child(Constants.MAX_PLAYERS_REFERENCE).addListenerForSingleValueEvent(Listeners.value(data -> maxPlayers = data.getValue(Integer.class)));
        initializeReferenceListeners();
    }

    /**
     * Add the {@link ChildEventListener}s to the reference's "cards" and "card_order" children.
     */
    private void initializeReferenceListeners() {

        reference.child(Constants.PILES_REFERENCE).addChildEventListener(new ChildEventAdapter() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String lastName) {
                PileFBC pile = new PileFBC(RoomFBC.this, dataSnapshot.getRef());
                pileMap.put(dataSnapshot.getKey(), pile);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                PileFBC pile = Objects.requireNonNull(pileMap.get(dataSnapshot.getKey()));
                pile.detach();
                pileMap.remove(dataSnapshot.getKey());
            }
        });

        reference.child(Constants.CARDS_REFERENCE).addChildEventListener(new ChildEventAdapter() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String lastName) {
                CardDisplay cd = dataSnapshot.getValue(CardDisplay.class);
                CardFBC cfc = new CardFBC(cd, dataSnapshot.getRef());
                displayConnectorMap.put(dataSnapshot.getKey(), cfc);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                CardFBC fbc = Objects.requireNonNull(displayConnectorMap.get(dataSnapshot.getKey()));
                fbc.detach();
                displayConnectorMap.remove(dataSnapshot.getKey());
            }
        });

        reference.child(Constants.PILE_ORDER_REFERENCE).addChildEventListener(new ChildEventAdapter() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                synchronized (RoomFBC.this) {
                    pileOrderList.addLast(dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                synchronized (RoomFBC.this) {
                    pileOrderList.remove(dataSnapshot.getValue(String.class));
                }
            }
        });
    }

    public static void initializeRoom(DatabaseReference reference) {
        List<String> order = new ArrayList<>();
        List<CardDisplay> displays = new ArrayList<>();

        String[] suits = {"spade", "club", "diamond", "heart"};
        for (int i = 0; i < suits.length; i++) {
            for (int j = 1; j <= 13; j++) {
                displays.add(new CardDisplay(j * 10, i * 10 + 40, suits[i], j));
            }
        }

        DatabaseReference cardsRef = reference.child(Constants.CARDS_REFERENCE);

        for (CardDisplay display : displays) {
            DatabaseReference dr = cardsRef.push();
            dr.setValue(display);
            order.add(dr.getKey());
        }

        DatabaseReference initialPileRef = reference.child(Constants.PILES_REFERENCE).push();
        initialPileRef.child(Constants.CARDS_REFERENCE).setValue(order);
        initialPileRef.child(Constants.TRANSFORM_REFERENCE).setValue(new PileData(100, 100, 16 * 2, 24 * 2));

        reference.child(Constants.PILE_ORDER_REFERENCE).child("0").setValue(initialPileRef.getKey());
    }

    /**
     * Upload the card order.
     */
    private synchronized void pushCardOrder() {
        reference.child(Constants.PILE_ORDER_REFERENCE).setValue(pileOrderList);
    }

    /**
     * Moves a card to front (moves it to the front of the list and pushes it).
     */
    public synchronized void pushToFront(PileFBC pile) {
        pileOrderList.remove(pile.getReference().getKey());
        pileOrderList.add(pile.getReference().getKey());
        pushCardOrder();
    }

    /**
     * Checks whether the client and database are synchronized - does the client have card references for all order references?
     * @return Are the database and client synchronized?
     */
    public synchronized boolean isFullyLoaded() {
        for (String pile : pileOrderList) {
            List<String> cardOrder = pileMap.get(pile).getCardOrder();
            for (String card : cardOrder) {
                if(!displayConnectorMap.containsKey(card)) {
                    System.out.println("NOT FULLY LOADED! FAILED ON " + card);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @return An iterator which goes in the order defined by {@link RoomFBC#pileOrderList}
     */
    @NonNull
    @Override
    public Iterator<PileFBC> iterator() {
        return new Iterator<PileFBC>() {
            private final Iterator<String> linkedListIterator = pileOrderList.iterator();

            @Override
            public boolean hasNext() {
                synchronized (RoomFBC.this) {
                    return linkedListIterator.hasNext();
                }
            }

            @Override
            public PileFBC next() {
                synchronized (RoomFBC.this) {
                    return pileMap.get(linkedListIterator.next());
                }
            }
        };
    }

    public String getRoomName() {
        return roomName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public CardFBC getCard(String s) {
        return displayConnectorMap.get(s);
    }

    public List<String> getPileOrder() {
        return pileOrderList;
    }

    public PileFBC getPile(String key) {
        return pileMap.get(key);
    }
}
