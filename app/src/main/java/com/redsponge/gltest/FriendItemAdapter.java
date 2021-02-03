package com.redsponge.gltest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FriendItemAdapter extends ArrayAdapter<Friend> {

    public FriendItemAdapter(Context context) {
        super(context, 0);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View myFriendItemView = LayoutInflater.from(getContext()).inflate(R.layout.friend_item, parent, false);
        Friend friend = getItem(position);

        TextView friendNameTV = myFriendItemView.findViewById(R.id.tvFriendName);
        friendNameTV.setText(friend.getName());

        return myFriendItemView;
    }
}
