package com.example.autotarget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class AlvoRapido extends Alvo {
    private float velocidadeX = 15f;
    private float velocidadeY = 10f;

    public AlvoRapido(float x, float y, int larguraTela, int alturaTela) {
        super(x, y, larguraTela, alturaTela);
        this.raio = 20f;
        this.velocidadeX = 15f * (Math.random() > 0.5 ? 1 : -1);
        this.velocidadeY = 10f * (Math.random() > 0.5 ? 1 : -1);
    }

    @Override
    public void mover() {
        x += velocidadeX;
        y += velocidadeY;

        if (x > larguraTela - raio || x < raio) velocidadeX *= -1;
        if (y > alturaTela - raio || y < raio) velocidadeY *= -1;
    }

    @Override
    public void desenhar(Canvas canvas, Paint paint) {
        paint.setColor(Color.RED);
        canvas.drawCircle(x, y, raio, paint);
    }
}