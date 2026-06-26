package com.example.autotarget;

import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CriptografiaAES {

    // Chave secreta de 16 bytes (128 bits) para o algoritmo AES
    private static final String CHAVE_SECRETA = "AutoTargetKey123";

    public static String encriptar(String dados) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(CHAVE_SECRETA.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] dadosEncriptados = cipher.doFinal(dados.getBytes());
            // Codificamos em Base64 para poder guardar como texto (String) no Firebase
            return Base64.encodeToString(dadosEncriptados, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro_Criptografia";
        }
    }

    public static String desencriptar(String dadosEncriptados) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(CHAVE_SECRETA.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] dadosDecodificados = Base64.decode(dadosEncriptados, Base64.DEFAULT);
            byte[] dadosOriginais = cipher.doFinal(dadosDecodificados);
            return new String(dadosOriginais);
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro_Descriptografia";
        }
    }
}