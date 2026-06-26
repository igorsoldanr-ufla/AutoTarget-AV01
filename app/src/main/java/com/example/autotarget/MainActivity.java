package com.example.autotarget;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

// Novos Imports para o Firebase e Concorrência (AV3)
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        Button btnRanking = findViewById(R.id.btnRanking); // <-- REFERÊNCIA DO NOVO BOTÃO

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

        // <-- LÓGICA DE CLIQUE DO BOTÃO DE RANKING -->
        btnRanking.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RankingActivity.class);
            startActivity(intent);
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
                salvarPartidaNoFirebase(); // <-- CHAMADA PARA GRAVAR DADOS ADICIONADA AQUI
            }
        }.start();
    }

    // NOVO MÉTODO: Grava os dados da partida na Nuvem de forma assíncrona
    private void salvarPartidaNoFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        // 1. Segurança Ciberfísica: Ocultar a identidade do jogador
        String emailOriginal = user.getEmail();
        String emailEncriptado = CriptografiaAES.encriptar(emailOriginal);

        // 2. Preparar o pacote de dados (JSON/Map)
        Map<String, Object> partida = new HashMap<>();
        partida.put("jogador_encriptado", emailEncriptado);
        partida.put("abates_esquerda", jogo.getAbatesEsq());
        partida.put("abates_direita", jogo.getAbatesDir());
        partida.put("timestamp", FieldValue.serverTimestamp()); // Hora exata do servidor

        // 3. Concorrência: Enviar para a cloud usando uma Thread separada (ExecutorService)
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("partidas").add(partida)
                    .addOnSuccessListener(documentReference -> {
                        // Executado se a gravação for um sucesso (Volta para a Thread da UI para mostrar o Toast)
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Partida gravada na Cloud!", Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e -> {
                        // Executado em caso de falha de rede
                        e.printStackTrace();
                    });
        });

        executor.shutdown();
    }
}