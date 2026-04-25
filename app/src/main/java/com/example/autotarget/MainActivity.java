package com.example.autotarget;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Jogo jogo;
    private GameView gameView;

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

        container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                jogo.setDimensoes(container.getWidth(), container.getHeight());
            }
        });

        Button btnIniciar = findViewById(R.id.btnIniciar);
        Button btnAddCanhao = findViewById(R.id.btnAdicionarCanhao);

        btnIniciar.setOnClickListener(v -> jogo.iniciarJogo());
        btnAddCanhao.setOnClickListener(v -> {
            try {
                // Tenta adicionar o canhão
                jogo.adicionarCanhao();
            } catch (JogoException e) {
                // Se o limite foi atingido, cai aqui e mostramos o erro para o usuário
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}