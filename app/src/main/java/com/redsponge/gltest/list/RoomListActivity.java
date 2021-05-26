package com.redsponge.gltest.list;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.transition.Visibility;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redsponge.gltest.GameActivity;
import com.redsponge.gltest.R;
import com.redsponge.gltest.auth.AuthActivity;
import com.redsponge.gltest.card.Constants;
import com.redsponge.gltest.room.ListRoomItem;
import com.redsponge.gltest.utils.Utils;

public class RoomListActivity extends Activity {

    private RecyclerView rvRoomList;
    private FirebaseDatabase db;
    private RoomAdapter adapter;
    private SwipeRefreshLayout srlList;

    private FloatingActionButton fabSettings;
    private FloatingActionButton fabLogout;
    private boolean isSettingsFabOpen;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        srlList = findViewById(R.id.srlList);

        rvRoomList = findViewById(R.id.rvRoomList);

        fabSettings = findViewById(R.id.fabSettings);
        fabLogout = findViewById(R.id.fabLogout);

        rvRoomList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RoomAdapter();

        rvRoomList.setAdapter(adapter);

        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        srlList.setOnRefreshListener(this::loadRooms);
        loadRooms();

        isSettingsFabOpen = false;

        fabLogout.setVisibility(View.INVISIBLE);
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
                    ListRoomItem item = new ListRoomItem(child.getKey(),
                            child.child(Constants.MAX_PLAYERS_REFERENCE).getValue(Integer.class),
                            Utils.readReferenceIfExists(child.child(Constants.PASSWORD_REFERENCE), String.class, ""),
                            child.child(Constants.ROOM_PLAYERS_REFERENCE).exists() ? (int) child.child(Constants.ROOM_PLAYERS_REFERENCE).getChildrenCount() : 0);

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
        db.getReference(Constants.ROOMS_REFERENCE).child(roomItem.getName()).child(Constants.ROOM_PLAYERS_REFERENCE)
                .child(auth.getCurrentUser().getUid()).child(Constants.PLAYER_CARDS_REFERENCE).child("placeholder").setValue("placeholder");

        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(Constants.ROOM_NAME_EXTRA, roomItem.getName());
        startActivity(i);
    }

    private static void rotateFab(View fab, boolean rotate) {
        fab.animate().setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                }).rotation(rotate ? 135 : 0);
    }

    public void toggleSettingsFab(View view) {
        rotateFab(fabSettings, isSettingsFabOpen = !isSettingsFabOpen);
        if (isSettingsFabOpen) {
            showSettings();
        } else {
            hideSettings();
        }
    }

    public void showSettings() {
        fabLogout.setVisibility(View.VISIBLE);
        fabLogout.setAlpha(0f);
        fabLogout.setTranslationX(0);
        fabLogout.animate()
                .setDuration(200)
                .translationY(-1.5f * fabLogout.getHeight())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .alpha(1)
                .start();
    }

    public void hideSettings() {
        fabLogout.setVisibility(View.VISIBLE);
        fabLogout.setAlpha(1f);
        fabLogout.setTranslationX(0);
        fabLogout.animate()
                .setDuration(200)
                .translationY(-.5f * fabLogout.getHeight())
                .alpha(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fabLogout.setVisibility(View.INVISIBLE);
                        super.onAnimationEnd(animation);
                    }
                })
                .start();
    }

    public void logout(View view) {
        auth.signOut();
        finish();
        startActivity(new Intent(this, AuthActivity.class));
    }
}
