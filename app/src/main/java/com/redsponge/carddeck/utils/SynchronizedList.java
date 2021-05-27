package com.redsponge.carddeck.utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class SynchronizedList<T> {

    private final DatabaseReference dbRef;
    private final List<Pair<String, T>> internalList; // Pairs of <FB Key, Value>
    private final Class<T> type;

    public SynchronizedList(DatabaseReference dbRef, Class<T> type) {
        this.dbRef = dbRef;
        this.internalList = new ArrayList<>();
        this.type = type;
        sync();
    }

    public String add(T value) {
        DatabaseReference newRef = dbRef.push();
        newRef.setValue(value);
        return newRef.getKey();
    }

    public void remove(String key) {
        dbRef.child(key).removeValue();
    }

    public void set(String key, T newVal) {
        dbRef.child(key).setValue(newVal);
    }

    public int indexOf(String key) {
        return positionFor(key);
    }

    private void sync() {
        dbRef.addChildEventListener(new ChildEventAdapter() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String prevChildName) {
                Pair<String, T> val = new Pair<>(dataSnapshot.getKey(), dataSnapshot.getValue(type));
                int pos = positionAfter(prevChildName);
                internalList.add(pos, val);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int i = positionFor(dataSnapshot.getKey());
                if(i > -1) {
                    internalList.remove(i);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String prevChildName) {
                int pos = positionFor(dataSnapshot.getKey());
                if(pos > -1) {
                    internalList.get(pos).first = dataSnapshot.getKey();
                    internalList.get(pos).second = dataSnapshot.getValue(type);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String prevChildName) {
                int curPos = positionFor(dataSnapshot.getKey());
                if(curPos > -1) {
                    Pair<String, T> data = internalList.get(curPos);
                    internalList.remove(curPos);

                    int newPos = positionAfter(prevChildName);
                    internalList.add(newPos, data);
                }
            }
        });
    }

    private int positionAfter(String prevChildName) {
        if(prevChildName == null) return 0;

        int i = positionFor(prevChildName);
        if(i == -1) {
            return internalList.size();
        }
        return i + 1;
    }

    private int positionFor(String prevChildName) {
        int listLen = internalList.size();
        for(int i = 0; i < listLen; i++) {
            if(internalList.get(i).first.equals(prevChildName)) return i;
        }
        return -1;
    }

}
