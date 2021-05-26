package com.redsponge.gltest.glscreen;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.redsponge.gltest.card.CardFBC;
import com.redsponge.gltest.card.Constants;
import com.redsponge.gltest.card.PackedTextures;
import com.redsponge.gltest.card.PileData;
import com.redsponge.gltest.card.PileFBC;
import com.redsponge.gltest.card.RoomFBC;
import com.redsponge.gltest.gl.TextureBatch;
import com.redsponge.gltest.gl.Vector2;
import com.redsponge.gltest.gl.input.InputHandler;
import com.redsponge.gltest.gl.projection.FitViewport;
import com.redsponge.gltest.gl.texture.TextureRegion;
import com.redsponge.gltest.utils.MathUtils;
import com.redsponge.gltest.utils.Utils;

import java.util.List;

public class GameScreen extends Screen implements InputHandler {

    private FitViewport viewport;
    private TextureBatch batch;
    private PackedTextures packedTextures;
    private final RoomFBC roomFBC;

    private PileFBC selectedPileFBC;
    private long cardSelectionTime;

    private final Vector2 dragStartPos;
    private final Vector2 tmpVector;

    private final String roomName;
    private boolean hasDoneSplit;

    public GameScreen(Context context, String roomName) {
        super(context);
        this.roomName = roomName;
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        roomFBC = new RoomFBC(db.getReference("rooms/" + roomName));
        dragStartPos = new Vector2();
        tmpVector = new Vector2();
    }

    @Override
    public void show() {
        viewport = new FitViewport(320 * 1.5f   , 180 * 1.5f);
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
        batch.draw(packedTextures.getTexture(Constants.TEXTURE_BACKGROUND), 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        synchronized (roomFBC) {
            if(roomFBC.isFullyLoaded()) {
                List<String> pileOrder = roomFBC.getPileOrder();
                for (String pileKey : pileOrder) {
                    PileFBC pile = roomFBC.getPile(pileKey);
                    pile.updateDrawnPosition();
                    if(!pile.hasTopCard()) {
                        Log.w("GameScreen", "Top card of pile " + pileKey + " is not loaded.");
                        continue;
                    }
                    CardFBC topCard = pile.getTopCard();
                    TextureRegion cardTex = packedTextures.getCard(topCard.getData().getType(), topCard.getData().isFlipped());
                    float width = pile.getData().getWidth() * pile.getDrawnScale();
                    float height = pile.getData().getHeight() * pile.getDrawnScale();

                    int pileHeight = Math.min(pile.getCardOrder().size(), Constants.PILE_MAX_DRAWN_HEIGHT);

                    for (int i = pileHeight - 1; i >= 0; i--) {
                        CardFBC drawnCard = pile.getCard(i);
                        TextureRegion tex = packedTextures.getCard(drawnCard.getData());

                        batch.draw(tex, pile.getDrawnX() - width / 2f, pile.getDrawnY() - height / 2f - i * 2, width, height);
                    }
                }
            }
        }
        batch.draw(packedTextures.getTexture(Constants.TEXTURE_HAND_BACKGROUND), 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
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
        packedTextures.dispose();
    }

    private void selectFBC(PileFBC pile) {
        selectedPileFBC = pile;
        cardSelectionTime = System.nanoTime();
        pile.getData().setChosenTime(cardSelectionTime);
        pile.pushUpdate();
    }

    @Override
    public void onTouch(float x, float y) {
        if(selectedPileFBC != null) return;
        if(!roomFBC.isFullyLoaded()) return;

        Vector2 inWorld = viewport.unproject(tmpVector.set(x, y), tmpVector);
        inWorld.y = viewport.getWorldHeight() - inWorld.y;

        PileFBC finalChosen = null;
        for (PileFBC pileFBC : roomFBC) {
            PileData pile = pileFBC.getData();

            if (pile.contains(inWorld) && !pile.isChosen()) {
                finalChosen = pileFBC;
            }
        }

        if(finalChosen != null) {
            dragStartPos.set(x, y);
            selectFBC(finalChosen);
            roomFBC.pushToFront(finalChosen);

            // Prevent splitting if there is only one card by setting the split flag to true
            hasDoneSplit = finalChosen.getCardOrder().size() <= 1;
        }
    }


    @Override
    public void onDrag(float x, float y) {
        if(selectedPileFBC == null) return;

        Vector2 inWorld = viewport.unproject(tmpVector.set(x, y), tmpVector);
        inWorld.y = viewport.getWorldHeight() - inWorld.y;

        if(!hasDoneSplit) {
            float distanceSquared = inWorld.dst2(dragStartPos);
            float deltaTime = Utils.secondsFrom(cardSelectionTime);

            if (deltaTime > Constants.PILE_HOLD_MOVE_TIME) {
                hasDoneSplit = true;
            }
            else if (distanceSquared > Constants.MIN_CARD_SPLIT_DST2) {
                System.out.println("Split!");
                CardFBC topCard = selectedPileFBC.popTopCard();
                PileFBC newPile = roomFBC.createPile(inWorld.x, inWorld.y, System.nanoTime(), topCard);
                hasDoneSplit = true;
                selectedPileFBC.getData().setChosenTime(0);
                selectedPileFBC.pushUpdate();
                selectedPileFBC = newPile;
            }
        }

        selectedPileFBC.getData().setX(MathUtils.clamp(Constants.CARD_WIDTH / 2f, inWorld.x - selectedPileFBC.getData().getWidth() / 2f, viewport.getWorldWidth() - Constants.CARD_WIDTH / 2f));
        selectedPileFBC.getData().setY(MathUtils.clamp(Constants.CARD_HEIGHT / 2f, inWorld.y - selectedPileFBC.getData().getWidth() / 2f, viewport.getWorldHeight() - Constants.CARD_HEIGHT / 2f));
        selectedPileFBC.getData().setChosenTime(System.nanoTime());
        selectedPileFBC.pushUpdate();
    }

    @Override
    public void onRelease(float x, float y) {
        if(selectedPileFBC == null) return;

        Vector2 inWorld = viewport.unproject(tmpVector.set(x, y), tmpVector);
        inWorld.y = viewport.getWorldHeight() - inWorld.y;

        float dt = (System.nanoTime() - cardSelectionTime) / 1000000000f;
        float distanceSquared = dragStartPos.dst2(inWorld);
        System.out.println("Release!");
        if(dt < 0.2f && (!hasDoneSplit || selectedPileFBC.getCardOrder().size() == 1) && distanceSquared < 20 * 20) {
            selectedPileFBC.getTopCard().getData().setFlipped(!selectedPileFBC.getTopCard().getData().isFlipped());
            selectedPileFBC.getTopCard().pushUpdate();
        } else {
            PileFBC pileToMergeWith = null;
            for (PileFBC otherPile : roomFBC) {
                if(otherPile == selectedPileFBC) continue;
                if(otherPile.getData().getCenter().dst2(selectedPileFBC.getData().getCenter()) < 10 * 10) {
                    pileToMergeWith = otherPile;
                }
            }
            if(pileToMergeWith != null) {
                roomFBC.mergePiles(pileToMergeWith, selectedPileFBC);
            }
        }

        selectedPileFBC.getData().setChosenTime(0);
        selectedPileFBC.pushUpdate();
        selectedPileFBC = null;
    }
}
