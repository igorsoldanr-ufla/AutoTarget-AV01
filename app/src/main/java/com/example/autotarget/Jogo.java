package com.example.autotarget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Jogo {
    private List<Alvo> alvos;
    private List<Canhao> canhoes;
    private List<Projetil> projeteis;
    private VerificadorColisao verificadorColisao;
    private int larguraTela;
    private int alturaTela;

    public Jogo() {
        alvos = Collections.synchronizedList(new ArrayList<>());
        canhoes = Collections.synchronizedList(new ArrayList<>());
        projeteis = Collections.synchronizedList(new ArrayList<>());
    }

    public void setDimensoes(int largura, int altura) {
        this.larguraTela = largura;
        this.alturaTela = altura;
    }

    public void iniciarJogo() {
        alvos.clear();

        for (int i = 0; i < 3; i++) {
            AlvoComum ac = new AlvoComum(0, (float) (Math.random() * alturaTela), larguraTela, alturaTela);
            alvos.add(ac);
            ac.start();
        }

        for (int i = 0; i < 2; i++) {
            float xAleatorio = (float) (Math.random() * larguraTela);
            float yAleatorio = (float) (Math.random() * alturaTela);

            AlvoRapido ar = new AlvoRapido(xAleatorio, yAleatorio, larguraTela, alturaTela);
            alvos.add(ar);
            ar.start();
        }

        if (verificadorColisao != null) {
            verificadorColisao.destruir();
        }

        // Inicia a thread que vai monitorar os acertos
        verificadorColisao = new VerificadorColisao(this);
        verificadorColisao.start();
    }

    public List<Alvo> getAlvos() {
        return alvos;
    }

    public List<Canhao> getCanhoes() {
        return canhoes;
    }

    public List<Projetil> getProjeteis() {
        return projeteis;
    }

    public void adicionarProjetil(Projetil projetil) {
        projeteis.add(projetil);
        projetil.start(); // Inicia a thread do tiro
    }

    public void adicionarCanhao() throws JogoException {

        // Verifica a regra de negócio: limite máximo de 3 canhões
        if (canhoes.size() >= 3) {
            throw new JogoException("Limite máximo de 3 canhões atingido!");
        }

        float xAleatorio = (float) (Math.random() * (larguraTela - 100)) + 50;
        float yBase = alturaTela;

        Canhao novoCanhao = new Canhao(xAleatorio, yBase, this);
        canhoes.add(novoCanhao);
        novoCanhao.start(); // Inicia a Thread independente deste canhão específico
    }
}