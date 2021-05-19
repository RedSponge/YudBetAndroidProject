package com.redsponge.gltest.card;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.redsponge.gltest.utils.Listeners;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PileFBC implements Iterable<CardFBC>{

    private final RoomFBC roomIn;
    private final List<String> cardList;
    private final DatabaseReference ref;
    private final ValueEventListener listener;

    private PileData drawData;

    public PileFBC(RoomFBC roomIn, DatabaseReference ref) {
        this.roomIn = roomIn;
        this.cardList = new ArrayList<>();
        this.ref = ref;
        this.drawData = new PileData();

        ref.child(Constants.TRANSFORM_REFERENCE).addValueEventListener(Listeners.value(data -> drawData = data.getValue(PileData.class)));

        ref.child(Constants.CARDS_REFERENCE).addValueEventListener(listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                synchronized (roomIn) {
                    cardList.clear();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        cardList.add(child.getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public PileData getData() {
        return drawData;
    }

    public void pushUpdate() {
        ref.setValue(cardList);
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

    public CardFBC getTopCard() {
        return roomIn.getCard(cardList.get(0));
    }

    public void detach() {
        ref.removeEventListener(listener);
    }

    public List<String> getCardOrder() {
        return cardList;
    }

    public DatabaseReference getReference() {
        return ref;
    }

    public boolean hasTopCard() {
        return cardList.size() > 0 && roomIn.getCard(cardList.get(0)) != null;
    }
}
