package com.redsponge.gltest.list;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.redsponge.gltest.R;
import com.redsponge.gltest.room.ListRoomItem;

public class RoomListActivity extends Activity {

    private RecyclerView rvRoomList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        rvRoomList = findViewById(R.id.rvRoomList);

        RoomAdapter adapter = new RoomAdapter();
        rvRoomList.setAdapter(adapter);
        adapter.add(new ListRoomItem("Epic", 6, false));
        adapter.add(new ListRoomItem("Cool", 2, true));
        adapter.add(new ListRoomItem("Noder", 8, false));
    }
}
