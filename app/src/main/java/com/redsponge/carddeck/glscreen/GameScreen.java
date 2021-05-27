package com.redsponge.carddeck.glscreen;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

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
import com.redsponge.carddeck.utils.Utils;

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

        roomFBC.updatePiles();

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().getCombinedMatrix());

        batch.begin();
        batch.draw(packedTextures.getTexture(Constants.TEXTURE_BACKGROUND), 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

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

        float handHeightPercent;
        float animationSpeed = 2;
        if (isHandUp) {
            handHeightPercent = MathUtils.easeOutInterpolation(MathUtils.clamp(0, Utils.secondsFrom(handSwitchTime) * animationSpeed, 1));
        } else {
            handHeightPercent = MathUtils.easeInInterpolation(MathUtils.clamp(0, 1 - Utils.secondsFrom(handSwitchTime) * animationSpeed, 1));
        }

        batch.draw(packedTextures.getTexture(Constants.TEXTURE_HAND_BACKGROUND), 0, -250 + handHeightPercent * 60, viewport.getWorldWidth(), viewport.getWorldHeight());
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

    private void selectPile(String pileKey) {
        this.selectedPile = pileKey;
        pileSelectionTime = System.nanoTime();
        roomFBC.getPile(pileKey).setChosenTime(pileSelectionTime);
    }

    @Override
    public void onTouch(float x, float y) {
        if(selectedPile != null) return;
//        if(!roomFBC.isFullyLoaded()) return;
//
        Vector2 inWorld = viewport.unproject(tmpVector.set(x, y), tmpVector);
        inWorld.y = viewport.getWorldHeight() - inWorld.y;

        String finalChosen = null;
        for (PileFBC pileFBC : roomFBC) {
            PileData data = pileFBC.getData();
            if(data.contains(inWorld) && !data.isChosen()) {
                finalChosen = pileFBC.getReference().getKey();
            }
        }

        if(finalChosen != null) {
            selectPile(finalChosen);
        }
    }


    @Override
    public void onDrag(float x, float y) {
        if(selectedPile == null) return;

        Vector2 inWorld = viewport.unproject(tmpVector.set(x, y), tmpVector);
        inWorld.y = viewport.getWorldHeight() - inWorld.y;

        PileFBC pileFBC = roomFBC.getPile(selectedPile);

        pileFBC.setData(new PileData(inWorld.x, inWorld.y, System.nanoTime()));
        //        if(!hasDoneSplit) {
//            float distanceSquared = inWorld.dst2(dragStartPos);
//            float deltaTime = Utils.secondsFrom(cardSelectionTime);
//
//            if (deltaTime > Constants.PILE_HOLD_MOVE_TIME) {
//                hasDoneSplit = true;
//            }
//            else if (distanceSquared > Constants.MIN_CARD_SPLIT_DST2) {
//                System.out.println("Split!");
//                CardFBC topCard = selectedPileFBC.popTopCard();
//                PileFBC newPile = roomFBC.createPile(inWorld.x, inWorld.y, System.nanoTime(), topCard);
//                hasDoneSplit = true;
//                selectedPileFBC.getData().setChosenTime(0);
//                selectedPileFBC.pushUpdate();
//                selectedPileFBC = newPile;
//            }
//        }
//
//        selectedPileFBC.getData().setX(MathUtils.clamp(Constants.CARD_WIDTH / 2f, inWorld.x - selectedPileFBC.getData().getWidth() / 2f, viewport.getWorldWidth() - Constants.CARD_WIDTH / 2f));
//        selectedPileFBC.getData().setY(MathUtils.clamp(Constants.CARD_HEIGHT / 2f, inWorld.y - selectedPileFBC.getData().getWidth() / 2f, viewport.getWorldHeight() - Constants.CARD_HEIGHT / 2f));
//        selectedPileFBC.getData().setChosenTime(System.nanoTime());
//        selectedPileFBC.pushUpdate();
    }

    @Override
    public void onRelease(float x, float y) {
        if(selectedPile == null) return;
        selectedPile = null;

//        Vector2 inWorld = viewport.unproject(tmpVector.set(x, y), tmpVector);
//        inWorld.y = viewport.getWorldHeight() - inWorld.y;
//
//        float dt = (System.nanoTime() - cardSelectionTime) / 1000000000f;
//        float distanceSquared = dragStartPos.dst2(inWorld);
//        System.out.println("Release!");
//        if(dt < 0.2f && (!hasDoneSplit || selectedPileFBC.getCardOrder().size() == 1) && distanceSquared < 20 * 20) {
//            selectedPileFBC.getTopCard().getData().setFlipped(!selectedPileFBC.getTopCard().getData().isFlipped());
//            selectedPileFBC.getTopCard().pushUpdate();
//        } else {
//            PileFBC pileToMergeWith = null;
//            for (PileFBC otherPile : roomFBC) {
//                if(otherPile == selectedPileFBC) continue;
//                if(otherPile.getData().getCenter().dst2(selectedPileFBC.getData().getCenter()) < 10 * 10) {
//                    pileToMergeWith = otherPile;
//                }
//            }
//            if(pileToMergeWith != null) {
//                roomFBC.mergePiles(pileToMergeWith, selectedPileFBC);
//            }
//        }
//
//        selectedPileFBC.getData().setChosenTime(0);
//        selectedPileFBC.pushUpdate();
//        selectedPileFBC = null;
    }

    @Override
    public void onAndroidEvent(int eventId) {
        if(eventId == Constants.TOGGLE_HAND_EVENT) {
            toggleHand();
        } else if(eventId == Constants.SHAKE_EVENT) {
            spreadCards();
        }
    }

    private void spreadCards() {
//        Random rnd = new Random();
//        synchronized (roomFBC) {
//            for (PileFBC pile : roomFBC) {
//                while (pile.getCardOrder().size() > 1) {
//                    CardFBC topCard = pile.popTopCard();
//                    float x = rnd.nextInt((int) (viewport.getWorldWidth() - 50)) + 25f;
//                    float y = rnd.nextInt((int) (viewport.getWorldHeight() - 50)) + 25f;
//
//                    roomFBC.createPile(x, y, 0, topCard);
//                }
//
//                float x = rnd.nextInt((int) (viewport.getWorldWidth() - 50)) + 25f;
//                float y = rnd.nextInt((int) (viewport.getWorldHeight() - 50)) + 25f;
//                pile.getData().setX(x);
//                pile.getData().setY(y);
//            }
//        }
    }

    private void toggleHand() {
        isHandUp = !isHandUp;
        handSwitchTime = System.nanoTime();
    }
}
