package com.redsponge.carddeck.card;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class CardFBC {

    private final CardData cardData;
    private final DatabaseReference dbReference;
    private final ValueEventListener fbListener;

    public CardFBC(CardData cardData, DatabaseReference dbReference) {
        this.cardData = cardData;
        this.dbReference = dbReference;
        dbReference.addValueEventListener(fbListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CardData cd = dataSnapshot.getValue(CardData.class);
                if(cd != null) cardData.set(cd);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        pushUpdate();
    }

    public void detach() {
        dbReference.removeEventListener(fbListener);
    }

    public void pushUpdate() {
        dbReference.setValue(cardData);
    }

    public CardData getData() {
        return cardData;
    }

    public DatabaseReference getReference() {
        return dbReference;
    }
}
