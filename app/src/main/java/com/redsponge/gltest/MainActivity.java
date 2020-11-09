package com.redsponge.gltest;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.redsponge.gltest.gl.GLView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new GLView(this));
    }
}
