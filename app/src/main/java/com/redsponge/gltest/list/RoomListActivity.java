package com.redsponge.gltest.list;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redsponge.gltest.GameActivity;
import com.redsponge.gltest.R;
import com.redsponge.gltest.card.Constants;
import com.redsponge.gltest.room.ListRoomItem;
import com.redsponge.gltest.utils.Utils;

public class RoomListActivity extends Activity {

    private RecyclerView rvRoomList;
    private FirebaseDatabase db;
    private RoomAdapter adapter;
    private SwipeRefreshLayout srlList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        srlList = findViewById(R.id.srlList);

        rvRoomList = findViewById(R.id.rvRoomList);

        rvRoomList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RoomAdapter();

        rvRoomList.setAdapter(adapter);

        db = FirebaseDatabase.getInstance();

        srlList.setOnRefreshListener(this::loadRooms);
        loadRooms();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRooms();
    }

    private void loadRooms() {
        db.getReference("rooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    ListRoomItem item = new ListRoomItem(child.getKey(), child.child(Constants.MAX_PLAYERS_REFERENCE).getValue(Integer.class), child.child(Constants.PASSWORD_REFERENCE).getValue(String.class));
                    adapter.add(item);
                }
                srlList.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void openCreateRoomScreen(View view) {
        Intent i = new Intent(this, CreateRoomActivity.class);
        startActivity(i);
    }

    public void tryJoinRoom(ListRoomItem roomItem) {
        if(roomItem.isLocked()) {
            EditText passwordInput = new EditText(this);
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            new AlertDialog.Builder(this)
                    .setView(passwordInput)
                    .setMessage("Enter Password:")
                    .setTitle("This room is locked!")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Join", (dialog, which) -> {
                        String pwInput = passwordInput.getText().toString();
                        if(!Utils.hashPassword(pwInput).equals(roomItem.getHashedPassword())) {
                            Toast.makeText(this, "Incorrect Password!", Toast.LENGTH_SHORT).show();
                        } else {
                            joinRoom(roomItem);
                        }
                    })
                    .show();
        } else {
            joinRoom(roomItem);
        }
    }

    private void joinRoom(ListRoomItem roomItem) {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(Constants.ROOM_NAME_EXTRA, roomItem.getName());
        startActivity(i);
    }
}
