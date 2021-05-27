package com.redsponge.carddeck.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public final class Listeners {

    public static ValueEventListener value(ValEventListenerLambda listener) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listener.onValue(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Listeners", databaseError.toString());
            }
        };
    }

    @FunctionalInterface
    public interface ValEventListenerLambda {
        void onValue(DataSnapshot dataSnapshot);
    }


}
