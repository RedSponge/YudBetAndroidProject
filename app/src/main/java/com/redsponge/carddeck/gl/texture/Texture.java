package com.redsponge.carddeck.gl.texture;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.redsponge.carddeck.gl.Disposable;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES30.GL_LINEAR;
import static android.opengl.GLES30.GL_NEAREST;
import static android.opengl.GLES30.GL_TEXTURE_2D;
import static android.opengl.GLES30.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES30.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES30.glBindTexture;
import static android.opengl.GLES30.glDeleteTextures;
import static android.opengl.GLES30.glGenTextures;
import static android.opengl.GLES30.glTexParameterf;
public class Texture implements Disposable {

    private final int texId;

    private int width, height;

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

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texId);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setMagFilter(TextureFilter filter) {
        bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter.code);
        unbind();
    }

    public void setMinFilter(TextureFilter filter) {
        bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter.code);
        unbind();
    }

    @Override
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
