package com.redsponge.carddeck.room;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redsponge.carddeck.R;
import com.redsponge.carddeck.card.CardData;
import com.redsponge.carddeck.card.Constants;
import com.redsponge.carddeck.card.PileData;
import com.redsponge.carddeck.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CreateRoomActivity extends Activity {

    private EditText etRoomName;
    private EditText etMaxPlayers;
    private EditText etPassword;
    private ProgressBar pbLoading;
    private Button btnCreateRoom;

    private FirebaseDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_room);

        etRoomName = findViewById(R.id.etRoomName);
        etMaxPlayers = findViewById(R.id.etMaxPlayers);
        etPassword = findViewById(R.id.etPassword);

        pbLoading = findViewById(R.id.pbLoading);
        btnCreateRoom = findViewById(R.id.btnCreateRoom);

        db = FirebaseDatabase.getInstance();

        unlockUI();
    }

    public void tryCreateRoom(View view) {
        lockUI();

        String roomName = etRoomName.getText().toString();
        int maxPlayers = Utils.tryParseInt(etMaxPlayers.getText().toString(), -1);
        String password = etPassword.getText().toString();

        if(Utils.isBlankOrNull(roomName) || !Utils.isFirebaseValidPathString(roomName)) {
            etRoomName.setError("Invalid Room Name!");
            unlockUI();
            return;
        }

        if(maxPlayers <= 0) {
            etMaxPlayers.setError("Invalid Number!");
            unlockUI();
            return;
        }

        db.getReference().child(Constants.ROOMS_REFERENCE).child(roomName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    etRoomName.setError("The Room Name is Taken!");
                    unlockUI();
                    return;
                }

                ListRoomItem roomItem = new ListRoomItem(roomName, maxPlayers, Utils.isBlankOrNull(password) ? "" : Utils.hashPassword(password), 0);
                db.getReference().child(Constants.ROOMS_REFERENCE).child(roomName).setValue(roomItem);
                createRoom(db.getReference().child(Constants.ROOMS_REFERENCE).child(roomName));
                Toast.makeText(CreateRoomActivity.this, "Successfully Created Room!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void createRoom(DatabaseReference roomRef) {
        List<String> cardIds = new ArrayList<>();
        for (String suit : Constants.SUITS) {
            for (int i = Constants.CARD_MIN; i <= Constants.CARD_MAX; i++) {
                DatabaseReference cardRef = roomRef.child(Constants.CARDS_REFERENCE).push();
                cardRef.setValue(new CardData(true, suit, i));
                cardIds.add(cardRef.getKey());
            }
        }

        DatabaseReference initialPileRef = roomRef.child(Constants.PILES_REFERENCE).push();
        initialPileRef.child(Constants.TRANSFORM_REFERENCE).setValue(new PileData(320 * 1.5f / 2f, 180 * 1.5f / 2f, 0));
        for (String cardId : cardIds) {
            initialPileRef.child(Constants.CARDS_REFERENCE).push().setValue(cardId);
        }

        roomRef.child(Constants.PILE_ORDER_REFERENCE).push().setValue(initialPileRef.getKey());
    }

    private void lockUI() {
        pbLoading.setVisibility(View.VISIBLE);
        btnCreateRoom.setEnabled(false);
    }

    private void unlockUI() {
        pbLoading.setVisibility(View.INVISIBLE);
        btnCreateRoom.setEnabled(true);
    }
}
