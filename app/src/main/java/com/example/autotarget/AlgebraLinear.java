package com.example.autotarget;

public class AlgebraLinear {
    // Resolve uma matriz 2x2 para simplificar
    public static float[][] inverterMatriz2x2(float[][] A) {
        float det = A[0][0] * A[1][1] - A[0][1] * A[1][0];
        float[][] inv = new float[2][2];
        inv[0][0] = A[1][1] / det;
        inv[0][1] = -A[0][1] / det;
        inv[1][0] = -A[1][0] / det;
        inv[1][1] = A[0][0] / det;
        return inv;
    }
}