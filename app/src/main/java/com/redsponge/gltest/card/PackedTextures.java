package com.redsponge.gltest.card;

import android.content.Context;

import com.redsponge.gltest.R;
import com.redsponge.gltest.gl.Disposable;
import com.redsponge.gltest.gl.texture.Texture;
import com.redsponge.gltest.gl.texture.TextureRegion;

import java.util.HashMap;

public class PackedTextures implements Disposable {

    private final HashMap<String, TextureRegion> textures;
    private final Texture packedTexture;

    public PackedTextures(Context ctx) {
        textures = new HashMap<>();

        packedTexture = new Texture(ctx.getResources(), R.drawable.cards_packed);
        packedTexture.setMagFilter(Texture.TextureFilter.Nearest);
        packedTexture.setMinFilter(Texture.TextureFilter.Nearest);
        final int cardTexWidth = 64;
        final int cardTexHeight = 96;
        final int cardStartMarginY = 180;

        String[] suits = {"heart", "diamond", "club", "spade"};

        for (int i = 0; i < suits.length; i++) {
            for (int j = 0; j < 13; j++) {
                int x = j * cardTexWidth;
                int y = i * cardTexHeight + cardStartMarginY;

                TextureRegion reg = new TextureRegion(packedTexture, x, y, cardTexWidth, cardTexHeight);
                textures.put(suits[i] + (j + 1), reg);
            }
        }

        textures.put(Constants.TEXTURE_FLIPPED, new TextureRegion(packedTexture, 0, cardTexHeight * 4 + cardStartMarginY, cardTexWidth, cardTexHeight));
        textures.put(Constants.TEXTURE_BACKGROUND, new TextureRegion(packedTexture, 0, 0, 320, 180));
        textures.put(Constants.TEXTURE_HAND_BACKGROUND, new TextureRegion(packedTexture, 320, 0, 320, 180));
    }



    public TextureRegion getTexture(String tex) {
        return textures.get(tex);
    }

    public TextureRegion getCard(CardData data) {
        return getCard(data.getType(), data.isFlipped());
    }

    public TextureRegion getCard(String type, boolean flipped) {
        if(flipped) {
            return textures.get(Constants.TEXTURE_FLIPPED);
        }
        return textures.get(type);
    }

    @Override
    public void dispose() {
        packedTexture.dispose();
    }
}
