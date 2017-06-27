package com.example.raul.turnstylerun;

import android.content.Context;
import android.opengl.GLES10;
import android.opengl.GLES11;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;

/**
 * Created by Raul on 01/05/2017.
 */

public class Surface extends GLSurfaceView {

    private final com.example.raul.turnstylerun.Renderer mRenderer;

    private long lastMove = 0;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Surface(Context context){
        super(context);

        mRenderer = new com.example.raul.turnstylerun.Renderer(context);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
        setRenderMode(this.RENDERMODE_WHEN_DIRTY);
    }

    public boolean onTouchEvent(MotionEvent e){
        float x = e.getX();
        if(x > (getWidth() / 2)){
            moveRight();
        }else{
            moveLeft();
        }
        mRenderer.changeColor();
        requestRender();
        return true;
    }

    private void moveRight(){
        mRenderer.moveRight();
    }

    private void moveLeft(){
        mRenderer.moveLeft();
    }
}
