package com.example.autotarget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Projetil extends Thread {
    private float x, y;
    private float velocidadeX, velocidadeY;
    private float raio = 10f;
    private boolean ativo = true;

    // Construtor recebe a posição do canhão (origem) e do alvo (destino)
    public Projetil(float origemX, float origemY, float destinoX, float destinoY) {
        this.x = origemX;
        this.y = origemY;

        // Cálculo da direção usando trigonometria básica
        float deltaX = destinoX - origemX;
        float deltaY = destinoY - origemY;
        float distancia = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        // Velocidade fixa do projétil
        float velocidade = 25f;

        // Normaliza a direção e aplica a velocidade
        if (distancia > 0) {
            this.velocidadeX = (deltaX / distancia) * velocidade;
            this.velocidadeY = (deltaY / distancia) * velocidade;
        }
    }

    @Override
    public void run() {
        while (ativo) {
            x += velocidadeX;
            y += velocidadeY;

            // Se o projétil sair muito da tela, ele "morre" para não gastar memória
            if (x < -100 || x > 3000 || y < -100 || y > 3000) {
                ativo = false;
            }

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void desenhar(Canvas canvas, Paint paint) {
        paint.setColor(Color.YELLOW); // Projéteis amarelos
        canvas.drawCircle(x, y, raio, paint);
    }

    public void destruir() { this.ativo = false; }
    public boolean isAtivo() { return ativo; }
    public float getX() { return x; }
    public float getY() { return y; }
}