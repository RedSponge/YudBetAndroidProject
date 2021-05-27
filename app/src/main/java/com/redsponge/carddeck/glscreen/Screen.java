package com.redsponge.carddeck.glscreen;

import android.content.Context;

public abstract class Screen {

    protected Context context;

    public Screen(Context context) {
        this.context = context;
    }

    public abstract void show();

    public abstract void render(float delta);

    public abstract void resize(int width, int height);

    public abstract void hide();

    public abstract void dispose();

    public abstract void onAndroidEvent(int eventId);
}
