package com.redsponge.gltest.glscreen;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;

import com.google.firebase.database.FirebaseDatabase;
import com.redsponge.gltest.R;
import com.redsponge.gltest.card.CardDisplay;
import com.redsponge.gltest.card.CardFirebaseConnector;
import com.redsponge.gltest.gl.TextureBatch;
import com.redsponge.gltest.gl.Vector2;
import com.redsponge.gltest.gl.input.InputHandler;
import com.redsponge.gltest.gl.projection.FitViewport;
import com.redsponge.gltest.gl.texture.Texture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestScreen extends Screen implements InputHandler {

    private FitViewport viewport;
    private TextureBatch batch;
    private Texture texture;

    private List<CardDisplay> cardDisplays;
    private Map<CardDisplay, CardFirebaseConnector> connectors;
    private Texture cardFlipped, cardFront;

    private CardDisplay selectedDisplay;
    private long cardSelectionTime;
    private boolean isDragged;

    public TestScreen(Context context) {
        super(context);
    }

    @Override
    public void show() {
        viewport = new FitViewport(320, 180);
        viewport.centerCamera();

        connectors = new HashMap<>();

        batch = new TextureBatch();

        texture = new Texture(context.getResources(), R.drawable.icon);

        cardDisplays = new ArrayList<>();

        // TODO: Read initiail cards from DB!
        for (int i = 0; i < 20; i++) {
            cardDisplays.add(new CardDisplay(10 * i, 50));
            cardDisplays.add(new CardDisplay(10 * i, 50));
            cardDisplays.add(new CardDisplay(10 * i, 50));
            cardDisplays.add(new CardDisplay(10 * i, 50));
            cardDisplays.add(new CardDisplay(10 * i, 50));
            cardDisplays.add(new CardDisplay(10 * i, 50));
        }

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        for (int i = 0; i < cardDisplays.size(); i++) {
            CardDisplay cardDisplay = cardDisplays.get(i);
            connectors.put(cardDisplay, new CardFirebaseConnector(cardDisplay, db.getReference("rooms").child("heya").child("cards").child(i + "")));
        }

        cardFlipped = new Texture(context.getResources(), R.drawable.card_back);
        cardFront = new Texture(context.getResources(), R.drawable.card_front);

        cardFront.setMagFilter(Texture.TextureFilter.Nearest);
        cardFront.setMinFilter(Texture.TextureFilter.Nearest);

        cardFlipped.setMagFilter(Texture.TextureFilter.Nearest);
        cardFlipped.setMinFilter(Texture.TextureFilter.Nearest);
    }

    @Override
    public void render(float delta) {

        GLES30.glClearColor(0.5f, 0, 0, 1.0f);
        GLES30.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().getCombinedMatrix());

        batch.begin();
        batch.draw(texture, 30, 30, 80, 80);
        synchronized (this) {
            for (CardDisplay cardDisplay : cardDisplays) {
                batch.draw(cardDisplay.isFlipped() ? cardFlipped : cardFront, cardDisplay.getX(), cardDisplay.getY(), cardDisplay.getWidth(), cardDisplay.getHeight());
            }
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.resize(width, height);
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    @Override
    public void onTouch(float x, float y) {
        Vector2 inWorld = viewport.unproject(new Vector2(x, y), new Vector2());
        inWorld.y = viewport.getWorldHeight() - inWorld.y;

        System.out.println("HELLO! " + x + " " + y + " " + inWorld);
        if(selectedDisplay == null) {
            int idx = -1;
            for(int i = cardDisplays.size() - 1; i >= 0; i--) {
                CardDisplay cardDisplay = cardDisplays.get(i);
                System.out.println(cardDisplay.getX() + " " + cardDisplay.getY());
                if (cardDisplay.contains(inWorld) && !cardDisplay.isChosen()) {
                    System.out.println("SELECTING CARD " + i);
                    synchronized (this) {
                        selectCard(cardDisplay);
                    }
                    idx = i;
                    break;
                }
            }
            if (idx != -1) {
                // Move to front
                System.out.println("MOVING TO FRONT");
                cardDisplays.remove(idx);
                cardDisplays.add(selectedDisplay);
                connectors.get(selectedDisplay).pushUpdate();
            }
        }
    }

    private void selectCard(CardDisplay cardDisplay) {
        selectedDisplay = cardDisplay;
        cardSelectionTime = System.nanoTime();
        isDragged = false;
        cardDisplay.setChosen(true);
    }

    @Override
    public void onDrag(float x, float y) {
        Vector2 inWorld = viewport.unproject(new Vector2(x, y), new Vector2());
        inWorld.y = viewport.getWorldHeight() - inWorld.y;

        if(selectedDisplay != null) {
            System.out.println("DRAG!");
            selectedDisplay.setX(inWorld.x - selectedDisplay.getWidth() / 2f);
            selectedDisplay.setY(inWorld.y - selectedDisplay.getWidth() / 2f);
            isDragged = true;
            connectors.get(selectedDisplay).pushUpdate();
        }
    }

    @Override
    public void onRelease(float x, float y) {
        Vector2 inWorld = viewport.unproject(new Vector2(x, y), new Vector2());
        inWorld.y = viewport.getWorldHeight() - inWorld.y;

        if(selectedDisplay != null) {
            float dt = (System.nanoTime() - cardSelectionTime) / 1000000000f;
            System.out.println("TIME: " + dt);
            if(dt < 0.2f && !isDragged) {
                System.out.println("FLIPPING!");
                selectedDisplay.setFlipped(!selectedDisplay.isFlipped());
            }
            selectedDisplay.setChosen(false);
            connectors.get(selectedDisplay).pushUpdate();
            selectedDisplay = null;
        }
    }
}
