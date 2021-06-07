package com.redsponge.carddeck;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.redsponge.carddeck.card.Constants;
import com.redsponge.carddeck.gl.GLGameView;
import com.redsponge.carddeck.gl.RawReader;
import com.redsponge.carddeck.glscreen.GameScreen;

public class GameActivity extends Activity  {

    private GLGameView glView;
    private SensorManager sensorManager;
    private float accelLast, accelCurrent, accel;
    private long lastShakeTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();

        setContentView(R.layout.activity_game);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        registerShakeSensor();
        accelLast = SensorManager.GRAVITY_EARTH;
        accelCurrent = SensorManager.GRAVITY_EARTH;

        Log.i("GameActivity", "Wassup!");
//        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
//            Log.i("GameActivity", "Is Fullscreen Now: " + ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0));
//        });
        RawReader.resources = getResources();

        String roomName = getIntent().getStringExtra(Constants.ROOM_NAME_EXTRA);

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
//        glView.getScreen().onAndroidEvent(Constants.SHAKE_EVENT);
    }
}
