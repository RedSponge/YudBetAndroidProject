package com.redsponge.gltest.glscreen;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.redsponge.gltest.R;
import com.redsponge.gltest.card.CardDisplay;
import com.redsponge.gltest.card.CardFBC;
import com.redsponge.gltest.card.CardRoomFBC;
import com.redsponge.gltest.card.CardTextures;
import com.redsponge.gltest.gl.TextureBatch;
import com.redsponge.gltest.gl.Vector2;
import com.redsponge.gltest.gl.input.InputHandler;
import com.redsponge.gltest.gl.projection.FitViewport;
import com.redsponge.gltest.gl.texture.Texture;

public class GameScreen extends Screen implements InputHandler {

    private FitViewport viewport;
    private TextureBatch batch;

    private CardTextures cardTextures;

    private final CardRoomFBC cardRoomFBC;

    private CardFBC selectedFBC;
    private long cardSelectionTime;
    private boolean isDragged;

    private final String roomName;

    public GameScreen(Context context, String roomName) {
        super(context);
        this.roomName = roomName;
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        cardRoomFBC = new CardRoomFBC(db.getReference("rooms/" + roomName));
    }

    @Override
    public void show() {
        viewport = new FitViewport(320*1.5f, 180*1.5f);
        viewport.centerCamera();

        batch = new TextureBatch();


        cardTextures = new CardTextures(context);
    }

    @Override
    public void render(float delta) {

        GLES30.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        GLES30.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().getCombinedMatrix());

        batch.begin();
        if(cardRoomFBC.isFullyLoaded()) {
            synchronized (cardRoomFBC) {
                for (CardFBC fbc : cardRoomFBC) {
                    CardDisplay cardDisplay = fbc.getDisplay();
                    cardDisplay.updateDrawnPos(delta);

                    float x = cardDisplay.getDrawnX() + cardDisplay.getWidth() / 2f;
                    float y = cardDisplay.getDrawnY() + cardDisplay.getHeight() / 2f;
                    float w = cardDisplay.getWidth() * cardDisplay.getDrawnScale();
                    float h = cardDisplay.getHeight() * cardDisplay.getDrawnScale();

                    batch.draw(cardTextures.get(cardDisplay.getType(), cardDisplay.isFlipped()), x - w / 2f, y - h / 2f, w, h);
                }
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
        if(selectedFBC == null) {
            if(cardRoomFBC.isFullyLoaded()) {
                CardFBC finalChosen = null;

                for (CardFBC fbc : cardRoomFBC) {
                    CardDisplay cardDisplay = fbc.getDisplay();
                    System.out.println(cardDisplay.getX() + " " + cardDisplay.getY());
                    if (cardDisplay.contains(inWorld) && !cardDisplay.isChosen()) {
                        System.out.println("SELECTING CARD");
                        finalChosen = fbc;
                    }
                }

                if(finalChosen != null) {
                    synchronized (this) {
                        selectFBC(finalChosen);
                        cardRoomFBC.pushToFront(finalChosen);
                    }
                }
            }
        }
    }

    private void selectFBC(CardFBC cardDisplay) {
        selectedFBC = cardDisplay;
        cardSelectionTime = System.nanoTime();
        isDragged = false;
        cardDisplay.getDisplay().setChosenTime(System.currentTimeMillis());
        cardDisplay.pushUpdate();
    }

    @Override
    public void onDrag(float x, float y) {
        Vector2 inWorld = viewport.unproject(new Vector2(x, y), new Vector2());
        inWorld.y = viewport.getWorldHeight() - inWorld.y;

        if(selectedFBC != null) {
            System.out.println("DRAG!");
            selectedFBC.getDisplay().setX(inWorld.x - selectedFBC.getDisplay().getWidth() / 2f);
            selectedFBC.getDisplay().setY(inWorld.y - selectedFBC.getDisplay().getWidth() / 2f);
            selectedFBC.getDisplay().setChosenTime(System.currentTimeMillis());
            isDragged = true;
            selectedFBC.pushUpdate();
        }
    }

    @Override
    public void onRelease(float x, float y) {
        Vector2 inWorld = viewport.unproject(new Vector2(x, y), new Vector2());
        inWorld.y = viewport.getWorldHeight() - inWorld.y;

        if(selectedFBC != null) {
            float dt = (System.nanoTime() - cardSelectionTime) / 1000000000f;
            System.out.println("TIME: " + dt);
            if(dt < 0.2f && !isDragged) {
                System.out.println("FLIPPING!");
                selectedFBC.getDisplay().setFlipped(!selectedFBC.getDisplay().isFlipped());
            }
            selectedFBC.getDisplay().setChosenTime(0);
            selectedFBC.pushUpdate();
            selectedFBC = null;
        }
    }
}
