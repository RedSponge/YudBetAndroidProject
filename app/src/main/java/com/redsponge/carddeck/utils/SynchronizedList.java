package com.redsponge.carddeck.utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SynchronizedList<T> implements Iterable<T> {

    private final DatabaseReference dbRef;
    private final List<Pair<String, T>> internalList; // Pairs of <FB Key, Value>
    private final Class<T> type;
    private ChildEventAdapter listener;

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

    public void removeIndex(int idx) {
        dbRef.child(internalList.get(idx).first).removeValue();
    }

    public void removeKey(String key) {
        dbRef.child(key).removeValue();
    }

    public void removeValue(T value) {
        String keyOfVal = getKeyOfVal(value);
        if(keyOfVal != null) {
            dbRef.child(keyOfVal).removeValue();
        }
    }

    public void set(String key, T newVal) {
        dbRef.child(key).setValue(newVal);
    }

    public int indexOf(String key) {
        return positionFor(key);
    }

    public void addAll(Iterable<T> toAdd) {
        HashMap<String, Object> valsToAdd = new HashMap<>();
        for (T value : toAdd) {
            valsToAdd.put(dbRef.push().getKey(), value);
        }
        dbRef.updateChildren(valsToAdd);
    }

    public T get(int idx) {
        if(0 <= idx && idx < internalList.size()) {
            return internalList.get(idx).second;
        }
        throw new RuntimeException("Invalid index " + idx);
    }

    public T get(String key) {
        return get(indexOf(key));
    }

    public int size() {
        return internalList.size();
    }

    private String getKeyOfVal(T value) {
        int len = internalList.size();
        for(int i = 0; i < len; i++) {
            if(internalList.get(i).second.equals(value)) {
                return internalList.get(i).first;
            }
        }
        return null;
    }

    private void sync() {
        dbRef.addChildEventListener(listener = new ChildEventAdapter() {
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

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < internalList.size();
            }

            @Override
            public T next() {
                return internalList.get(i++).second;
            }
        };
    }

    public void detach() {
        dbRef.removeEventListener(listener);
    }
}
