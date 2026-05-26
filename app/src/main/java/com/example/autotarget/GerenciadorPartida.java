package com.example.autotarget;

public class GerenciadorPartida extends Thread {
    private Jogo jogo;
    private boolean ativo = true;

    public GerenciadorPartida(Jogo jogo) {
        this.jogo = jogo;
    }

    @Override
    public void run() {
        while (ativo) {
            try {
                Thread.sleep(1000); // Roda a cada 1 segundo exato
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Consome 1 de energia para cada canhão ativo na tela
            jogo.decrementarEnergiaEsq(jogo.getCanhoesEsq().size());
            jogo.decrementarEnergiaDir(jogo.getCanhoesDir().size());
        }
    }

    public void destruir() {
        ativo = false;
    }
}