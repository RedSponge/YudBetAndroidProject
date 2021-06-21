package com.redsponge.carddeck.room;

import com.google.firebase.database.Exclude;

public class ListRoomItem {

    private String name;
    private int maxPlayers;
    private String hashedPassword;
    private int playerCount;

    public ListRoomItem(String name, int maxPlayers, String hashedPassword, int playerCount) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.hashedPassword = hashedPassword;
        this.playerCount = playerCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public boolean isLocked() {
        return !hashedPassword.isEmpty();
    }

    @Exclude
    public int getPlayerCount() {
        return playerCount;
    }

    @Exclude
    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    @Override
    public String toString() {
        return "ListRoomItem{" +
                "name='" + name + '\'' +
                ", maxPlayers=" + maxPlayers +
                ", hashedPassword='" + hashedPassword + '\'' +
                ", playerCount=" + playerCount +
                '}';
    }
}
