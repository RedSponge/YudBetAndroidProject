package com.redsponge.carddeck.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.redsponge.carddeck.gl.input.InputHandler;
import com.redsponge.carddeck.glscreen.Screen;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer, InputHandler {

    private Screen screen;
    private Context context;
    private long lastTime;

    private int lastWidth, lastHeight;
    private Screen pendingScreen;

    public GLRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if (screen != null) {
            synchronized (this) {
                screen.show();
            }
        }
        lastTime = System.currentTimeMillis();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (screen != null) {
            synchronized (this) {
                screen.resize(width, height);
            }
        }

        lastWidth = width;
        lastHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        updatePendingScreen();

        long now = System.currentTimeMillis();
        float delta = (now - lastTime) / 1000f;
        lastTime = now;

        if (screen != null) {
            synchronized (this) {
                screen.render(delta);
            }
        }
    }

    private void updatePendingScreen() {
        if (pendingScreen != null) {
            switchScreen(pendingScreen);
            pendingScreen = null;
        }
    }

    private void switchScreen(Screen screen) {
        if (this.screen != null) {
            synchronized (this) {
                this.screen.hide();
                this.screen.dispose();
            }
        }
        this.screen = screen;
        synchronized (this) {
            this.screen.show();
            this.screen.resize(lastWidth, lastHeight);
        }
    }

    @Override
    public void onTouch(float x, float y) {
        if (screen instanceof InputHandler) {
            ((InputHandler) screen).onTouch(x, y);
        }
    }

    @Override
    public void onDrag(float x, float y) {
        if (screen instanceof InputHandler) {
            ((InputHandler) screen).onDrag(x, y);
        }
    }

    @Override
    public void onRelease(float x, float y) {
        if (screen instanceof InputHandler) {
            ((InputHandler) screen).onRelease(x, y);
        }
    }

    public Screen getScreen() {
        return screen;
    }

    public void setPendingScreen(Screen pendingScreen) {
        this.pendingScreen = pendingScreen;
    }
}
