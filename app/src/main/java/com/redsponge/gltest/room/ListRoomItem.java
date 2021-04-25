package com.redsponge.gltest.room;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;
import com.redsponge.gltest.card.Constants;

public class ListRoomItem {

    @Exclude
    private String name;

    @PropertyName(Constants.MAX_PLAYERS_REFERENCE)
    private int maxPlayers;

    @PropertyName("password")
    private String hashedPassword;

    public ListRoomItem() {
    }

    public ListRoomItem(String name, int maxPlayers, String hashedPassword) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.hashedPassword = hashedPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PropertyName(Constants.MAX_PLAYERS_REFERENCE)
    public int getMaxPlayers() {
        return maxPlayers;
    }

    @PropertyName(Constants.MAX_PLAYERS_REFERENCE)
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    @PropertyName("password")
    public String getHashedPassword() {
        return hashedPassword;
    }

    @PropertyName("password")
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    @Exclude
    public boolean isLocked() {
        return !hashedPassword.isEmpty();
    }
}
