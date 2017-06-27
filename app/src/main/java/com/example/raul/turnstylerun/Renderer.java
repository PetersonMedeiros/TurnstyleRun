package com.example.raul.turnstylerun;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.World;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Raul on 01/05/2017.
 */

public class Renderer implements GLSurfaceView.Renderer{

    private MainCharacter mainChar;
    private Enemy enemy;

    Context context;

    private int hexColor = 0xc0392a;
    private float color[] = {1.0f, 1.0f, 1.0f, 1.0f};
    private float _width = 320f;
    private float _height = 480f;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Renderer(Context context) {
        // Set up the data-array buffers for these shapes ( NEW )
        mainChar = new MainCharacter();

        enemy = new Enemy(context);

        this.context = context;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        //Matriz de projeção, usada para criar perspectiva 3D
        gl.glMatrixMode(GL10.GL_PROJECTION);

        float ratio = _width / _height;
        float size = .01f * (float) Math.tan(Math.toRadians(45.0) / 2);
        // orthographic:
        gl.glFrustumf(-1, 1, -1/ratio, 1/ratio , 0.1f, 100.0f);
        //gl.glOrthof(-1, 1, -1 / ratio, 1 / ratio, 0.1f, 100.0f);
        gl.glViewport(0, 0, (int) _width, (int) _height);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        // preparation
        gl.glClearColor(color[0],color[1],color[2],color[3]);
        // enable the differentiation of which side may be visible
        gl.glEnable(GL10.GL_CULL_FACE);
        // which is the front? the one which is drawn counter clockwise
        gl.glFrontFace(GL10.GL_CCW);
        // which one should NOT be drawn
        gl.glCullFace(GL10.GL_BACK); //

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
    }

    public void onDrawFrame(GL10 gl) {

        // Redraw background color
        gl.glClearColor(color[0],color[1],color[2],color[3]);

        gl.glLoadIdentity();

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        mainChar.draw(gl);
        if(enemy.isVisible) {
            enemy.draw(gl);
        }
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        _width = width;
        _height = height;
        gl.glViewport(0, 0, width, height);
    }

    public void changeColor(){
        boolean boo = false;
        int i = 0x4FEF3;
        color[0] = Color.red(hexColor)/255f - Color.red(i)/255f;
        color[1] = Color.green(hexColor)/255f - Color.green(i)/255f;
        color[2] = Color.blue(hexColor)/255f - Color.blue(i)/255f;

        if(hexColor == 0xc0392b) boo = false;
        if(hexColor == 0x8e44ad) boo = true;

        if(boo) hexColor = hexColor + i;
        else hexColor = hexColor - i;
    }

    public void moveLeft(){
        mainChar.moveLeft();
    }

    public void moveRight(){
        mainChar.moveRight();
    }

    public void goDown(){
        enemy.goDown();
    }
}
