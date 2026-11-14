package com.ufrpe.parkadventure.model.pessoa;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

// Senhas nunca são guardadas em texto puro. Para cada senha: salt aleatório de 16 bytes + PBKDF2-HMAC-SHA256
// (65.000 iterações, chave de 256 bits). O arquivo de dados guarda só "salt$hash" em Base64. O hash não é reversível:
// no login a senha digitada é processada com o mesmo salt e comparada em tempo constante.
public final class Senha {
    private static final int ITERACOES = 65_000;
    private static final int BITS_DA_CHAVE = 256;
    private static final int BYTES_DO_SALT = 16;

    private Senha() { }

    public static String gerarHash(String senha) {
        byte[] salt = new byte[BYTES_DO_SALT];
        new SecureRandom().nextBytes(salt);
        Base64.Encoder b64 = Base64.getEncoder();
        return b64.encodeToString(salt) + "$" + b64.encodeToString(derivar(senha, salt));
    }

    public static boolean confere(String senha, String armazenado) {
        if (senha == null || armazenado == null) {
            return false;
        }
        String[] partes = armazenado.split("\\$");
        if (partes.length != 2) {
            return false;
        }
        try {
            byte[] salt = Base64.getDecoder().decode(partes[0]);
            byte[] esperado = Base64.getDecoder().decode(partes[1]);
            return MessageDigest.isEqual(esperado, derivar(senha, salt));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] derivar(String senha, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), salt, ITERACOES, BITS_DA_CHAVE);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Não foi possível processar a senha", e);
        }
    }
}
