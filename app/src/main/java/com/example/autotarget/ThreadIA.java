package com.example.autotarget;

public class ThreadIA extends Thread {
    private Jogo jogo;
    private boolean ativo = true;

    public ThreadIA(Jogo jogo) {
        this.jogo = jogo;
    }

    @Override
    public void run() {
        while (ativo) {
            try {
                // A IA avalia o cenário a cada 4 segundos
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int qtdAlvos = jogo.getAlvosDir().size();
            int qtdCanhoes = jogo.getCanhoesDir().size();
            int energia = jogo.getEnergiaDir();

            // FUNÇÃO DE UTILIDADE (Custo-Benefício)
            // Benefício: Ameaça iminente (cada alvo na tela aumenta a urgência)
            double beneficio = qtdAlvos * 2.0;

            // Custo: Risco de zerar energia ou sofrer a penalidade de recarga (>5 canhões)
            double custo = (qtdCanhoes * 1.5) + (qtdCanhoes >= 5 ? 5.0 : 0);

            // A IA só constrói um canhão novo se o benefício superar o custo,
            // se tiver uma margem segura de energia (>30), e não tiver lotado a tela.
            if (beneficio > custo && energia > 30) {
                try {
                    // IA invoca um canhão para o seu lado (Direita = false)
                    jogo.adicionarCanhao(false);
                } catch (Exception e) {
                    // Ignora silenciosamente se o metodo disparar a JogoException de limite máximo
                }
            }
        }
    }

    public void destruir() {
        this.ativo = false;
    }
}