package com.redsponge.gltest.gl.texture;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import static android.opengl.GLES30.*;
public class Texture {

    private final int texId;
    private boolean isBound;

    private float width, height;

    public Texture(Resources res, int resource) {
        int[] receivers = new int[1];
        glGenTextures(1, receivers, 0);
        texId = receivers[0];

        Bitmap bitmap = BitmapFactory.decodeResource(res, resource);
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        glBindTexture(GL_TEXTURE_2D, texId);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texId);
        isBound = true;
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
        isBound = false;
    }

    public void setMagFilter(TextureFilter filter) {
        bind();
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter.code);
        unbind();
    }

    public void setMinFilter(TextureFilter filter) {
        bind();
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter.code);
        unbind();
    }

    public void dispose() {
        glDeleteTextures(1, new int[] {texId}, 0);
    }

    public enum TextureFilter {
        Linear(GL_LINEAR),
        Nearest(GL_NEAREST)
        ;
        private final int code;

        TextureFilter(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
