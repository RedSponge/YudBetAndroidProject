package com.redsponge.gltest.gl.texture;

public class TextureRegion {

    private Texture texture;
    private float x, y, width, height;

    public TextureRegion() {}

    public TextureRegion(Texture texture) {
        this.texture = texture;
        x = y = 0;
        width = texture.getWidth();
        height = texture.getHeight();
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public TextureRegion(Texture texture, float x, float y, float width, float height) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Texture getTexture() {
        return texture;
    }

    public float getX1() {
        return x / texture.getWidth();
    }

    public float getY1() {
        return y / texture.getHeight();
    }

    public float getX2() {
        return getX1() + getNormWidth();
    }

    private float getNormWidth() {
        return width / texture.getWidth();
    }

    public float getY2() {
        return getY1() + getNormHeight();
    }

    private float getNormHeight() {
        return height / texture.getHeight();
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public String toString() {
        return "TextureRegion{" +
                "texture=" + texture +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
