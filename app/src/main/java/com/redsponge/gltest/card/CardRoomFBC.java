package com.redsponge.gltest.card;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CardRoomFBC implements Iterable<CardFBC> {

    private final DatabaseReference reference;

    private Map<String, CardFBC> displayConnectorMap;
    private List<String> cardOrder;

    public CardRoomFBC(DatabaseReference reference) {
        this.reference = reference;
        displayConnectorMap = new HashMap<>();

        cardOrder = new ArrayList<>();

        reference.child("cards").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String lastName) {
                CardDisplay cd = dataSnapshot.getValue(CardDisplay.class);
                CardFBC cfc = new CardFBC(cd, dataSnapshot.getRef());

                displayConnectorMap.put(dataSnapshot.getKey(), cfc);
//                cardOrder.add(dataSnapshot.getKey());
//                pushCardOrder();
                Log.d("CardRoomFBC", "Loaded card " + cd + " and card order is now " + cardOrder);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String lastName) {
                // Handled by individual connectors.
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                CardFBC fbc = Objects.requireNonNull(displayConnectorMap.get(dataSnapshot.getKey()));
                fbc.detach();
                displayConnectorMap.remove(dataSnapshot.getKey());
                cardOrder.remove(dataSnapshot.getKey());
                pushCardOrder();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("card_order").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                synchronized (CardRoomFBC.this) {
                    System.out.println("Reloading order!");
                    cardOrder.clear();
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        cardOrder.add(s.getValue(String.class));
                    }
                    System.out.println("Done reloading order!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void initialzieRoom(DatabaseReference reference) {
        List<String> order = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DatabaseReference dr = reference.child("cards").push();
            dr.setValue(new CardDisplay(i * 10, 40));
            order.add(dr.getKey());
        }
        reference.child("card_order").setValue(order);
    }

    private void pushCardOrder() {
        reference.child("card_order").setValue(cardOrder);
    }

    public Collection<CardFBC> getCardDisplays() {
        return displayConnectorMap.values();
    }

    public Map<String, CardFBC> getDisplayConnectorMap() {
        return displayConnectorMap;
    }

    public void pushToFront(CardFBC fbc) {
        cardOrder.remove(fbc.getReference().getKey());
        cardOrder.add(fbc.getReference().getKey());
        pushCardOrder();
    }


    @NonNull
    @Override
    public Iterator<CardFBC> iterator() {
        return new Iterator<CardFBC>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                synchronized (CardRoomFBC.this) {
                    return i < cardOrder.size();
                }
            }

            @Override
            public CardFBC next() {
                synchronized (CardRoomFBC.this) {
                    System.out.println(i + " " + cardOrder.get(i) + " " + displayConnectorMap.get(cardOrder.get(i)));
                    return displayConnectorMap.get(cardOrder.get(i++));
                }
            }
        };
    }

    public synchronized boolean isFullyLoaded() {
        for (String s : cardOrder) {
            if(!displayConnectorMap.containsKey(s)) return false;
        }
        return true;
    }
}
