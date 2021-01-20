package com.redsponge.gltest.card;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoomFBC {

    private final DatabaseReference roomReference;
    private FirebaseDatabase db;

    private Map<CardFBC, CardDisplay> connections;
    private List<CardDisplay> displays;
    private List<CardFBC> connectors;

    public RoomFBC(String roomName) {
        db = FirebaseDatabase.getInstance();
        roomReference = db.getReference("rooms").child(roomName);

        connectors = new ArrayList<>();
        displays = new ArrayList<>();


    }
}
