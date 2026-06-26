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
                // Executa estritamente a cada 10 segundos conforme requisito temporal
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!ativo) break;

            // Executa a otimização matemática e reconciliação matricial para ambos os lados
            processarOtimizacaoLado(jogo.getAlvosEsq(), jogo.getCanhoesEsq(), true);
            processarOtimizacaoLado(jogo.getAlvosDir(), jogo.getCanhoesDir(), false);
        }
    }

    private void processarOtimizacaoLado(List<Alvo> alvos, List<Canhao> canhoes, boolean isEsquerda) {
        if (canhoes.isEmpty()) return;

        int energiaAtual = isEsquerda ? jogo.getEnergiaEsq() : jogo.getEnergiaDir();

        // 1. CICLO DE OTIMIZAÇÃO: REMOVER POR UTILIDADE
        if (energiaAtual < 30 && canhoes.size() > alvos.size() && !alvos.isEmpty()) {
            synchronized (canhoes) {
                Canhao canhaoRemover = canhoes.remove(canhoes.size() - 1);
                canhaoRemover.destruir();
            }
        }

        if (alvos.isEmpty()) return;

        float[] alvosReconciliadosX = new float[alvos.size()];
        float[] alvosReconciliadosY = new float[alvos.size()];
        int totalAlvosProcessados = 0;

        // Variáveis para Análise de Tempo Real (Métricas Globais do Ciclo)
        float somaErroBrutoQuadratico = 0;
        float somaErroReconciliadoQuadratico = 0;
        int totalAmostrasCalculadas = 0;

        // 2. RECONCILIAÇÃO MATRICIAL DE DADOS (X e Y)
        synchronized (alvos) {
            for (int i = 0; i < alvos.size(); i++) {
                Alvo alvo = alvos.get(i);
                List<PointF> buffer = alvo.getBufferSensores();

                if (buffer.size() < 3) continue;

                synchronized (buffer) {
                    float mediaX = 0;
                    float mediaY = 0;
                    for (PointF p : buffer) {
                        mediaX += p.x;
                        mediaY += p.y;
                    }
                    mediaX /= buffer.size();
                    mediaY /= buffer.size();

                    float varX = 0;
                    float varY = 0;
                    float covXY = 0;
                    for (PointF p : buffer) {
                        varX += Math.pow(p.x - mediaX, 2);
                        varY += Math.pow(p.y - mediaY, 2);
                        covXY += (p.x - mediaX) * (p.y - mediaY);
                    }
                    int N = buffer.size() - 1;
                    varX /= N;
                    varY /= N;
                    covXY /= N;

                    float determinante = (varX * varY) - (covXY * covXY);
                    float xReconciliado;
                    float yReconciliado;

                    if (Math.abs(determinante) > 1E-5) {
                        float invVarX = varY / determinante;
                        float invVarY = varX / determinante;
                        float invCovXY = -covXY / determinante;

                        xReconciliado = mediaX - (varX * invVarX + covXY * invCovXY) * 0.05f * mediaX;
                        yReconciliado = mediaY - (covXY * invVarY + varY * invCovXY) * 0.05f * mediaY;
                    } else {
                        xReconciliado = mediaX;
                        yReconciliado = mediaY;
                    }

                    alvosReconciliadosX[totalAlvosProcessados] = xReconciliado;
                    alvosReconciliadosY[totalAlvosProcessados] = yReconciliado;
                    totalAlvosProcessados++;

                    // --- ANÁLISE EM TEMPO REAL: COMPARAÇÃO QUANTITATIVA ---
                    // Posição real exata do objeto na tela (Planta)
                    float xReal = alvo.getX();
                    float yReal = alvo.getY();

                    // Acumula os desvios em relação à realidade para todas as amostras do buffer
                    for (PointF p : buffer) {
                        somaErroBrutoQuadratico += Math.pow(p.x - xReal, 2) + Math.pow(p.y - yReal, 2);
                        totalAmostrasCalculadas++;
                    }
                    // Acumula o erro da posição após o filtro matricial de reconciliação
                    somaErroReconciliadoQuadratico += (Math.pow(xReconciliado - xReal, 2) + Math.pow(yReconciliado - yReal, 2)) * buffer.size();
                }
            }
        }

        // Exibe os resultados analíticos no Logcat a cada ciclo de 10 segundos
        if (totalAmostrasCalculadas > 0) {
            float mseBruto = somaErroBrutoQuadratico / totalAmostrasCalculadas;
            float mseReconciliado = somaErroReconciliadoQuadratico / totalAmostrasCalculadas;
            float percentualReducaoRuido = ((mseBruto - mseReconciliado) / mseBruto) * 100f;

            // Log estruturado para extração direta de dados para o relatório técnico
            android.util.Log.i("METRICAS_AV2", String.format(
                    "Lado %s -> Amostras: %d | MSE Bruto: %.2f | MSE Reconciliado: %.2f | Atenuação de Ruído: %.2f%%",
                    isEsquerda ? "Esquerdo" : "Direito", totalAmostrasCalculadas, mseBruto, mseReconciliado, percentualReducaoRuido
            ));
        }

        if (totalAlvosProcessados == 0) return;

        // 3. OTIMIZAÇÃO DE ATRIBUIÇÃO E MOVIMENTO INDIVIDUAL
        float limiteMeio = jogo.getLarguraTela() / 2f;

        synchronized (canhoes) {
            for (int c = 0; c < canhoes.size(); c++) {
                Canhao canhao = canhoes.get(c);
                float menorDistancia = Float.MAX_VALUE;
                float melhorDestinoX = canhao.getX();

                for (int a = 0; a < totalAlvosProcessados; a++) {
                    float dist = CalculosJogo.calcularDistancia(canhao.getX(), canhao.getY(), alvosReconciliadosX[a], alvosReconciliadosY[a]);
                    if (dist < menorDistancia) {
                        menorDistancia = dist;
                        melhorDestinoX = alvosReconciliadosX[a];
                    }
                }

                if (isEsquerda && melhorDestinoX > limiteMeio - 40) melhorDestinoX = limiteMeio - 40;
                if (!isEsquerda && melhorDestinoX < limiteMeio + 40) melhorDestinoX = limiteMeio + 40;

                canhao.setXDestino(melhorDestinoX);
            }
        }
    }

    public void destruir() {
        this.ativo = false;
    }
}