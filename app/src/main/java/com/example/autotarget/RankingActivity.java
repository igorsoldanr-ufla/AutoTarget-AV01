package com.example.autotarget;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class RankingActivity extends AppCompatActivity {

    private TextView txtListaRanking;
    private Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        // --- ADICIONADO: Manter o Modo Imersivo (Ecrã Inteiro) ---
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        // ---------------------------------------------------------

        txtListaRanking = findViewById(R.id.txtListaRanking);
        btnVoltar = findViewById(R.id.btnVoltar);

        btnVoltar.setOnClickListener(v -> finish()); // Fecha esta tela e volta ao jogo

        carregarRanking();
    }

    private void carregarRanking() {
        txtListaRanking.setText("A carregar dados da Cloud...");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Puxa as partidas ordenadas por quem teve mais "abates_esquerda" (o nosso lado)
        db.collection("partidas")
                .orderBy("abates_esquerda", Query.Direction.DESCENDING)
                .limit(10) // Traz apenas os 10 melhores
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StringBuilder construtorTexto = new StringBuilder();
                        int posicao = 1;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Extrair dados
                            Long abates = document.getLong("abates_esquerda");
                            String emailEncriptado = document.getString("jogador_encriptado");

                            // Desencriptar (Processo inverso da Segurança)
                            String emailReal = CriptografiaAES.desencriptar(emailEncriptado);

                            // Montar a linha de texto
                            construtorTexto.append(posicao).append("º Lugar: ")
                                    .append(emailReal)
                                    .append("\nAbates: ").append(abates)
                                    .append("\n--------------------------\n");
                            posicao++;
                        }

                        if (construtorTexto.length() == 0) {
                            txtListaRanking.setText("Ainda não há partidas registadas.");
                        } else {
                            txtListaRanking.setText(construtorTexto.toString());
                        }

                    } else {
                        txtListaRanking.setText("Erro ao carregar ranking.");
                        Toast.makeText(RankingActivity.this, "Falha de rede", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}