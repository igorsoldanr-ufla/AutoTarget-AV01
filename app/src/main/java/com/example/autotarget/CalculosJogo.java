package com.example.autotarget;

public class CalculosJogo {

    // Metodo crítico 1: Calcula a distância entre dois pontos (Teorema de Pitágoras)
    public static float calcularDistancia(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    // Metodo crítico 2: Verifica se houve colisão baseada nos raios
    public static boolean verificarColisao(float distancia, float raioAlvo, float raioProjetil) {
        // Se a distância entre os centros for menor que a soma dos raios, eles se tocaram
        return distancia < (raioAlvo + raioProjetil);
    }
}