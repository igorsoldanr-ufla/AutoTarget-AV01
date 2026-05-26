package com.example.autotarget;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Jogo jogo;
    private GameView gameView;
    private TextView txtTempo, txtEnergiaA, txtAbatesA, txtEnergiaB, txtAbatesB;
    private CountDownTimer timerJogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ativa o modo tela cheia e oculta a barra de navegação (Modo Imersivo)
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        jogo = new Jogo();

        FrameLayout container = findViewById(R.id.gameContainer);
        gameView = new GameView(this, jogo);
        container.addView(gameView);

        // Capturar dimensões do Canvas assim que ele for renderizado
        container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                jogo.setDimensoes(container.getWidth(), container.getHeight());
            }
        });

        // Referências do Placar
        txtTempo = findViewById(R.id.txtTempo);
        txtEnergiaA = findViewById(R.id.txtEnergiaA);
        txtAbatesA = findViewById(R.id.txtAbatesA);
        txtEnergiaB = findViewById(R.id.txtEnergiaB);
        txtAbatesB = findViewById(R.id.txtAbatesB);

        // Referências dos Botões
        Button btnIniciar = findViewById(R.id.btnIniciar);
        Button btnAddCanhaoEsq = findViewById(R.id.btnAdicionarCanhaoEsq);
        Button btnAddCanhaoDir = findViewById(R.id.btnAdicionarCanhaoDir);

        // Configuração dos cliques (Listeners)
        btnIniciar.setOnClickListener(v -> {
            jogo.iniciarJogo();
            iniciarTimer(); // Dispara o relógio quando o jogo começa
        });

        btnAddCanhaoEsq.setOnClickListener(v -> {
            try {
                jogo.adicionarCanhao(true); // TRUE = Esquerda
            } catch (JogoException e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnAddCanhaoDir.setOnClickListener(v -> {
            try {
                jogo.adicionarCanhao(false); // FALSE = Direita
            } catch (JogoException e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    } // Fim do onCreate

    private void iniciarTimer() {
        if (timerJogo != null) timerJogo.cancel();

        timerJogo = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                txtTempo.setText((millisUntilFinished / 1000) + "s");

                // Atualiza os textos do placar buscando os valores em tempo real
                txtEnergiaA.setText("Energia A: " + jogo.getEnergiaEsq());
                txtAbatesA.setText("Abates A: " + jogo.getAbatesEsq());

                txtEnergiaB.setText("Energia B: " + jogo.getEnergiaDir());
                txtAbatesB.setText("Abates B: " + jogo.getAbatesDir());
            }

            public void onFinish() {
                txtTempo.setText("FIM");
            }
        }.start();
    }
}