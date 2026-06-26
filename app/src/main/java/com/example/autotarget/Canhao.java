package com.example.autotarget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.List;

public class Canhao extends Thread {
    private float x, y;
    private Jogo jogo;
    private boolean isEsquerda;
    private float largura = 60f;
    private float altura = 100f;
    private boolean ativo = true;
    private float xDestino;

    public Canhao(float x, float y, Jogo jogo, boolean isEsquerda) {
        this.x = x;
        this.y = y;
        this.jogo = jogo;
        this.isEsquerda = isEsquerda;
        this.xDestino = x;
    }

    @Override
    public void run() {
        while (ativo) {
            moverParaDestino();

            Alvo alvoMaisProximo = null;
            float menorDistancia = Float.MAX_VALUE;

            List<Alvo> alvosInimigos = isEsquerda ? jogo.getAlvosEsq() : jogo.getAlvosDir();

            synchronized (alvosInimigos) {
                for (Alvo alvo : alvosInimigos) {
                    float dx = alvo.getX() - this.x;
                    float dy = alvo.getY() - this.y;
                    float distancia = (float) Math.sqrt(dx * dx + dy * dy);

                    if (distancia < menorDistancia) {
                        menorDistancia = distancia;
                        alvoMaisProximo = alvo;
                    }
                }
            }

            int energiaAtual = isEsquerda ? jogo.getEnergiaEsq() : jogo.getEnergiaDir();

            if (alvoMaisProximo != null && energiaAtual > 0) {
                Projetil tiro = new Projetil(this.x, this.y, alvoMaisProximo.getX(), alvoMaisProximo.getY());
                if (isEsquerda) {
                    jogo.getProjeteisEsq().add(tiro);
                } else {
                    jogo.getProjeteisDir().add(tiro);
                }
                tiro.start();
            }

            // --- LÓGICA DE TEMPO DE RECARGA ---
            long tempoRecargaBase = 1000;
            int qtdCanhoesLado = isEsquerda ? jogo.getCanhoesEsq().size() : jogo.getCanhoesDir().size();

            // 1. Penalidade por sobrecarga de unidades (AV2)
            if (qtdCanhoesLado > 5) {
                int canhoesExtras = qtdCanhoesLado - 5;
                tempoRecargaBase += (long) (1000 * 0.20 * canhoesExtras);
            }

            // 2. NOVO: Termóstato de Segurança (Sistema Ciberfísico - AV3)
            // Se a temperatura do sistema passar de 40ºC, força um arrefecimento de +2 segundos
            if (jogo.getTemperatura() > 40.0) {
                tempoRecargaBase += 2000;
            }

            try {
                Thread.sleep(tempoRecargaBase);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void desenhar(Canvas canvas, Paint paint) {
        // Muda a cor visual do canhão para vermelho se estiver sobreaquecido
        if (jogo.getTemperatura() > 40.0) {
            paint.setColor(Color.RED);
        } else {
            paint.setColor(Color.DKGRAY);
        }
        canvas.drawRect(x - largura / 2, y - altura, x + largura / 2, y, paint);
    }

    public void destruir() {
        this.ativo = false;
    }

    public float getX() { return x; }
    public float getY() { return y; }

    public void setXDestino(float xDestino) {
        this.xDestino = xDestino;
    }

    private void moverParaDestino() {
        List<Alvo> alvosInimigos = isEsquerda ? jogo.getAlvosEsq() : jogo.getAlvosDir();

        if (alvosInimigos.isEmpty()) {
            this.xDestino = this.x;
            return;
        }

        float velocidadeMovimento = 3f;

        // Se estiver em modo de arrefecimento térmico (>40ºC), o motor também fica mais lento
        if (jogo.getTemperatura() > 40.0) {
            velocidadeMovimento = 1f;
        }

        if (Math.abs(this.x - xDestino) > velocidadeMovimento) {
            if (this.x < xDestino) {
                this.x += velocidadeMovimento;
            } else {
                this.x -= velocidadeMovimento;
            }
        }
    }
}