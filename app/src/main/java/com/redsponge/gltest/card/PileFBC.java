package com.redsponge.gltest.card;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PileFBC implements Iterable<CardFBC>{

    private CardRoomFBC roomIn;
    private List<String> cardList;

    public PileFBC(CardRoomFBC roomIn, DatabaseReference ref) {
        this.roomIn = roomIn;
        cardList = new ArrayList<>();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cardList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    cardList.add(child.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @NonNull
    @Override
    public Iterator<CardFBC> iterator() {
        return new Iterator<CardFBC>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < cardList.size();
            }

            @Override
            public CardFBC next() {
                return roomIn.getCard(cardList.get(i++));
            }
        };
    }
}
