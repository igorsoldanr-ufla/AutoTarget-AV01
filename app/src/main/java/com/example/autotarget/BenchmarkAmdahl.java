package com.example.autotarget;

import android.util.Log;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BenchmarkAmdahl {

    public static void executarTesteBaterias() {
        int[] cenarios = {1, 10, 20, 50, 100};

        new Thread(() -> {
            Log.i("AMDAHL_OTIMIZADO", "--- INICIANDO BENCHMARK OTIMIZADO (THREAD POOL) ---");

            // OTIMIZAÇÃO: Descobre quantos núcleos físicos o telemóvel tem
            int nucleosCPU = Runtime.getRuntime().availableProcessors();
            Log.i("AMDAHL_OTIMIZADO", "Núcleos de CPU detetados: " + nucleosCPU);

            // Cria um "Pool" fixo de threads com base no hardware real do dispositivo
            ExecutorService pool = Executors.newFixedThreadPool(nucleosCPU);

            for (int qtdThreads : cenarios) {
                long tempoInicio = System.currentTimeMillis();
                CountDownLatch latch = new CountDownLatch(qtdThreads);

                // Em vez de dar start() numa nova Thread, submetemos a tarefa ao Pool
                for (int i = 0; i < qtdThreads; i++) {
                    pool.execute(() -> {
                        double resultado = 0;
                        for (int j = 0; j < 50000; j++) {
                            resultado += Math.sqrt(Math.pow(Math.random() * 100, 2) + Math.pow(Math.random() * 100, 2));
                        }
                        latch.countDown();
                    });
                }

                try {
                    latch.await();
                    long tempoTotal = System.currentTimeMillis() - tempoInicio;

                    Log.i("AMDAHL_OTIMIZADO", "Cenário: " + qtdThreads + " tarefas | Tempo de Processamento: " + tempoTotal + " ms");

                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            pool.shutdown(); // Limpa o pool de memória no final
            Log.i("AMDAHL_OTIMIZADO", "--- BENCHMARK CONCLUÍDO ---");
        }).start();
    }
}