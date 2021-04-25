package com.redsponge.gltest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.redsponge.gltest.card.Constants;
import com.redsponge.gltest.gl.GLView;
import com.redsponge.gltest.gl.RawReader;
import com.redsponge.gltest.glscreen.TestScreen;

public class GameActivity extends Activity {

    private GLView view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RawReader.resources = getResources();

        String roomName = getIntent().getStringExtra(Constants.ROOM_NAME_EXTRA);

        view = new GLView(this);
        view.setPendingScreen(new TestScreen(this, roomName));
        setContentView(view);
    }
}
