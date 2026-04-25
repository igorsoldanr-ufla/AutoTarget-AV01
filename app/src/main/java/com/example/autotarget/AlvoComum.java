package com.example.autotarget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class AlvoComum extends Alvo {
    private float velocidadeX = 5f;

    public AlvoComum(float x, float y, int larguraTela, int alturaTela) {
        super(x, y, larguraTela, alturaTela);
    }

    @Override
    public void mover() {
        x += velocidadeX;
        if (x > larguraTela + raio) {
            x = -raio;
            y = (float) (Math.random() * alturaTela);
        }
    }

    @Override
    public void desenhar(Canvas canvas, Paint paint) {
        paint.setColor(Color.BLUE);
        canvas.drawCircle(x, y, raio, paint);
    }
}