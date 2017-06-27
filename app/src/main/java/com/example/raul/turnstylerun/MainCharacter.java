package com.example.raul.turnstylerun;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLES32;
import android.opengl.GLSurfaceView;

import com.threed.jpct.Object3D;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Raul on 14/05/2017.
 */

public class MainCharacter {

    // new object variables we need
    // a raw buffer to hold indices
    private ShortBuffer _indexBuffer;

    // a raw buffer to hold the vertices
    private FloatBuffer _vertexBuffer;
    private FloatBuffer _colorBuffer;

    private int _nrOfVertices = 0;

    private int pos = 0;

    float[] coords = {
             0.25f, -1.80f, -0.14f, //0
             0.25f, -1.25f, -0.14f, //1
            -0.25f, -1.80f, -0.14f, //2
            -0.25f, -1.25f, -0.14f, //3
             0.25f, -1.80f, -0.13f, //4
             0.25f, -1.25f, -0.13f, //5
            -0.25f, -1.80f, -0.13f, //6
            -0.25f, -1.25f, -0.13f  //7
    };

    float[] colors = {
            0f, 0f, 1f, 1f, // point 0 blue
            0f, 0f, 0f, 1f, // point 1 blue
            0f, 0f, 1f, 1f, // point 2 blue
            0f, 0f, 0f, 1f, // point 3 blue
            0f, 0f, 1f, 1f, // point 4 blue
            0f, 0f, 0f, 1f, // point 5 blue
            0f, 0f, 1f, 1f, // point 6 blue
            0f, 0f, 0f, 1f  // point 7 blue
    };

    short[] indices = new short[] {
            2,6,4,2,4,0, //face de baixo

            0,4,5,0,5,1, //face direita

            1,5,7,1,7,3, //face de cima

            3,7,6,3,6,2, //face esquerda

            6,5,7,6,4,5, //face da frente

            3,2,0,3,0,1  //face de trás
    };

    public MainCharacter() {
        _nrOfVertices = coords.length;

        // float has 4 bytes, coordinate * 4 bytes
        ByteBuffer vbb = ByteBuffer.allocateDirect(coords.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        _vertexBuffer = vbb.asFloatBuffer();

        // short has 2 bytes, indices * 2 bytes
        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        _indexBuffer = ibb.asShortBuffer();

        // float has 4 bytes, colors (RGBA) * 4 bytes
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        _colorBuffer = cbb.asFloatBuffer();

        _vertexBuffer.put(coords);
        _indexBuffer.put(indices);
        _colorBuffer.put(colors);

        _vertexBuffer.position(0);
        _indexBuffer.position(0);
        _colorBuffer.position(0);
    }

    public void draw(GL10 gl) {
        // define the vertices we want to draw
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, _colorBuffer);

        // finally draw the vertices
        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, _indexBuffer);
        //gl.glDrawArrays(GL10.GL_TRIANGLES, 0, _nrOfVertices);
    }

    public void moveLeft(){
        if(pos <= -18)
            return;
        for(int i=0;i< coords.length;i+=3){
            coords[i] -= 0.75f/15;
        }
        ByteBuffer vbb = ByteBuffer.allocateDirect(coords.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        _vertexBuffer = vbb.asFloatBuffer();
        _vertexBuffer.put(coords);
        _vertexBuffer.position(0);
        pos--;
    }

    public void moveRight(){
        if(pos >= 18)
            return;
        for(int i=0;i< coords.length;i+=3){
            coords[i] += 0.75f/15;
        }
        ByteBuffer vbb = ByteBuffer.allocateDirect(coords.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        _vertexBuffer = vbb.asFloatBuffer();
        _vertexBuffer.put(coords);
        _vertexBuffer.position(0);
        pos++;
    }

}
