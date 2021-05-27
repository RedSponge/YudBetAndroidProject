package com.redsponge.carddeck.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;

import com.redsponge.carddeck.gl.input.InputHandler;
import com.redsponge.carddeck.glscreen.Screen;

public class GLGameView extends GLSurfaceView {

    private GLRenderer renderer;
    private InputHandler inputHandler;

    public GLGameView(Context context) {
        super(context);
        init();
    }

    public GLGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(3);
        setRenderer(renderer = new GLRenderer(getContext()));
        setRenderMode(RENDERMODE_CONTINUOUSLY);
        this.inputHandler = renderer;
    }

    public Screen getScreen() {
        return renderer.getScreen();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if(inputHandler != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    inputHandler.onTouch(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    inputHandler.onDrag(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    inputHandler.onRelease(x, y);
                    break;
            }
        }
        return true;
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        if(inputHandler != null) inputHandler.onDrag(event.getX(), event.getY());
        return super.onDragEvent(event);
    }

    public void setPendingScreen(Screen pendingScreen) {
        renderer.setPendingScreen(pendingScreen);
    }
}
