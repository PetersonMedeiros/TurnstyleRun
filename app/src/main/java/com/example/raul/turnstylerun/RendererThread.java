package com.example.raul.turnstylerun;

import com.example.raul.turnstylerun.Renderer;

/**
 * Created by raulg on 28/06/2017.
 */

public class RendererThread extends Thread {

    private Renderer r;
    public boolean running = true;

    public RendererThread(Renderer r){
        this.r = r;
    }

    @Override
    public void run(){
        long time = System.currentTimeMillis();
        long time1 = time;
        while(running){
            long tempoAtual = System.currentTimeMillis();
            if(tempoAtual >=  time1 + (1000/60)){
                //r.s.requestRender();
                time1 = tempoAtual;
                r.s.requestRender();
            }

            if (tempoAtual >= time + 3000) {
                time = tempoAtual;
                r.addEnemies();
            }
        }

        for(int i=0;i<r.enemies.size();i++){
            r.enemies.get(i).running = false;
        }
        r.stap();
    }
}
