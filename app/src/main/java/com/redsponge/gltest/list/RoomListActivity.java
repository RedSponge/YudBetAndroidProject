package com.redsponge.gltest.list;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.redsponge.gltest.R;
import com.redsponge.gltest.room.ListRoomItem;

public class RoomListActivity extends Activity {

    private RecyclerView rvRoomList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        rvRoomList = findViewById(R.id.rvRoomList);

        rvRoomList.setLayoutManager(new LinearLayoutManager(this));
        RoomAdapter adapter = new RoomAdapter();
        adapter.add(new ListRoomItem("Epic", 6, false));
        adapter.add(new ListRoomItem("Cool", 2, true));
        adapter.add(new ListRoomItem("Noder", 8, false));

        rvRoomList.setAdapter(adapter);

        for (int i = 0; i < 20; i++) {
            adapter.add(new ListRoomItem("Room#" + i, i, i % 2 == 0));
        }
    }
}
