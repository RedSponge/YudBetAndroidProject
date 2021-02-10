package com.redsponge.gltest.glscreen;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;

import com.google.firebase.database.FirebaseDatabase;
import com.redsponge.gltest.R;
import com.redsponge.gltest.card.CardDisplay;
import com.redsponge.gltest.card.CardFBC;
import com.redsponge.gltest.card.CardRoomFBC;
import com.redsponge.gltest.gl.TextureBatch;
import com.redsponge.gltest.gl.Vector2;
import com.redsponge.gltest.gl.input.InputHandler;
import com.redsponge.gltest.gl.projection.FitViewport;
import com.redsponge.gltest.gl.texture.Texture;

public class TestScreen extends Screen implements InputHandler {

    private FitViewport viewport;
    private TextureBatch batch;
    private Texture texture;

    private CardRoomFBC cardRoomFBC;
    private Texture cardFlipped, cardFront;

    private CardFBC selectedFBC;
    private long cardSelectionTime;
    private boolean isDragged;

    public TestScreen(Context context) {
        super(context);
    }

    @Override
    public void show() {
        viewport = new FitViewport(320, 180);
        viewport.centerCamera();

        batch = new TextureBatch();
        texture = new Texture(context.getResources(), R.drawable.icon);


        // TODO: Read initial cards from DB!

        FirebaseDatabase db = FirebaseDatabase.getInstance();

        CardRoomFBC.initialzieRoom(db.getReference("rooms/heya"));
        cardRoomFBC = new CardRoomFBC(db.getReference("rooms/heya"));

        cardFlipped = new Texture(context.getResources(), R.drawable.card_back);
        cardFront = new Texture(context.getResources(), R.drawable.suit1);

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
        if(cardRoomFBC.isFullyLoaded()) {
            synchronized (this) {
                for (CardFBC fbc : cardRoomFBC) {
                    CardDisplay cardDisplay = fbc.getDisplay();
                    cardDisplay.updateDrawnPos(delta);

                    float x = cardDisplay.getDrawnX() + cardDisplay.getWidth() / 2f;
                    float y = cardDisplay.getDrawnY() + cardDisplay.getHeight() / 2f;
                    float w = cardDisplay.getWidth() * cardDisplay.getDrawnScale();
                    float h = cardDisplay.getHeight() * cardDisplay.getDrawnScale();

                    if(w != cardDisplay.getWidth()) {
                        System.out.println("Different width yum yum! " + w);
                    }

                    batch.draw(cardDisplay.isFlipped() ? cardFlipped : cardFront, x - w / 2f, y - h / 2f, w, h);
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
                for (CardFBC fbc : cardRoomFBC) {
                    CardDisplay cardDisplay = fbc.getDisplay();
                    System.out.println(cardDisplay.getX() + " " + cardDisplay.getY());
                    if (cardDisplay.contains(inWorld) && !cardDisplay.isChosen()) {
                        System.out.println("SELECTING CARD");
                        synchronized (this) {
                            selectFBC(fbc);
                            cardRoomFBC.pushToFront(fbc);
                        }
                        break;
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
