package com.redsponge.gltest;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;

public class EpicActivity extends Activity {

    private ListView lvEpic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.epic_activity);

        lvEpic = findViewById(R.id.lvEpic);
    }
}
