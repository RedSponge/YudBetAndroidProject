package com.redsponge.carddeck.card;

public class Constants {

    public static final String CARDS_REFERENCE = "cards";
    public static final String PILE_ORDER_REFERENCE = "pile_order";
    public static final String ROOM_MAX_PLAYERS_REFERENCE = "maxPlayers";
    public static final String ROOMS_REFERENCE = "rooms";
    public static final String PASSWORD_REFERENCE = "hashedPassword";
    public static final String ROOM_NAME_EXTRA = "room_name";
    public static final String PILES_REFERENCE = "piles";
    public static final String TRANSFORM_REFERENCE = "transform";

    public static final String TEXTURE_FLIPPED = "flipped";
    public static final String TEXTURE_BACKGROUND = "background";
    public static final String TEXTURE_HAND_BACKGROUND = "hand_background";

    public static final String USERS_REFERENCE = "users";
    public static final String ROOM_PLAYERS_REFERENCE = "players";
    public static final String PLAYER_CARDS_REFERENCE = "cards";

    public static final String USER_CURRENT_ROOM_REFERENCE = "currentRoom";

    public static final String[] SUITS = new String[] {"spade", "heart", "diamond", "club"};

    public static final int CARD_MIN = 1;
    public static final int CARD_MAX = 13;

    public static final float PILE_HOLD_MOVE_TIME = 0.3f;

    public static final int CARD_WIDTH = 16 * 2;
    public static final int CARD_HEIGHT = 24 * 2;
    public static final int PILE_MAX_DRAWN_HEIGHT = 10;
    ;

    public static final float MAX_CHOICE_TIME = 0.1f;
    public static final float SPLIT_MAX_TIME = 0.05f;

    public static final float MIN_CARD_SPLIT_DST = 40;
    public static final float MIN_CARD_SPLIT_DST2 = MIN_CARD_SPLIT_DST * MIN_CARD_SPLIT_DST;

    public static final float MIN_PILE_MERGE_DST = 20;
    public static final float MIN_PILE_MERGE_DST2 = MIN_PILE_MERGE_DST * MIN_PILE_MERGE_DST;

    public static final int TOGGLE_HAND_EVENT = 1;
    public static final int SHAKE_EVENT = 2;

    public static final int HAND_DISPLAY_MARGIN = 20;
    public static final int HAND_ANIMATION_HEIGHT = 45;
    public static final int HAND_ROW_LENGTH = 26;
    public static final int HAND_BACKGROUND_BOTTOM_Y = -260;

    // Queries
    public static final int QUERY_PLAYER_AMOUNT = 1;
}
