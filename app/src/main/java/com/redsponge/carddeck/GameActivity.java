package com.redsponge.carddeck;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.redsponge.carddeck.card.Constants;
import com.redsponge.carddeck.gl.GLGameView;
import com.redsponge.carddeck.gl.RawReader;
import com.redsponge.carddeck.glscreen.GameScreen;

public class GameActivity extends Activity  {

    private GLGameView glView;
    private SensorManager sensorManager;
    private float accelLast, accelCurrent, accel;
    private long lastShakeTime;

    private String roomName;

    private FirebaseDatabase db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();


        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        setContentView(R.layout.activity_game);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        registerShakeSensor();
        accelLast = SensorManager.GRAVITY_EARTH;
        accelCurrent = SensorManager.GRAVITY_EARTH;

        RawReader.resources = getResources();

        roomName = getIntent().getStringExtra(Constants.ROOM_NAME_EXTRA);

        glView = findViewById(R.id.glView);
        glView.setPendingScreen(new GameScreen(this, roomName));

    }

    private void registerShakeSensor() {
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                long currentTime = System.nanoTime();
                if((currentTime - lastShakeTime) / 1000000000f < 0.2f) return;
                lastShakeTime = currentTime;
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                accelLast = accelCurrent;
                accelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));

                float delta = accelCurrent - accelLast;
                accel = accel * .9f + delta;
                if(accel > 12) {
                    glView.getScreen().onAndroidEvent(Constants.SHAKE_EVENT);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
              | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
              // Set the content to appear under the system bars so that the
              // content doesn't resize when the system bars hide and show.
              | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
              | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
              | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
              // Hide the nav bar and status bar
              | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
              | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public void toggleHand(View view) {
        glView.getScreen().onAndroidEvent(Constants.TOGGLE_HAND_EVENT);
    }

    public void exitRoomOnClick(View view) {
        exitRoom();
    }

    public void exitRoom() {
        if((Integer) glView.getScreen().queryData(Constants.QUERY_PLAYER_AMOUNT) == 1) {
            new AlertDialog.Builder(this)
                    .setTitle("Last Player!")
                    .setMessage("You are the last player to exit the room, delete it?")
                    .setPositiveButton("Yes!", (dialog, option) -> {
                        exitScreen(true);
                    })
                    .setNegativeButton("No!", (dialog, option) -> {
                        exitScreen(false);
                    })
                    .setNeutralButton("Cancel", null)
                    .show();
        } else {
            exitScreen(false);
        }
    }

    public void exitScreen(boolean deleteRoom) {
        glView.getScreen().dispose();
        if(deleteRoom) {
            db.getReference(Constants.ROOMS_REFERENCE).child(roomName).removeValue();
        }
        db.getReference(Constants.USERS_REFERENCE).child(auth.getCurrentUser().getUid()).child(Constants.USER_CURRENT_ROOM_REFERENCE).removeValue();
        finish();
    }

    @Override
    public void onBackPressed() {
        exitRoom();
    }
}
