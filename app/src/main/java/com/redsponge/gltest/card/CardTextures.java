package com.redsponge.gltest.card;

import android.content.Context;
import android.util.Log;

import com.redsponge.gltest.R;
import com.redsponge.gltest.gl.texture.Texture;
import com.redsponge.gltest.gl.texture.TextureRegion;

import java.util.HashMap;

public class CardTextures {

    private HashMap<String, TextureRegion> textures;
    private Texture packedTexture;

    public CardTextures(Context ctx) {
        textures = new HashMap<>();

        packedTexture = new Texture(ctx.getResources(), R.drawable.cards_packed);
        packedTexture.setMagFilter(Texture.TextureFilter.Nearest);
        packedTexture.setMinFilter(Texture.TextureFilter.Nearest);
        int cardTexWidth = 64;
        int cardTexHeight = 96;
        //flippedTexture = new Texture(ctx.getResources(), R.drawable.card_back);
        //textures.put("suit1", new Texture(ctx.getResources(), R.drawable.suit1));
        String[] suits = {"heart", "diamond", "club", "spade"};

        for (int i = 0; i < suits.length; i++) {
            for (int j = 0; j < 13; j++) {
                int x = j * cardTexWidth;
                int y = i * cardTexHeight;

                Log.i("CardTextures", "card " + suits[i] + (j + 1) + " is in pos [" + x + ',' + y + ']');
                TextureRegion reg = new TextureRegion(packedTexture, x, y, cardTexWidth, cardTexHeight);
                textures.put(suits[i] + (j + 1), reg);
            }
        }

        textures.put("flipped", new TextureRegion(packedTexture, 0, cardTexHeight * 4, cardTexWidth, cardTexHeight));
    }


    public TextureRegion get(String type, boolean flipped) {
        if(flipped) {
            Log.d("CardTextures", textures.get("flipped").toString());
            return textures.get("flipped");
        }
        return textures.get(type);
    }
}
