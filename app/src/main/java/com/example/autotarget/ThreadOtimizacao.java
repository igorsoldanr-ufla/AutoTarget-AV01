package com.example.autotarget;

import android.graphics.PointF;
import java.util.List;

public class ThreadOtimizacao extends Thread {
    private Jogo jogo;
    private boolean ativo = true;

    public ThreadOtimizacao(Jogo jogo) {
        this.jogo = jogo;
    }

    @Override
    public void run() {
        while (ativo) {
            try {
                Thread.sleep(10000); // Roda estritamente a cada 10 segundos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Executa a otimização matemática para ambos os lados
            otimizarPosicionamento(jogo.getAlvosEsq(), jogo.getCanhoesEsq(), true);
            otimizarPosicionamento(jogo.getAlvosDir(), jogo.getCanhoesDir(), false);
        }
    }

    private void otimizarPosicionamento(List<Alvo> alvos, List<Canhao> canhoes, boolean isEsquerda) {
        if (alvos.isEmpty() || canhoes.isEmpty()) return;

        float somaXCorrigido = 0;
        int alvosProcessados = 0;

        synchronized (alvos) {
            for (Alvo alvo : alvos) {
                List<PointF> buffer = alvo.getBufferSensores();
                if (buffer.isEmpty()) continue;

                float somaLeiturasX = 0;

                // RECONCILIAÇÃO DE DADOS (Filtro Temporal)
                // A média temporal de um ruído Gaussiano de média zero converge para o valor real.
                synchronized (buffer) {
                    for (PointF ponto : buffer) {
                        somaLeiturasX += ponto.x;
                    }
                    float xReconciliado = somaLeiturasX / buffer.size();
                    somaXCorrigido += xReconciliado;
                    alvosProcessados++;
                }
            }
        }

        // CÁLCULO DO CENTROIDE (Ponto de Otimização)
        if (alvosProcessados > 0) {
            float centroideX = somaXCorrigido / alvosProcessados;

            // Restringe o destino para que os canhões não invadam o lado do adversário
            float limiteMeio = jogo.getLarguraTela() / 2f;
            if (isEsquerda && centroideX > limiteMeio - 30) centroideX = limiteMeio - 30;
            if (!isEsquerda && centroideX < limiteMeio + 30) centroideX = limiteMeio + 30;

            // Dispara o comando de set-point para as plantas (canhões)
            synchronized (canhoes) {
                for (Canhao c : canhoes) {
                    c.setXDestino(centroideX);
                }
            }
        }
    }

    public void destruir() {
        this.ativo = false;
    }
}