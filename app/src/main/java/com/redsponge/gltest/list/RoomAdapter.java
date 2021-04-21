package com.redsponge.gltest.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.redsponge.gltest.R;
import com.redsponge.gltest.room.ListRoomItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    private List<ListRoomItem> rooms;

    public RoomAdapter() {
        rooms = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(com.redsponge.gltest.R.layout.item_list_room, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListRoomItem item = rooms.get(position);
        holder.tvTitle.setText(item.getName());
        holder.tvUserCount.setText(String.format(Locale.UK, "0 / %d Players", item.getMaxPlayers()));
        holder.ivLock.setImageResource(item.isLocked() ? R.drawable.outline_lock_black_36 : R.drawable.outline_lock_open_black_36);
        holder.btnJoinRoom.setOnClickListener((v) -> {
            Toast.makeText(holder.btnJoinRoom.getContext(), "Clicked room " + item.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public void add(ListRoomItem item) {
        rooms.add(item);
        notifyDataSetChanged();
    }

    public void remove(ListRoomItem item) {
        rooms.remove(item);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private TextView tvUserCount;
        private Button btnJoinRoom;
        private ImageView ivLock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public TextView getTitleTextView() {
            return tvTitle;
        }

        public TextView getUserCountTextView() {
            return tvUserCount;
        }

        public Button getJoinRoomButton() {
            return btnJoinRoom;
        }

        public ImageView getLockImageView() {
            return ivLock;
        }
    }

}