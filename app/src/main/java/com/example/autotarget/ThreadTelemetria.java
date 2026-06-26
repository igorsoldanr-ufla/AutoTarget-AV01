package com.example.autotarget;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ThreadTelemetria extends Thread {
    private Jogo jogo;
    private boolean ativo = true;

    public ThreadTelemetria(Jogo jogo) {
        this.jogo = jogo;
    }

    @Override
    public void run() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        while (ativo) {
            try {
                // O requisito pede a amostragem e envio a cada 10 segundos
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!ativo) break;

            // Simulação de Aquecimento: Base de 35ºC + (1.5ºC por cada canhão na tela) + ruído do sensor
            int qtdCanhoes = jogo.getCanhoesEsq().size() + jogo.getCanhoesDir().size();
            double novaTemperatura = 35.0 + (qtdCanhoes * 1.5) + (Math.random() * 2.0);

            // Atualiza a variável global do sistema
            jogo.setTemperatura(novaTemperatura);

            // Prepara pacote de dados para enviar para a Nuvem
            Map<String, Object> dados = new HashMap<>();
            dados.put("temperatura_celsius", novaTemperatura);
            dados.put("canhoes_ativos", qtdCanhoes);
            dados.put("timestamp", FieldValue.serverTimestamp());

            // Envio assíncrono para a coleção "telemetria"
            db.collection("telemetria").add(dados);
        }
    }

    public void destruir() {
        this.ativo = false;
    }
}