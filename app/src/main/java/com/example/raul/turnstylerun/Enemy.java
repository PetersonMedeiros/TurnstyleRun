package com.example.raul.turnstylerun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.util.Log;

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

public class Enemy extends Thread{

    Context context;
    ArrayList<Particle> particulas = new ArrayList<Particle>();

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

    private float posX;
    private float posY = 0.01f;
    private float posZ = 0.0015f;

    private float rotation = 0;

    private float[] model = new float[16];


    private int _nrOfVertices = 0;
    private int _nrFaces = 0;

    private int pos = 0;

    public int goal;

    boolean isVisible = true;

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    // Use to access and set the view transformation
    private int mMVPMatrixHandle;
    private final int mProgram = GLES20.glCreateProgram();

    Renderer r;

    boolean running = true;
    boolean ganhou = false;
    int animacao;

    public Enemy(Context context, Renderer r) {
        this.context = context;
        loadObjFile();
        this.r = r;

        Log.d("LOAD DEU CERTO", "FUCK YEAH");
    }

    @Override
    public void run(){
        long time = System.currentTimeMillis();
        while(running){
            long tempoAtual = System.currentTimeMillis();
            if (tempoAtual >= time + 5) {
                time = tempoAtual;
                goDown(r.mainChar.pos);
                if(animacao > 0){
                    rotate();
                    criarParticulas(10);
                    animacao--;
                }
            }
        }
    }

    public void criarParticulas(int num){
        if(particulas.size() > 300)
            return;
        Random r = new Random(System.currentTimeMillis());
        for(int i=0;i<num;i++){
            float pos1 = (r.nextFloat()* 0.25f) - 0.125f;
            float pos2 = (r.nextFloat()* 0.25f) - 0.125f;
            float pos3 = (r.nextFloat()* 0.25f) - 0.125f;
            if(goal == 1){
                goLeft();

            }else if(goal == 2){
                goRight();
            }
            float posX = this.posX;
            if(goal == 1){
                goRight();

            }else if(goal == 2){
                goLeft();
            }


            Particle p = new Particle(posX + pos1,posY + pos2,posZ + pos3,(r.nextFloat() * 0.02f) - 0.01f,(r.nextFloat() * 0.02f) - 0.01f,(r.nextFloat() * 0.02f) - 0.01f);
            particulas.add(p);
        }
    }

    public void draw(GL10 gl, float[] mvpMatrix) {
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);


