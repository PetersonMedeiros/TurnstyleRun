package com.example.raul.turnstylerun;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;


import com.mokiat.data.front.parser.IOBJParser;
import com.mokiat.data.front.parser.OBJDataReference;
import com.mokiat.data.front.parser.OBJFace;
import com.mokiat.data.front.parser.OBJMesh;
import com.mokiat.data.front.parser.OBJModel;
import com.mokiat.data.front.parser.OBJObject;
import com.mokiat.data.front.parser.OBJParser;
import com.threed.jpct.Object3D;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.text.MessageFormat;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Raul on 14/05/2017.
 */

public class Enemy {

    // new object variables we need
    // a raw buffer to hold indices
    private ShortBuffer _indexBuffer;

    // a raw buffer to hold the vertices
    private FloatBuffer _vertexBuffer;
    private FloatBuffer _normalBuffer;

    private int _nrOfVertices = 0;

    private int pos = 0;

    boolean isVisible = true;
    boolean status = false;

    float[] coords;
    float[] normals;
    short[] indices;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Enemy(Context context) {

        AssetManager am = context.getAssets();
        OBJModel model = null;

        try (InputStream in = am.open("c.obj")) {

            Log.d("TESTE: ", "objeto criado");
            // Create an OBJParser and parse the resource
            IOBJParser parser = new OBJParser();
            model = parser.parse(in);

            coords = new float[model.getVertices().size()*3];
            normals = new float[model.getNormals().size()*3];
            indices = new short[7201*3];

            for(int i = 0; i < model.getVertices().size(); i = i+3){
                coords[i] = model.getVertices().get(i).x;
                coords[i+1] = model.getVertices().get(i).y;
                coords[i+2] = model.getVertices().get(i).z;
            }

            for(int i = 0; i < model.getNormals().size(); i = i+3){
                normals[i] = model.getNormals().get(i).x;
                normals[i+1] = model.getNormals().get(i).y;
                normals[i+2] = model.getNormals().get(i).z;
            }
            System.out.println(model.getObjects().get(0).getMeshes().get(0).getFaces().get(0).getReferences().size());
            List<OBJFace> faces = model.getObjects().get(0).getMeshes().get(0).getFaces();
            int j = 0;
            for(OBJFace face:faces){
                for(int i = 0; i<3; i++){
                    indices[j] = (short)face.getReferences().get(i).vertexIndex;
                    System.out.println(indices[j]);
                    j++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        _nrOfVertices = model.getVertices().size();

        // float has 4 bytes, coordinate * 4 bytes
        ByteBuffer vbb = ByteBuffer.allocateDirect(model.getVertices().size() * 12);
        vbb.order(ByteOrder.nativeOrder());
        _vertexBuffer = vbb.asFloatBuffer();

        // float has 4 bytes, colors (RGBA) * 4 bytes
        ByteBuffer ibb = ByteBuffer.allocateDirect(7200*3*4);
        ibb.order(ByteOrder.nativeOrder());
        _indexBuffer = ibb.asShortBuffer();

        ByteBuffer nbb = ByteBuffer.allocateDirect(model.getNormals().size() * 12);
        nbb.order(ByteOrder.nativeOrder());
        _normalBuffer = nbb.asFloatBuffer();


        _vertexBuffer.put(coords);
        _normalBuffer.put(normals);
        _indexBuffer.put(indices);

        _vertexBuffer.position(0);
        _normalBuffer.position(0);
        _indexBuffer.position(0);
    }

    public void draw(GL10 gl) {
        // define the vertices we want to draw
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);
//        gl.glNormalPointer(GL10.GL_FLOAT, 0, _normalBuffer);

        System.out.println("Pointers definidos ===================================");
        
        // finally draw the vertices
//        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, _indexBuffer);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, _nrOfVertices*3);
        System.out.println("Draw acabaram ===================================");

    }

    public void moveLeft(){
        Log.d("Left pos: ", Integer.toString(pos));
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
        Log.d("Right pos: ", Integer.toString(pos));

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

    public void goDown(){

        for(int i=1;i< coords.length;i+=3){
            coords[i] -= 0.05f;
        }
        ByteBuffer vbb = ByteBuffer.allocateDirect(coords.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        _vertexBuffer = vbb.asFloatBuffer();
        _vertexBuffer.put(coords);
        _vertexBuffer.position(0);

        if(coords[1] < -2.00f) isVisible = false;
    }
}
