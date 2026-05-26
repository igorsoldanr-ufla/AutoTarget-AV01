package com.example.autotarget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import java.util.ArrayList;
import java.util.List;

public abstract class Alvo extends Thread {
    protected float x, y;
    protected float raio = 30f;
    protected boolean ativo = true;
    protected int larguraTela, alturaTela;

    // O buffer que guarda as últimas 10 leituras ruidosas do sensor
    private List<PointF> bufferSensores = new ArrayList<>();

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

    // Calcula a posição atual + ruído e guarda no buffer
    public void registrarLeituraSensor() {
        // Simula um ruído aleatório entre -5% e +5% na leitura atual
        float ruidoX = (float) ((Math.random() * 0.10) - 0.05) * this.x;
        float ruidoY = (float) ((Math.random() * 0.10) - 0.05) * this.y;

        float leituraX = this.x + ruidoX;
        float leituraY = this.y + ruidoY;

        // Região crítica: Bloqueia o buffer para não dar erro se lermos e escrevermos ao mesmo tempo
        synchronized(bufferSensores) {
            if (bufferSensores.size() >= 10) {
                bufferSensores.remove(0); // Apaga a leitura mais antiga (mantém apenas as 10 últimas)
            }
            bufferSensores.add(new PointF(leituraX, leituraY));
        }
    }

    // Permite que outras classes leiam o buffer deste alvo
    public List<PointF> getBufferSensores() { return bufferSensores; }


    public float getX() { return x; }
    public float getY() { return y; }
    public float getRaio() { return raio; }
}