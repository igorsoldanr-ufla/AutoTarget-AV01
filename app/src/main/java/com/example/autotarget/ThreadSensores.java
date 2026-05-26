package com.example.autotarget;

public class ThreadSensores extends Thread {
    private Jogo jogo;
    private boolean ativo = true;

    public ThreadSensores(Jogo jogo) {
        this.jogo = jogo;
    }

    @Override
    public void run() {
        while (ativo) {
            try {
                Thread.sleep(1000); // O radar varre a tela a cada 1 segundo exato
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Manda todos os alvos do Lado Esquerdo anotarem no diário onde estão
            synchronized(jogo.getAlvosEsq()) {
                for (Alvo alvo : jogo.getAlvosEsq()) {
                    alvo.registrarLeituraSensor();
                }
            }

            // Faz o mesmo para os alvos do Lado Direito
            synchronized(jogo.getAlvosDir()) {
                for (Alvo alvo : jogo.getAlvosDir()) {
                    alvo.registrarLeituraSensor();
                }
            }
        }
    }

    public void destruir() {
        this.ativo = false;
    }
}