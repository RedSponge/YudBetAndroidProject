package com.redsponge.gltest.gl;

public class Vector2 {

    public float x, y;

    public Vector2(Vector2 vec) {
        this(vec.x, vec.y);
    }

    public Vector2() {
        this(0, 0);
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2 set(Vector2 other) {
        this.x = other.x;
        this.y = other.y;
        return this;
    }

    public Vector2 add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2 add(Vector2 other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public Vector2 sub(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vector2 sub(Vector2 other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }

    public Vector2 scl(float scaler) {
        this.x *= scaler;
        this.y *= scaler;
        return this;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public float dst2(Vector2 dragStartPos) {
        return dst2(dragStartPos.x, dragStartPos.y);
    }

    private float dst2(float x, float y) {
        float dx = this.x - x;
        float dy = this.y - y;
        return dx * dx + dy * dy;
    }
}
