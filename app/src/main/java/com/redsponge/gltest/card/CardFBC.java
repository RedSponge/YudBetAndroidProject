package com.redsponge.gltest.card;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class CardFBC {

    private CardDisplay cardDisplay;
    private final DatabaseReference dbReference;
    private ValueEventListener listener;

    public CardFBC(CardDisplay cardDisplay, DatabaseReference dbReference) {
        this.cardDisplay = cardDisplay;
        this.dbReference = dbReference;
        dbReference.addValueEventListener(listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CardDisplay cd = dataSnapshot.getValue(CardDisplay.class);
                if(cd != null) cardDisplay.set(cd);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        pushUpdate();
    }

    public void detach() {
        dbReference.removeEventListener(listener);
    }

    public void pushUpdate() {
        dbReference.setValue(cardDisplay);
    }
}