        // define the vertices we want to draw

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);
        gl.glNormalPointer(GL10.GL_FLOAT, 0, _normalBuffer);
        float rAux = 0;
        if(goal == 0)
            rAux = rotation;

        gl.glTranslatef(posX, posY, posZ);
        gl.glRotatef(rAux, 0, 0, 1);

        // finally draw the vertices
        gl.glDrawElements(GL10.GL_TRIANGLES, _nrOfVertices, GL10.GL_UNSIGNED_SHORT, _indexBuffer);
        //gl.glDrawArrays(GL10.GL_TRIANGLES, 0, _nrOfVertices);

        gl.glRotatef(-rAux, 0, 0, 1);
        gl.glTranslatef(-posX,-posY,-posZ);

        rAux = 0;
        if(goal == 1)
            rAux = rotation;

        goLeft();
        gl.glTranslatef(posX, posY, posZ);
        gl.glRotatef(rAux, 0, 0, 1);

        // finally draw the vertices
        gl.glDrawElements(GL10.GL_TRIANGLES, _nrOfVertices, GL10.GL_UNSIGNED_SHORT, _indexBuffer);
        //gl.glDrawArrays(GL10.GL_TRIANGLES, 0, _nrOfVertices);

        gl.glRotatef(-rAux, 0, 0, 1);
        gl.glTranslatef(-posX,-posY,-posZ);
        goRight();

        rAux = 0;
        if(goal == 2)
            rAux = rotation;

        goRight();
        gl.glTranslatef(posX, posY, posZ);
        gl.glRotatef(rAux, 0, 0, 1);

        // finally draw the vertices
        gl.glDrawElements(GL10.GL_TRIANGLES, _nrOfVertices, GL10.GL_UNSIGNED_SHORT, _indexBuffer);
        //gl.glDrawArrays(GL10.GL_TRIANGLES, 0, _nrOfVertices);

        gl.glRotatef(-rAux, 0, 0, 1);
        gl.glTranslatef(-posX,-posY,-posZ);
        goLeft();
        ArrayList<Particle> remover = new ArrayList<Particle>();
        for(int i=0;i<particulas.size();i++){
            if(particulas.get(i).timeToDie <= 0){
                remover.add(particulas.get(i));
            }
            particulas.get(i).draw(gl, mvpMatrix);
        }
        particulas.removeAll(remover);
        remover.clear();
    }

    public void goUp(int val){
        posY += 0.01f * val;
        posZ += 0.0015f * val;
    }

    public void goDown(int charPos) {

//        for (int i = 1; i < vertices.size(); i += 3) {
//            vertices.set(i, vertices.get(i) - 0.01f);
//            vertices.set(i+1, vertices.get(i) - 0.0015f);
//        }
//        ByteBuffer vbb = ByteBuffer.allocateDirect(faces.size() * 4 * 3);
//        vbb.order(ByteOrder.nativeOrder());
//        _vertexBuffer = vbb.asFloatBuffer();
//
//        for (int j = 0; j < faces.size(); j++) {
//            _vertexBuffer.put(vertices.get((int) (faces.get(j).Vi * 3)));
//            _vertexBuffer.put(vertices.get((int) (faces.get(j).Vi * 3 + 1)));
//            _vertexBuffer.put(vertices.get((int) (faces.get(j).Vi * 3 + 2)));
//        }
//
//        _vertexBuffer.position(0);

        posY -= 0.01f;
        posZ -= 0.0015f;



        if(posY < -1f){
            Log.d(Integer.toString(charPos), Integer.toString(goal));
            if((goal == 1 && charPos < -6) || (goal == 0 && charPos <6 && charPos >= -6 ) || (goal == 2 && charPos >= 6)){
                this.animacao = 20;
                ganhou = true;
                r.pontuacao += 3.8f;
            }else if(!ganhou){
                r.rThread.running = false;
            }
        }
        if (posY < -2.50f) isVisible = false;
    }

    public void rotate(){
        rotation += 5;
    }

    public void goLeft(){
        posX += 0.6f;
//        for (int i = 0; i < vertices.size(); i += 3) {
//            vertices.set(i, vertices.get(i) - 0.55f);
//        }
//        ByteBuffer vbb = ByteBuffer.allocateDirect(faces.size() * 4 * 3);
//        vbb.order(ByteOrder.nativeOrder());
//        _vertexBuffer = vbb.asFloatBuffer();
//
//        for (int j = 0; j < faces.size(); j++) {
//            _vertexBuffer.put(vertices.get((int) (faces.get(j).Vi * 3)));
//            _vertexBuffer.put(vertices.get((int) (faces.get(j).Vi * 3 + 1)));
//            _vertexBuffer.put(vertices.get((int) (faces.get(j).Vi * 3 + 2)));
//        }
//
//        _vertexBuffer.position(0);
//
//        if (vertices.get(1) < -4.00f) isVisible = false;
    }

    public void goRight(){
        posX -= 0.6f;
//        for (int i = 0; i < vertices.size(); i += 3) {
//            vertices.set(i, vertices.get(i) + 0.55f);
//        }
//        ByteBuffer vbb = ByteBuffer.allocateDirect(faces.size() * 4 * 3);
//        vbb.order(ByteOrder.nativeOrder());
//        _vertexBuffer = vbb.asFloatBuffer();
//
//        for (int j = 0; j < faces.size(); j++) {
//            _vertexBuffer.put(vertices.get((int) (faces.get(j).Vi * 3)));
//            _vertexBuffer.put(vertices.get((int) (faces.get(j).Vi * 3 + 1)));
//            _vertexBuffer.put(vertices.get((int) (faces.get(j).Vi * 3 + 2)));
//        }
//
//        _vertexBuffer.position(0);
//
//        if (vertices.get(1) < -4.00f) isVisible = false;
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
                        vertices.add(v*0.069f);
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
