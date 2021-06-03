package com.redsponge.carddeck.glscreen;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.redsponge.carddeck.card.CardData;
import com.redsponge.carddeck.card.Constants;
import com.redsponge.carddeck.card.PackedTextures;
import com.redsponge.carddeck.card.PileData;
import com.redsponge.carddeck.card.PileFBC;
import com.redsponge.carddeck.card.RoomFBC;
import com.redsponge.carddeck.gl.TextureBatch;
import com.redsponge.carddeck.gl.Vector2;
import com.redsponge.carddeck.gl.input.InputHandler;
import com.redsponge.carddeck.gl.projection.FitViewport;
import com.redsponge.carddeck.gl.texture.TextureRegion;
import com.redsponge.carddeck.utils.MathUtils;
import com.redsponge.carddeck.utils.Pair;
import com.redsponge.carddeck.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameScreen extends Screen implements InputHandler {

    private FitViewport viewport;
    private TextureBatch batch;
    private PackedTextures packedTextures;
    private final RoomFBC roomFBC;

    private String selectedPile;
    private long pileSelectionTime;

    private final Vector2 dragStartPos;
    private final Vector2 tmpVector;

    private final String roomName;
    private boolean hasDoneSplit;
    private boolean isHandUp;
    private long handSwitchTime;

    private boolean isSpreadingCards;

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
        viewport = new FitViewport(320 * 1.5f, 180 * 1.5f);
        viewport.centerCamera();

        batch = new TextureBatch();

        packedTextures = new PackedTextures(context);
    }

    @Override
    public void render(float delta) {

        GLES30.glClearColor(30 / 255f, 123 / 255f, 58 / 255f, 1.0f);
        GLES30.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        roomFBC.updatePiles();

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().getCombinedMatrix());

        batch.begin();
        batch.draw(packedTextures.getTexture(Constants.TEXTURE_BACKGROUND), 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        renderRoom();
        renderHand();

        batch.end();
    }

    private void renderHand() {
        float handHeightPercent;
        float animationSpeed = 2;
        if (isHandUp) {
            handHeightPercent = MathUtils.easeOutInterpolation(MathUtils.clamp(0, Utils.secondsSince(handSwitchTime) * animationSpeed, 1));
        } else {
            handHeightPercent = MathUtils.easeInInterpolation(MathUtils.clamp(0, 1 - Utils.secondsSince(handSwitchTime) * animationSpeed, 1));
        }

        batch.draw(packedTextures.getTexture(Constants.TEXTURE_HAND_BACKGROUND), 0, -250 + handHeightPercent * 60, viewport.getWorldWidth(), viewport.getWorldHeight());
    }

    private void renderRoom() {
        if (roomFBC.isLoaded()) {
            for (PileFBC pile : roomFBC) {
                pile.updateDrawnPosition();
                if (!pile.hasTopCard()) {
                    Log.w("GameScreen", "Top card of pile " + pile.getReference().getKey() + " is not loaded.");
                    continue;
                }

                float width = pile.getData().getWidth() * pile.getDrawnScale();
                float height = pile.getData().getHeight() * pile.getDrawnScale();

                int pileHeight = Math.min(pile.getSize(), Constants.PILE_MAX_DRAWN_HEIGHT);

                for (int i = pileHeight - 1; i >= 0; i--) {
                    CardData drawnCard = pile.getCard(i);
                    TextureRegion tex = packedTextures.getCard(drawnCard);

                    batch.draw(tex, pile.getDrawnX() - width / 2f, pile.getDrawnY() - height / 2f - i * 2, width, height);
                }
            }
        }
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

    private void selectPile(String pileKey) {
        this.selectedPile = pileKey;
        pileSelectionTime = System.nanoTime();
        roomFBC.getPile(pileKey).setChosenTime(pileSelectionTime);
    }

    @Override
    public void onTouch(float x, float y) {
        if (selectedPile != null) return;

        Vector2 inWorld = viewport.unproject(tmpVector.set(x, y), tmpVector);
        inWorld.y = viewport.getWorldHeight() - inWorld.y;

        String finalChosen = null;
        for (PileFBC pileFBC : roomFBC) {
            PileData data = pileFBC.getData();
            if (data.contains(inWorld) && !data.isChosen()) {
                finalChosen = pileFBC.getReference().getKey();
            }
        }

        if (finalChosen != null) {
            selectPile(finalChosen);
            dragStartPos.set(inWorld);
            roomFBC.pushPileToFront(finalChosen);
            hasDoneSplit = false;
        }
    }


    @Override
    public void onDrag(float x, float y) {
        if (selectedPile == null) return;
        if (!roomFBC.isLoaded()) return;
        if (!roomFBC.isPileLoaded(selectedPile)) return;

        Vector2 inWorld = viewport.unproject(tmpVector.set(x, y), tmpVector);
        inWorld.y = viewport.getWorldHeight() - inWorld.y;

        PileFBC pileFBC = roomFBC.getPile(selectedPile);
        if (pileFBC.getSize() > 1 && Utils.secondsSince(pileSelectionTime) < Constants.PILE_HOLD_MOVE_TIME) {
            float distanceSquared = inWorld.dst2(dragStartPos);

            if (distanceSquared < Constants.MIN_CARD_SPLIT_DST2) {
                DatabaseReference newPile = roomFBC.newPileRef();

                newPile.child(Constants.TRANSFORM_REFERENCE).setValue(pileFBC.getData().cpy());
                newPile.child(Constants.CARDS_REFERENCE).push().setValue(pileFBC.getCardId(0));
                pileFBC.getCardList().removeIndex(0);
                pileFBC.setChosenTime(0);

                roomFBC.addPileToOrder(newPile.getKey());
                hasDoneSplit = true;

                selectedPile = newPile.getKey();
            }
        } else {
            pileFBC.setData(new PileData(inWorld.x, inWorld.y, System.nanoTime()));
        }
    }

    @Override
    public void onRelease(float x, float y) {
        if (selectedPile == null) return;

        Vector2 inWorld = viewport.unproject(tmpVector.set(x, y), tmpVector);
        inWorld.y = viewport.getWorldHeight() - inWorld.y;

        if (!hasDoneSplit && Utils.secondsSince(pileSelectionTime) < 0.2f && inWorld.dst2(dragStartPos) < 20 * 20) {
            String cardId = roomFBC.getPile(selectedPile).getCardId(0);
            roomFBC.setCardFlip(cardId, !roomFBC.getCard(cardId).isFlipped());
        } else {
            String pileToMergeWith = null;
            for (PileFBC pileFBC : roomFBC) {
                if (pileFBC.getReference().getKey().equals(selectedPile)) continue;

                float dst2 = inWorld.dst2(pileFBC.getData().getCenter());
                if (dst2 < Constants.MIN_PILE_MERGE_DST2) {
                    pileToMergeWith = pileFBC.getReference().getKey();
                }
            }

            if (pileToMergeWith != null) {
                roomFBC.mergePiles(pileToMergeWith, selectedPile);
            }
        }

        roomFBC.getPile(selectedPile).setChosenTime(0);
        selectedPile = null;
    }

    @Override
    public void onAndroidEvent(int eventId) {
        if (eventId == Constants.TOGGLE_HAND_EVENT) {
            toggleHand();
        } else if (eventId == Constants.SHAKE_EVENT) {
            spreadCards();
        }
    }

    private synchronized void spreadCards() {
        if(!roomFBC.isLoaded()) return;
        if(isSpreadingCards) return;
        isSpreadingCards = true;

        Random rnd = new Random();
        List<Pair<Vector2, String>> newPiles = new ArrayList<>();

        for (PileFBC pileFBC : roomFBC) {
            for (int i = 0; i < pileFBC.getSize() - 1; i++) {
                float x = rnd.nextInt((int) (viewport.getWorldWidth() - 50)) + 25f;
                float y = rnd.nextInt((int) (viewport.getWorldHeight() - 50)) + 25f;

                newPiles.add(new Pair<>(new Vector2(x, y), pileFBC.getCardId(i)));
            }

            DatabaseReference child = pileFBC.getReference().child(Constants.CARDS_REFERENCE);
            String topCard = pileFBC.getCardId(pileFBC.getSize() - 1);
            child.removeValue();
            child.push().setValue(topCard);
        }

        for (Pair<Vector2, String> newPile : newPiles) {
            DatabaseReference newPileRef = roomFBC.newPileRef();

            PileData data = new PileData(newPile.first.x, newPile.first.y, 0);
            newPileRef.child(Constants.TRANSFORM_REFERENCE).setValue(data);
            newPileRef.child(Constants.CARDS_REFERENCE).push().setValue(newPile.second);

            roomFBC.addPileToOrder(newPileRef.getKey());
        }

        isSpreadingCards = false;
    }

    private void toggleHand() {
        isHandUp = !isHandUp;
        handSwitchTime = System.nanoTime();
    }
}
