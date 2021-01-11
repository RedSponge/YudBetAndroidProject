package com.redsponge.gltest.card;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class CardFirebaseConnector {

    private CardDisplay cardDisplay;
    private final DatabaseReference dbReference;

    public CardFirebaseConnector(CardDisplay cardDisplay, DatabaseReference dbReference) {
        this.cardDisplay = cardDisplay;
        this.dbReference = dbReference;
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cardDisplay.set(Objects.requireNonNull(dataSnapshot.getValue(CardDisplay.class)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        pushUpdate();
    }

    public void pushUpdate() {
        dbReference.setValue(cardDisplay);
    }
}
