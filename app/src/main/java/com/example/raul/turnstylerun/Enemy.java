package com.example.raul.turnstylerun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLES32;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.threed.jpct.Object3D;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Raul on 14/05/2017.
 */
class Faces{

    float Vi;
    float Ni;
    float Ti;

    Faces(float Vi, float Ti, float Ni){
        this.Vi = Vi;
        this.Ti = Ti;
        this.Ni = Ni;
    }
}

public class Enemy {

    Context context;

    ArrayList<Float> vertices = new ArrayList<>();
    ArrayList<Float> normals = new ArrayList<>();
    ArrayList<Faces> faces = new ArrayList<>();
    
    // new object variables we need
    // a raw buffer to hold indices
    private ShortBuffer _indexBuffer;

    // a raw buffer to hold the vertices
    private FloatBuffer _vertexBuffer;
    private FloatBuffer _colorBuffer;
    private FloatBuffer _normalBuffer;

    private int _nrOfVertices = 0;
    private int _nrFaces = 0;

    private int pos = 0;

    boolean isVisible = true;

    public Enemy(Context context) {
        this.context = context;
        loadObjFile();

        Log.d("LOAD DEU CERTO", "FUCK YEAH");
    }

    public void draw(GL10 gl) {
        // define the vertices we want to draw

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);
        gl.glNormalPointer(GL10.GL_FLOAT, 0, _normalBuffer);

        // finally draw the vertices
        gl.glDrawElements(GL10.GL_TRIANGLES, _nrOfVertices, GL10.GL_UNSIGNED_SHORT, _indexBuffer);
        //gl.glDrawArrays(GL10.GL_TRIANGLES, 0, _nrOfVertices);
    }

    public void goDown() {

        for (int i = 1; i < vertices.size(); i += 3) {
            vertices.set(i, vertices.get(i) - 0.05f);
        }
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.size() * 4);
        vbb.order(ByteOrder.nativeOrder());
        _vertexBuffer = vbb.asFloatBuffer();

        for (int j = 0; j < faces.size(); j++) {
            _vertexBuffer.put(vertices.get((int) (faces.get(j).Vi * 3)));
            _vertexBuffer.put(vertices.get((int) (faces.get(j).Vi * 3 + 1)));
            _vertexBuffer.put(vertices.get((int) (faces.get(j).Vi * 3 + 2)));
        }

        _vertexBuffer.position(0);

        if (vertices.get(1) < -2.00f) isVisible = false;
    }


    private void loadObjFile() {

        try {
            AssetManager am = context.getAssets();
            String str;
            String[] temp;
            String[] ftemp;
            float v;

            BufferedReader inb = new BufferedReader(new InputStreamReader(am.open("catraca.obj")), 1024);

            while ((str = inb.readLine()) != null) {
                temp = str.split(" ");

                //vertices
                if (temp[0].equalsIgnoreCase("v")) {
                    for (int i = 1; i < 4; i++) {
                        v = Float.parseFloat(temp[i]);
                        vertices.add(v);
                    }
                }
                //normais
                if (temp[0].equalsIgnoreCase("vn")) {
                    for (int i = 1; i < 4; i++) {
                        v = Float.parseFloat(temp[i]);
                        normals.add(v);
                    }
                }
                //faces
                if (temp[0].equalsIgnoreCase("f")) {
                    for (int i = 1; i < 4; i++) {
                        ftemp = temp[i].split("/");

                        long vtx = Integer.parseInt(ftemp[0]) - 1;
                        int t = 0;
                        if (!ftemp[1].equals(""))
                            t = Integer.parseInt(ftemp[1]) - 1;
                        int n = Integer.parseInt(ftemp[2]) - 1;

                        faces.add(new Faces(vtx, t, n));
                    }
                    _nrFaces++;
                }
            }

            ByteBuffer vbb = ByteBuffer.allocateDirect(faces.size() * 4 * 3);
            vbb.order(ByteOrder.nativeOrder());
            _vertexBuffer = vbb.asFloatBuffer();

            ByteBuffer nbb = ByteBuffer.allocateDirect(faces.size() * 4 * 3);
            nbb.order(ByteOrder.nativeOrder());
            _normalBuffer = nbb.asFloatBuffer();

            _nrOfVertices = vertices.size();

            for (int j = 0; j < faces.size(); j++) {
                _vertexBuffer.put(vertices.get((int) (faces.get(j).Vi * 3)));
                _vertexBuffer.put(vertices.get((int) (faces.get(j).Vi * 3 + 1)));
                _vertexBuffer.put(vertices.get((int) (faces.get(j).Vi * 3 + 2)));

                _normalBuffer.put(normals.get((int) faces.get(j).Ni * 3));
                _normalBuffer.put(normals.get((int) (faces.get(j).Ni * 3) + 1));
                _normalBuffer.put(normals.get((int) (faces.get(j).Ni * 3) + 2));
            }

            _indexBuffer = ShortBuffer.allocate(faces.size());
            for (int j = 0; j < faces.size(); j++) {
                _indexBuffer.put((short) j);
            }

            _vertexBuffer.position(0);
            _normalBuffer.position(0);
            _indexBuffer.position(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
