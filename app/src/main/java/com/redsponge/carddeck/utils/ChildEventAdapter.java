package com.redsponge.carddeck.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class ChildEventAdapter implements ChildEventListener {

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String prevChildName) {

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String prevChildName) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String prevChildName) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
