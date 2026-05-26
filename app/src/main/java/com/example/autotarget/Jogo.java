package com.example.autotarget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Jogo {

    private int energiaEsq = 100;
    private int energiaDir = 100;
    private int abatesEsq = 0;
    private int abatesDir = 0;

    // Listas do Lado A (Esquerda)
    private List<Alvo> alvosEsq;
    private List<Canhao> canhoesEsq;
    private List<Projetil> projeteisEsq;

    // Listas do Lado B (Direita)
    private List<Alvo> alvosDir;
    private List<Canhao> canhoesDir;
    private List<Projetil> projeteisDir;

    private int larguraTela;
    private int alturaTela;

    private VerificadorColisao verificadorColisao;
    private GerenciadorPartida gerenciadorPartida;
    private ThreadSensores threadSensores;
    private ThreadOtimizacao threadOtimizacao;
    private ThreadIA threadIA;

    public Jogo() {
        alvosEsq = Collections.synchronizedList(new ArrayList<>());
        canhoesEsq = Collections.synchronizedList(new ArrayList<>());
        projeteisEsq = Collections.synchronizedList(new ArrayList<>());

        alvosDir = Collections.synchronizedList(new ArrayList<>());
        canhoesDir = Collections.synchronizedList(new ArrayList<>());
        projeteisDir = Collections.synchronizedList(new ArrayList<>());
    }

    public void setDimensoes(int largura, int altura) {
        this.larguraTela = largura;
        this.alturaTela = altura;
    }

    public void iniciarJogo() {
        energiaEsq = 100;
        energiaDir = 100;
        abatesEsq = 0;
        abatesDir = 0;

        // 1. DESTRUIR AS THREADS ANTIGAS
        for (Alvo a : alvosEsq) a.destruir();
        for (Alvo a : alvosDir) a.destruir();

        for (Canhao c : canhoesEsq) c.destruir();
        for (Canhao c : canhoesDir) c.destruir();

        for (Projetil p : projeteisEsq) p.destruir();
        for (Projetil p : projeteisDir) p.destruir();

        // 2. LIMPAR AS LISTAS
        alvosEsq.clear();
        alvosDir.clear();
        canhoesEsq.clear();
        canhoesDir.clear();
        projeteisEsq.clear();
        projeteisDir.clear();

        // Cria alvos do Lado Esquerdo (4 Rápidos e 4 Comuns)
        for (int i = 0; i < 8; i++) {
            Alvo alvo;
            float xAleatorio = (float)(Math.random() * (larguraTela/2));
            float yAleatorio = (float) (Math.random() * alturaTela);

            if (i % 2 == 0) {
                alvo = new AlvoRapido(xAleatorio, yAleatorio, larguraTela, alturaTela);
            } else {
                alvo = new AlvoComum(xAleatorio, yAleatorio, larguraTela, alturaTela);
            }
            alvosEsq.add(alvo);
            alvo.start();
        }

        // Cria alvos do Lado Direito (4 Rápidos e 4 Comuns)
        for (int i = 0; i < 8; i++) {
            Alvo alvo;
            float xAleatorio = (float)(Math.random() * (larguraTela/2)) + (larguraTela/2);
            float yAleatorio = (float) (Math.random() * alturaTela);

            if (i % 2 == 0) {
                alvo = new AlvoRapido(xAleatorio, yAleatorio, larguraTela, alturaTela);
            } else {
                alvo = new AlvoComum(xAleatorio, yAleatorio, larguraTela, alturaTela);
            }
            alvosDir.add(alvo);
            alvo.start();
        }

        // Destrói as Threads de Controle Antigas
        if (verificadorColisao != null) verificadorColisao.destruir();
        if (threadSensores != null) threadSensores.destruir();
        if (threadOtimizacao != null) threadOtimizacao.destruir();
        if (threadIA != null) threadIA.destruir();
        if (gerenciadorPartida != null) gerenciadorPartida.destruir(); // Adicionado para garantir a limpeza total

        // Inicia as Novas Threads de Controle
        threadIA = new ThreadIA(this);
        threadIA.start();

        threadOtimizacao = new ThreadOtimizacao(this);
        threadOtimizacao.start();

        threadSensores = new ThreadSensores(this);
        threadSensores.start();

        verificadorColisao = new VerificadorColisao(this);
        verificadorColisao.start();

        gerenciadorPartida = new GerenciadorPartida(this);
        gerenciadorPartida.start();
    }

    public List<Alvo> getAlvosEsq() { return alvosEsq; }
    public List<Canhao> getCanhoesEsq() { return canhoesEsq; }
    public List<Projetil> getProjeteisEsq() { return projeteisEsq; }

    public List<Alvo> getAlvosDir() { return alvosDir; }
    public List<Canhao> getCanhoesDir() { return canhoesDir; }
    public List<Projetil> getProjeteisDir() { return projeteisDir; }

    public int getLarguraTela() { return larguraTela; }

    public void adicionarCanhao(boolean isEsquerda) throws JogoException {
        List<Canhao> listaCanhoes = isEsquerda ? canhoesEsq : canhoesDir;

        if (listaCanhoes.size() >= 10) {
            throw new JogoException("Limite máximo de canhões atingido neste lado!");
        }

        // Se for esquerda, nasce na metade esquerda. Se for direita, nasce na metade direita.
        float xAleatorio;
        if (isEsquerda) {
            xAleatorio = (float) (Math.random() * ((larguraTela / 2f) - 100)) + 50;
        } else {
            xAleatorio = (float) (Math.random() * ((larguraTela / 2f) - 100)) + (larguraTela / 2f) + 50;
        }

        float yBase = alturaTela;

        Canhao novoCanhao = new Canhao(xAleatorio, yBase, this, isEsquerda);
        listaCanhoes.add(novoCanhao);
        novoCanhao.start();
    }

    public int getEnergiaEsq() { return energiaEsq; }
    public int getEnergiaDir() { return energiaDir; }
    public int getAbatesEsq() { return abatesEsq; }
    public int getAbatesDir() { return abatesDir; }

    public void decrementarEnergiaEsq(int consumo) {
        energiaEsq = Math.max(0, energiaEsq - consumo); // Não deixa ficar negativo
    }
    public void decrementarEnergiaDir(int consumo) {
        energiaDir = Math.max(0, energiaDir - consumo);
    }

    public void incrementarAbateEsq() { abatesEsq++; }
    public void incrementarAbateDir() { abatesDir++; }
}