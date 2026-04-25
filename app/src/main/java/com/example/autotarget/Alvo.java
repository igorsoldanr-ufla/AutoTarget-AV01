package com.example.autotarget;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class Alvo extends Thread {
    protected float x, y;
    protected float raio = 30f;
    protected boolean ativo = true;
    protected int larguraTela, alturaTela;

    public Alvo(float x, float y, int larguraTela, int alturaTela) {
        this.x = x;
        this.y = y;
        this.larguraTela = larguraTela;
        this.alturaTela = alturaTela;
    }

    public abstract void mover();

    public abstract void desenhar(Canvas canvas, Paint paint);

    @Override
    public void run() {
        while (ativo) {
            mover();
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void destruir() {
        this.ativo = false;
    }

    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public float getRaio() { return raio; }
}