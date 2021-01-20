package com.redsponge.gltest.card;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

public class CardListFBC {

    private final DatabaseReference reference;

    private List<CardDisplay> cardDisplays;
    private Map<CardDisplay, CardFBC> displayConnectorMap;

    public CardListFBC(DatabaseReference reference) {
        this.reference = reference;
        cardDisplays = new LinkedList<>();
        displayConnectorMap = new TreeMap<>();

        List<CardDisplay> initials = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            initials.add(new CardDisplay(i * 10, 40));
        }
        reference.setValue(initials);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                CardDisplay cd = dataSnapshot.getValue(CardDisplay.class);
                CardFBC cfc = new CardFBC(cd, dataSnapshot.getRef());
                cardDisplays.add(cd);

                displayConnectorMap.put(cd, cfc);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Handled by individual connectors.
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int idx = Integer.parseInt(Objects.requireNonNull(dataSnapshot.getKey()));
                CardDisplay removedDisplay = cardDisplays.remove(idx);
                CardFBC fbc = Objects.requireNonNull(displayConnectorMap.get(removedDisplay));
                fbc.detach();
                displayConnectorMap.remove(removedDisplay);


                reference.setValue(cardDisplays);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addCard(CardDisplay cd) {
        reference.child(String.valueOf(cardDisplays.size())).setValue(cd);
    }

    public List<CardDisplay> getCardDisplays() {
        return cardDisplays;
    }

    public Map<CardDisplay, CardFBC> getDisplayConnectorMap() {
        return displayConnectorMap;
    }
}
