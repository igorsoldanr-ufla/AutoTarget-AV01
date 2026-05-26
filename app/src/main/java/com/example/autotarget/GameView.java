package com.example.autotarget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import java.util.List;

public class GameView extends View {
    private Jogo jogo;
    private Paint paint;
    private Paint paintLinha;

    public GameView(Context context, Jogo jogo) {
        super(context);
        this.jogo = jogo;
        paint = new Paint();
        paint.setAntiAlias(true);
        paintLinha = new Paint();
        paintLinha.setColor(Color.BLACK);
        paintLinha.setStrokeWidth(5f); // Espessura da linha
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawLine(getWidth() / 2f, 0, getWidth() / 2f, getHeight(), paintLinha);

        // --- DESENHAR ALVOS ---
        synchronized (jogo.getAlvosEsq()) {
            for (Alvo alvo : jogo.getAlvosEsq()) alvo.desenhar(canvas, paint);
        }
        synchronized (jogo.getAlvosDir()) {
            for (Alvo alvo : jogo.getAlvosDir()) alvo.desenhar(canvas, paint);
        }

        // --- DESENHAR CANHÕES ---
        synchronized (jogo.getCanhoesEsq()) {
            for (Canhao canhao : jogo.getCanhoesEsq()) canhao.desenhar(canvas, paint);
        }
        synchronized (jogo.getCanhoesDir()) {
            for (Canhao canhao : jogo.getCanhoesDir()) canhao.desenhar(canvas, paint);
        }

        // --- DESENHAR PROJÉTEIS ---
        synchronized (jogo.getProjeteisEsq()) {
            for (Projetil p : jogo.getProjeteisEsq()) if (p.isAtivo()) p.desenhar(canvas, paint);
        }
        synchronized (jogo.getProjeteisDir()) {
            for (Projetil p : jogo.getProjeteisDir()) if (p.isAtivo()) p.desenhar(canvas, paint);
        }

        postInvalidate();
    }
}