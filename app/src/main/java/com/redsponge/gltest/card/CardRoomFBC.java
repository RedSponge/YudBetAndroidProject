package com.redsponge.gltest.card;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.redsponge.gltest.utils.ChildEventAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CardRoomFBC implements Iterable<CardFBC> {

    private final DatabaseReference reference;

    private final Map<String, CardFBC> displayConnectorMap;
    private final LinkedList<String> cardOrderList;


    public CardRoomFBC(DatabaseReference reference) {
        this.reference = reference;
        displayConnectorMap = new HashMap<>();

        cardOrderList = new LinkedList<>();
        initializeReferenceListeners();
    }

    /**
     * Add the {@link ChildEventListener}s to the reference's "cards" and "card_order" children.
     */
    private void initializeReferenceListeners() {
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

        reference.child(Constants.CARD_ORDER_REFERENCE).addChildEventListener(new ChildEventAdapter() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                synchronized (CardRoomFBC.this) {
                    cardOrderList.addLast(dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                synchronized (CardRoomFBC.this) {
                    cardOrderList.remove(dataSnapshot.getValue(String.class));
                }
            }
        });
    }

    public static void initialzieRoom(DatabaseReference reference) {
        List<String> order = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DatabaseReference dr = reference.child(Constants.CARDS_REFERENCE).push();
            dr.setValue(new CardDisplay(i * 10, 40));
            order.add(dr.getKey());
        }
        reference.child(Constants.CARD_ORDER_REFERENCE).setValue(order);
    }

    /**
     * Upload the card order.
     */
    private synchronized void pushCardOrder() {
        reference.child(Constants.CARD_ORDER_REFERENCE).setValue(cardOrderList);
    }

    /**
     * Moves a card to front (moves it to the front of the list and pushes it).
     */
    public synchronized void pushToFront(CardFBC fbc) {
        cardOrderList.remove(fbc.getReference().getKey());
        cardOrderList.add(fbc.getReference().getKey());
        pushCardOrder();
    }

    /**
     * Checks whether the client and database are synchronized - does the client have card references for all order references?
     * @return Are the database and client synchronized?
     */
    public synchronized boolean isFullyLoaded() {
        for (String s : cardOrderList) {
            if(!displayConnectorMap.containsKey(s)) {
                System.out.println("NOT FULLY LOADED! FAILED ON " + s);
                return false;
            }
        }
        return true;
    }

    /**
     * @return An iterator which goes in the order defined by {@link CardRoomFBC#cardOrderList}
     */
    @NonNull
    @Override
    public Iterator<CardFBC> iterator() {
        return new Iterator<CardFBC>() {
            private final Iterator<String> linkedListIterator = cardOrderList.iterator();

            @Override
            public boolean hasNext() {
                synchronized (CardRoomFBC.this) {
                    return linkedListIterator.hasNext();
                }
            }

            @Override
            public CardFBC next() {
                synchronized (CardRoomFBC.this) {
                    return displayConnectorMap.get(linkedListIterator.next());
                }
            }
        };
    }
}
