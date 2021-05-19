package com.redsponge.gltest.glscreen;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;

import com.google.firebase.database.FirebaseDatabase;
import com.redsponge.gltest.card.CardFBC;
import com.redsponge.gltest.card.PileData;
import com.redsponge.gltest.card.RoomFBC;
import com.redsponge.gltest.card.CardTextures;
import com.redsponge.gltest.card.PileFBC;
import com.redsponge.gltest.gl.TextureBatch;
import com.redsponge.gltest.gl.Vector2;
import com.redsponge.gltest.gl.input.InputHandler;
import com.redsponge.gltest.gl.projection.FitViewport;
import com.redsponge.gltest.gl.texture.TextureRegion;

import java.util.List;

public class GameScreen extends Screen implements InputHandler {

    private FitViewport viewport;
    private TextureBatch batch;

    private CardTextures cardTextures;

    private final RoomFBC roomFBC;

    private PileFBC selectedPileFBC;
    private long cardSelectionTime;
    private boolean isDragged;

    private final String roomName;

    public GameScreen(Context context, String roomName) {
        super(context);
        this.roomName = roomName;
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        roomFBC = new RoomFBC(db.getReference("rooms/" + roomName));
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
        if(roomFBC.isFullyLoaded()) {
            synchronized (roomFBC) {
                List<String> pileOrder = roomFBC.getPileOrder();
                for (String pileKey : pileOrder) {
                    PileFBC pile = roomFBC.getPile(pileKey);
                    pile.updateDrawnPosition();
                    if(!pile.hasTopCard()) {
                        System.out.println("Top card of pile " + pileKey + " is not loaded :/");
                        continue;
                    }
                    CardFBC topCard = pile.getTopCard();
                    TextureRegion cardTex = cardTextures.get(topCard.getDisplay().getType(), topCard.getDisplay().isFlipped());
                    float width = pile.getData().getWidth() * pile.getDrawnScale();
                    float height = pile.getData().getHeight() * pile.getDrawnScale();

                    batch.draw(cardTex, pile.getDrawnX() - width / 2f, pile.getDrawnY() - height / 2f, width, height);
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
        if(selectedPileFBC == null) {
            if(roomFBC.isFullyLoaded()) {
                PileFBC finalChosen = null;

                for (PileFBC pileFBC : roomFBC) {
                    PileData pile = pileFBC.getData();

                    if (pile.contains(inWorld) && !pile.isChosen()) {
                        System.out.println("Selecting Pile " + pileFBC);
                        finalChosen = pileFBC;
                        break;
                    }
                }

                if(finalChosen != null) {
                    synchronized (roomFBC) {
                        selectFBC(finalChosen);
                    }
                    roomFBC.pushToFront(finalChosen);
                }
            }
        }
    }

    private void selectFBC(PileFBC pile) {
        selectedPileFBC = pile;
        cardSelectionTime = System.nanoTime();
        isDragged = false;
        pile.getData().setChosenTime(System.currentTimeMillis());
        pile.pushUpdate();
    }

    @Override
    public void onDrag(float x, float y) {
        Vector2 inWorld = viewport.unproject(new Vector2(x, y), new Vector2());
        inWorld.y = viewport.getWorldHeight() - inWorld.y;

        if(selectedPileFBC != null) {
            System.out.println("DRAG!");
            selectedPileFBC.getData().setX(inWorld.x - selectedPileFBC.getData().getWidth() / 2f);
            selectedPileFBC.getData().setY(inWorld.y - selectedPileFBC.getData().getWidth() / 2f);
            selectedPileFBC.getData().setChosenTime(System.nanoTime());
            isDragged = true;
            selectedPileFBC.pushUpdate();
        }
    }

    @Override
    public void onRelease(float x, float y) {
        Vector2 inWorld = viewport.unproject(new Vector2(x, y), new Vector2());
        inWorld.y = viewport.getWorldHeight() - inWorld.y;

        if(selectedPileFBC != null) {
            float dt = (System.nanoTime() - cardSelectionTime) / 1000000000f;
            System.out.println("TIME: " + dt);
            if(dt < 0.2f && !isDragged) {
//                System.out.println("FLIPPING!");
//                selectedPileFBC.getTopCard().getDisplay().setFlipped(!selectedPileFBC.getTopCard().getDisplay().isFlipped());
            }
            selectedPileFBC.getData().setChosenTime(0);
            selectedPileFBC.pushUpdate();
            selectedPileFBC = null;
        }
    }
}
