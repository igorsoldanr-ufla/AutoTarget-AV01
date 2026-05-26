package com.example.autotarget;

import org.junit.Test;
import static org.junit.Assert.*;

public class CalculosJogoTest {

    @Test
    public void testeCalcularDistancia() {
        // Usei um triângulo pitagórico clássico (3, 4, 5) para testar
        // Ponto A(0,0) e Ponto B(3,4). A distância deve ser exatamente 5.
        float distancia = CalculosJogo.calcularDistancia(0, 0, 3, 4);

        // O terceiro parâmetro (0.001) é a margem de erro aceitável para números flutuantes
        assertEquals(5.0f, distancia, 0.001);
    }

    @Test
    public void testeColisaoOcorreu() {
        // Distância entre os centros = 10
        // Raio do Alvo = 8
        // Raio do Projétil = 5
        // Soma dos raios (13) é MAIOR que a distância (10), então TEM que colidir.
        boolean colidiu = CalculosJogo.verificarColisao(10f, 8f, 5f);

        assertTrue("Deveria ter detectado colisão", colidiu);
    }

    @Test
    public void testeColisaoNaoOcorreu() {
        // Distância entre os centros = 20
        // Raio do Alvo = 5
        // Raio do Projétil = 5
        // Soma dos raios (10) é MENOR que a distância (20), então NÃO PODE colidir.
        boolean colidiu = CalculosJogo.verificarColisao(20f, 5f, 5f);

        assertFalse("Não deveria ter detectado colisão", colidiu);
    }
}