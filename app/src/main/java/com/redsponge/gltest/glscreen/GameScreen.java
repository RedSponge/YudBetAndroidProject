package com.redsponge.gltest.glscreen;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.redsponge.gltest.R;
import com.redsponge.gltest.card.CardFBC;
import com.redsponge.gltest.card.PileData;
import com.redsponge.gltest.card.RoomFBC;
import com.redsponge.gltest.card.PackedTextures;
import com.redsponge.gltest.card.PileFBC;
import com.redsponge.gltest.gl.TextureBatch;
import com.redsponge.gltest.gl.Vector2;
import com.redsponge.gltest.gl.input.InputHandler;
import com.redsponge.gltest.gl.projection.FitViewport;
import com.redsponge.gltest.gl.projection.ScaleViewport;
import com.redsponge.gltest.gl.texture.Texture;
import com.redsponge.gltest.gl.texture.TextureRegion;
import com.redsponge.gltest.utils.MathUtils;

import java.util.List;

public class GameScreen extends Screen implements InputHandler {

    private FitViewport viewport;

    private TextureBatch batch;

    private PackedTextures packedTextures;


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
        viewport = new FitViewport(320*2, 180*2);
        viewport.centerCamera();

        batch = new TextureBatch();

        packedTextures = new PackedTextures(context);
    }

    @Override
    public void render(float delta) {

        GLES30.glClearColor(30 / 255f, 123 / 255f, 58 / 255f, 1.0f);
        GLES30.glClear(GLES20.GL_COLOR_BUFFER_BIT);


        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().getCombinedMatrix());

        batch.begin();
        batch.draw(packedTextures.getTexture("background"), 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        if(roomFBC.isFullyLoaded()) {
            synchronized (roomFBC) {
                List<String> pileOrder = roomFBC.getPileOrder();
                for (String pileKey : pileOrder) {
                    PileFBC pile = roomFBC.getPile(pileKey);
                    pile.updateDrawnPosition();
                    if(!pile.hasTopCard()) {
                        Log.w("GameScreen", "Top card of pile " + pileKey + " is not loaded.");
                        continue;
                    }
                    CardFBC topCard = pile.getTopCard();
                    TextureRegion cardTex = packedTextures.getCard(topCard.getDisplay().getType(), topCard.getDisplay().isFlipped());
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

        if(selectedPileFBC == null) {
            if(roomFBC.isFullyLoaded()) {
                System.out.println("Touch: " + inWorld);
                PileFBC finalChosen = null;

                for (PileFBC pileFBC : roomFBC) {
                    PileData pile = pileFBC.getData();

                    System.out.println(pile);
                    if (pile.contains(inWorld) && !pile.isChosen()) {
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
            selectedPileFBC.getData().setX(MathUtils.clamp(0, inWorld.x - selectedPileFBC.getData().getWidth() / 2f, viewport.getWorldWidth()));
            selectedPileFBC.getData().setY(MathUtils.clamp(0, inWorld.y - selectedPileFBC.getData().getWidth() / 2f, viewport.getWorldHeight()));
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
