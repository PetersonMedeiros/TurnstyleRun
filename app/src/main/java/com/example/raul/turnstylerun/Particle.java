package com.example.raul.turnstylerun;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by raulg on 02/07/2017.
 */

public class Particle {
    static float[] coords = {
            0.05f, -0.05f, 0.2f, //0
            0.05f, 0.05f, 0.2f, //1
            -0.05f, -0.05f, 0.2f, //2
            -0.05f, 0.05f, 0.2f, //3
            0.05f, -0.05f, 0f, //4
            0.05f, 0.05f, 0f, //5
            -0.05f, -0.05f, 0f, //6
            -0.05f, 0.05f, 0f  //7
    };

    static float[] colors = {
            0.93f, 0.91f, 0.66f, 0.4f, // point 0 blue
            0.93f, 0.91f, 0.66f, 0.4f, // point 1 blue
            0.93f, 0.91f, 0.66f, 0.4f, // point 2 blue
            0.93f, 0.91f, 0.66f, 0.4f, // point 3 blue
            0.93f, 0.91f, 0.66f, 0.4f, // point 4 blue
            0.93f, 0.91f, 0.66f, 0.4f, // point 5 blue
            0.93f, 0.91f, 0.66f, 0.4f, // point 6 blue
            0.93f, 0.91f, 0.66f, 0.4f  // point 7 blue
    };

    static short[] indices = new short[] {
            2,6,4,2,4,0, //face de baixo

            0,4,5,0,5,1, //face direita

            1,5,7,1,7,3, //face de cima

            3,7,6,3,6,2, //face esquerda

            6,5,7,6,4,5, //face da frente

            3,2,0,3,0,1  //face de trás
    };

    // new object variables we need
    // a raw buffer to hold indices
    static private ShortBuffer _indexBuffer;

    // a raw buffer to hold the vertices
    static private FloatBuffer _vertexBuffer;
    static private FloatBuffer _colorBuffer;

    private int mMVPMatrixHandle;
    private final int mProgram = GLES20.glCreateProgram();

    static private int _nrOfVertices = 0;

    public float posX;
    public float posY;
    public float posZ;
    private float speedX;
    private float speedY;
    private float speedZ;
    public int timeToDie = 20;

    public Particle(float posX, float posY, float posZ, float speedX, float speedY, float speedZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.speedX = speedX;
        this.speedY = speedY;
        this.speedZ = speedZ;

        if(_nrOfVertices == 0) {
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
    }

    public void draw(GL10 gl, float[] mvpMatrix) {
        if(timeToDie <= 0)
            return;

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);


        // define the vertices we want to draw

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);
        float rAux = 5;

        gl.glTranslatef(posX, posY, posZ);
        gl.glRotatef(rAux, 0, 0, 1);

        // finally draw the vertices
        gl.glDrawElements(GL10.GL_TRIANGLES, _nrOfVertices, GL10.GL_UNSIGNED_SHORT, _indexBuffer);
        //gl.glDrawArrays(GL10.GL_TRIANGLES, 0, _nrOfVertices);

        gl.glRotatef(-rAux, 0, 0, 1);
        gl.glTranslatef(-posX,-posY,-posZ);
        move();
    }

    public void move(){
        this.posX += speedX;
        this.posY += speedY;
        this.posZ += speedZ;
        timeToDie--;
    }
}
