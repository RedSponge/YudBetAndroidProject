package com.redsponge.gltest.card;

import android.content.Context;

import com.redsponge.gltest.R;
import com.redsponge.gltest.gl.texture.Texture;

import java.util.HashMap;

public class CardTextures {

    private HashMap<String, Texture> textures;
    private Texture flippedTexture;

    public CardTextures(Context ctx) {
        textures = new HashMap<>();

        flippedTexture = new Texture(ctx.getResources(), R.drawable.card_back);
        textures.put("suit1", new Texture(ctx.getResources(), R.drawable.suit1));
    }


    public Texture get(String type, boolean flipped) {
        if(flipped) return flippedTexture;
        return textures.get(type);
    }
}
