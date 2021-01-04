package com.redsponge.gltest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.redsponge.gltest.gl.GLView;
import com.redsponge.gltest.gl.RawReader;
import com.redsponge.gltest.glscreen.TestScreen;

public class MainActivity extends Activity {

    private GLView view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        RawReader.resources = getResources();
        super.onCreate(savedInstanceState);

        view = new GLView(this);
        view.setPendingScreen(TestScreen.class);
        setContentView(view);
    }
}
