package com.redsponge.gltest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.redsponge.gltest.gl.GLView;
import com.redsponge.gltest.gl.RawReader;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        RawReader.resources = getResources();
        super.onCreate(savedInstanceState);

        setContentView(new GLView(this));
    }
}
