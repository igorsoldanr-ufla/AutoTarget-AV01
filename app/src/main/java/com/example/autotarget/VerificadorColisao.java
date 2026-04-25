package com.example.autotarget;

import java.util.Iterator;
import java.util.List;

public class VerificadorColisao extends Thread {
    private Jogo jogo;
    private boolean ativo = true;

    public VerificadorColisao(Jogo jogo) {
        this.jogo = jogo;
    }

    @Override
    public void run() {
        while (ativo) {
            verificarColisoes();

            try {
                // Checa as colisões a cada 30ms (acompanhando o FPS do jogo)
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void verificarColisoes() {
        List<Projetil> projeteis = jogo.getProjeteis();
        List<Alvo> alvos = jogo.getAlvos();

        // REGIÃO CRÍTICA: Bloqueia a lista de alvos e projéteis para modificação simultânea
        synchronized (alvos) {
            synchronized (projeteis) {
                // Usamos Iterator para poder remover elementos das listas de forma segura
                Iterator<Projetil> itProjetil = projeteis.iterator();

                while (itProjetil.hasNext()) {
                    Projetil p = itProjetil.next();
                    boolean acertou = false;

                    Iterator<Alvo> itAlvo = alvos.iterator();
                    while (itAlvo.hasNext()) {
                        Alvo a = itAlvo.next();

                        // Cálculo da distância euclidiana entre o centro do tiro e o centro do alvo
                        float dx = p.getX() - a.getX();
                        float dy = p.getY() - a.getY();
                        float distancia = (float) Math.sqrt(dx * dx + dy * dy);

                        // Se a distância for menor que a soma dos raios, houve colisão!
                        // Adicionamos 10 (raio do projetil) + raio do alvo
                        if (distancia < (10f + a.getRaio())) {
                            a.destruir(); // Para a thread do alvo
                            itAlvo.remove(); // Remove o alvo da lista
                            acertou = true;
                            break; // Se acertou, não precisa checar esse projétil contra outros alvos
                        }
                    }

                    // Se o projétil acertou ou saiu da tela, ele morre e é removido da lista
                    if (acertou || !p.isAtivo()) {
                        p.destruir(); // Para a thread do projétil
                        itProjetil.remove();
                    }
                }
            }
        }
    }

    public void destruir() {
        this.ativo = false;
    }
}