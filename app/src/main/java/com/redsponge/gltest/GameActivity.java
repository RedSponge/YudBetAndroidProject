package com.redsponge.gltest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;

import androidx.annotation.Nullable;

import com.redsponge.gltest.card.Constants;
import com.redsponge.gltest.gl.GLView;
import com.redsponge.gltest.gl.RawReader;
import com.redsponge.gltest.glscreen.GameScreen;

public class GameActivity extends Activity {

    private GLView view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();

        Log.i("GameActivity", "Wassup!");
//        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
//            Log.i("GameActivity", "Is Fullscreen Now: " + ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0));
//        });
        RawReader.resources = getResources();

        String roomName = getIntent().getStringExtra(Constants.ROOM_NAME_EXTRA);

        view = new GLView(this);
        view.setPendingScreen(new GameScreen(this, roomName));
        setContentView(view);
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

}
