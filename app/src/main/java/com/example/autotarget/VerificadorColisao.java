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
            // Verifica colisões para ambos os lados
            verificarColisoesLado(jogo.getAlvosEsq(), jogo.getProjeteisEsq());
            verificarColisoesLado(jogo.getAlvosDir(), jogo.getProjeteisDir());

            // Verifica se algum alvo cruzou a linha do meio
            verificarTransferencias();

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void verificarColisoesLado(List<Alvo> alvos, List<Projetil> projeteis) {
        synchronized (alvos) {
            synchronized (projeteis) {
                Iterator<Projetil> itProjetil = projeteis.iterator();
                while (itProjetil.hasNext()) {
                    Projetil p = itProjetil.next();
                    boolean acertou = false;

                    Iterator<Alvo> itAlvo = alvos.iterator();
                    while (itAlvo.hasNext()) {
                        Alvo a = itAlvo.next();

                        // Substituição pelo cálculo unificado
                        float distancia = CalculosJogo.calcularDistancia(p.getX(), p.getY(), a.getX(), a.getY());

                        if (CalculosJogo.verificarColisao(distancia, a.getRaio(), p.getRaio())) {
                            a.destruir();
                            itAlvo.remove();
                            acertou = true;
                            if (alvos == jogo.getAlvosEsq()) jogo.incrementarAbateEsq();
                            else jogo.incrementarAbateDir();
                            break;
                        }
                    }

                    if (acertou || !p.isAtivo()) {
                        p.destruir();
                        itProjetil.remove();
                    }
                }
            }
        }
    }

    private void verificarTransferencias() {
        float meioTela = jogo.getLarguraTela() / 2f;

        // REGIÃO CRÍTICA: Bloqueia ambas as listas por ordem
        synchronized (jogo.getAlvosEsq()) {
            synchronized (jogo.getAlvosDir()) {

                // Da Esquerda para a Direita
                Iterator<Alvo> itEsq = jogo.getAlvosEsq().iterator();
                while (itEsq.hasNext()) {
                    Alvo a = itEsq.next();
                    if (a.getX() > meioTela) { // Cruzou o meio
                        itEsq.remove();
                        jogo.getAlvosDir().add(a);
                    }
                }

                // Da Direita para a Esquerda
                Iterator<Alvo> itDir = jogo.getAlvosDir().iterator();
                while (itDir.hasNext()) {
                    Alvo a = itDir.next();
                    if (a.getX() < meioTela) { // Cruzou o meio
                        itDir.remove();
                        jogo.getAlvosEsq().add(a);
                    }
                }
            }
        }
    }

    public void destruir() {
        this.ativo = false;
    }
}