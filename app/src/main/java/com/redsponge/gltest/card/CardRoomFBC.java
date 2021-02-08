package com.redsponge.gltest.card;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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

        reference.child(Constants.CARDS_REFERENCE).addChildEventListener(new ChildEventListener() {
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

            //region unused methods

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String lastName) {
                // Handled by individual connectors.
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
            //endregion
        });

        reference.child("card_order").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                cardOrderList.addLast(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                cardOrderList.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
        reference.child("card_order").setValue(order);
    }

    public void addCard(CardDisplay cd) {
        DatabaseReference dr = reference.child(Constants.CARDS_REFERENCE).push();
    }

    private void pushCardOrder() {
        reference.child("card_order").setValue(cardOrderList);
    }

    public Collection<CardFBC> getCardDisplays() {
        return displayConnectorMap.values();
    }

    public Map<String, CardFBC> getDisplayConnectorMap() {
        return displayConnectorMap;
    }

    public void pushToFront(CardFBC fbc) {
        cardOrderList.remove(fbc.getReference().getKey());
        cardOrderList.add(fbc.getReference().getKey());
        pushCardOrder();
    }


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

    public synchronized boolean isFullyLoaded() {
        for (String s : cardOrderList) {
            if(!displayConnectorMap.containsKey(s)) return false;
        }
        return true;
    }
}
