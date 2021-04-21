package com.redsponge.gltest.room;

public class ListRoomItem {

    private String name;
    private int maxPlayers;
    private boolean isLocked;

    public ListRoomItem(String name, int maxPlayers, boolean isLocked) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.isLocked = isLocked;
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

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}
