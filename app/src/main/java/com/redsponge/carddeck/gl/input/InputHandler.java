package com.redsponge.carddeck.gl.input;

public interface InputHandler {

    void onTouch(float x, float y);
    void onDrag(float x, float y);
    void onRelease(float x, float y);
}
