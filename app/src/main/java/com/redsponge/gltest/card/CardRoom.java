package com.redsponge.gltest.card;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.redsponge.gltest.gl.TextureBatch;

import java.util.ArrayList;
import java.util.List;

public class CardRoom {

    private final List<CardDisplay> cardDisplays;
    private final String roomName;
    private final FirebaseDatabase database;
    private final DatabaseReference roomReference;

    public CardRoom(String roomName) {
        this.database = FirebaseDatabase.getInstance();
        this.roomName = roomName;
        this.cardDisplays = new ArrayList<>();
        roomReference = this.database.getReference("rooms").child(roomName).child("cards");
        connectToFirebase();
    }

    private void connectToFirebase() {
        roomReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e("CardRoom", "ADD: " + s);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e("CardRoom", "CHANGE: " + s);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.e("CardRoom", "REM: " + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e("CardRoom", "MV: " + s);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("CardRoom", "Failed to fetch child data!", databaseError.toException());
            }
        });
    }

    public void render(TextureBatch batch, CardTextures textures) {
        for (CardDisplay cardDisplay : cardDisplays) {
            batch.draw(textures.get(cardDisplay.getType(), cardDisplay.isFlipped()), cardDisplay.getX(), cardDisplay.getY(), cardDisplay.getWidth(), cardDisplay.getHeight());
        }
    }
}
