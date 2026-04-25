package com.example.autotarget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import java.util.List;

public class GameView extends View {
    private Jogo jogo;
    private Paint paint;

    public GameView(Context context, Jogo jogo) {
        super(context);
        this.jogo = jogo;
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        List<Alvo> alvos = jogo.getAlvos();
        List<Canhao> canhoes = jogo.getCanhoes();
        List<Projetil> projeteis = jogo.getProjeteis();

        synchronized (alvos) {
            for (Alvo alvo : alvos) {
                alvo.desenhar(canvas, paint);
            }
        }

        synchronized (canhoes) {
            for (Canhao canhao : canhoes) {
                canhao.desenhar(canvas, paint);
            }
        }

        synchronized (projeteis) {
            for (Projetil p : projeteis) {
                if (p.isAtivo()) {
                    p.desenhar(canvas, paint);
                }
            }
        }

        postInvalidate();
    }
}