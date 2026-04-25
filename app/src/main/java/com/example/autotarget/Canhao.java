package com.example.autotarget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Canhao extends Thread {
    private float x, y;
    private Jogo jogo;
    private float largura = 60f;
    private float altura = 100f;
    private boolean ativo = true;

    public Canhao(float x, float y, Jogo jogo) {
        this.x = x;
        this.y = y;
        this.jogo = jogo;
    }

    @Override
    public void run() {
        while (ativo) {
            Alvo alvoMaisProximo = null;
            float menorDistancia = Float.MAX_VALUE;

            // Busca o alvo mais próximo sincronizando a lista para evitar erros
            synchronized (jogo.getAlvos()) {
                for (Alvo alvo : jogo.getAlvos()) {
                    float dx = alvo.getX() - this.x;
                    float dy = alvo.getY() - this.y;
                    float distancia = (float) Math.sqrt(dx * dx + dy * dy);

                    if (distancia < menorDistancia) {
                        menorDistancia = distancia;
                        alvoMaisProximo = alvo;
                    }
                }
            }

            // Se encontrou um alvo, atira!
            if (alvoMaisProximo != null) {
                // Cria o projétil passando a posição do canhão e do alvo
                Projetil tiro = new Projetil(this.x, this.y, alvoMaisProximo.getX(), alvoMaisProximo.getY());
                jogo.adicionarProjetil(tiro);
            }

            try {
                // Tempo de recarga do canhão (atira a cada 1 segundo)
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void desenhar(Canvas canvas, Paint paint) {
        paint.setColor(Color.DKGRAY); // Cor cinza escuro para o canhão
        // Desenha um retângulo representando o canhão, partindo do chão para cima
        canvas.drawRect(x - largura / 2, y - altura, x + largura / 2, y, paint);
    }

    public void destruir() {
        this.ativo = false;
    }

    public float getX() { return x; }
    public float getY() { return y; }
}