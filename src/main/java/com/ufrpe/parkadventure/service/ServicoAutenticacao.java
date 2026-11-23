package com.ufrpe.parkadventure.service;

import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.model.pessoa.Pessoa;
import com.ufrpe.parkadventure.model.pessoa.Visitante;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Locale;

/**
 * Serviço de autenticação: login, validação de credenciais, verificação de disponibilidade
 * de usuário e garantia de operadores nas atrações.
 */
public class ServicoAutenticacao {

    public enum Papel { ADMIN, OPERADOR, VISITANTE }

    public record Resultado(Papel papel, Pessoa pessoa) { }

    // Conta do administrador (fixa; não é criada pela tela de cadastro)
    public static final String ADMIN_USUARIO = "leandro";
    private static final String ADMIN_SENHA = "admin123";

    // Senha inicial dos operadores criados automaticamente
    public static final String SENHA_PADRAO_OPERADOR = "operador123";

    public static final int TAMANHO_MINIMO_USUARIO = 3;
    public static final int TAMANHO_MINIMO_SENHA = 6;

    private final ServicoAtracao servicoAtracao;
    private final ServicoVisitante servicoVisitante;

    public ServicoAutenticacao(ServicoAtracao servicoAtracao, ServicoVisitante servicoVisitante) {
        this.servicoAtracao = servicoAtracao;
        this.servicoVisitante = servicoVisitante;
    }

    /**
     * Tenta autenticar um usuário. Retorna null quando o usuário/senha não conferem.
     */
    public Resultado entrar(String usuario, String senha) {
        if (usuario == null || senha == null || usuario.isBlank()) {
            return null;
        }
        String u = usuario.trim();
        if (u.equalsIgnoreCase(ADMIN_USUARIO)) {
            return iguais(senha, ADMIN_SENHA) ? new Resultado(Papel.ADMIN, null) : null;
        }
        Operador operador = servicoAtracao.buscarOperadorPorUsuario(u);
        if (operador != null && operador.senhaConfere(senha)) {
            return new Resultado(Papel.OPERADOR, operador);
        }
        Visitante visitante = servicoVisitante.buscarPorUsuario(u);
        if (visitante != null && visitante.senhaConfere(senha)) {
            return new Resultado(Papel.VISITANTE, visitante);
        }
        return null;
    }

    /**
     * Mensagem de erro, ou null se usuário e senha são aceitáveis.
     */
    public String validarCredenciais(String usuario, String senha) {
        String u = usuario == null ? "" : usuario.trim();
        if (u.length() < TAMANHO_MINIMO_USUARIO || u.matches(".*\\s.*")) {
            return "O usuário deve ter pelo menos " + TAMANHO_MINIMO_USUARIO + " caracteres e não pode ter espaços.";
        }
        if (senha == null || senha.length() < TAMANHO_MINIMO_SENHA) {
            return "A senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.";
        }
        return null;
    }

    /**
     * Verifica se um usuário está disponível (único entre visitantes, operadores e o administrador).
     */
    public boolean usuarioDisponivel(String usuario, Pessoa ignorar) {
        if (usuario == null || usuario.isBlank() || usuario.trim().equalsIgnoreCase(ADMIN_USUARIO)) {
            return false;
        }
        Operador operador = servicoAtracao.buscarOperadorPorUsuario(usuario);
        if (operador != null && operador != ignorar) {
            return false;
        }
        Visitante visitante = servicoVisitante.buscarPorUsuario(usuario);
        return visitante == null || visitante == ignorar;
    }

    /**
     * Garante que toda atração tenha operadores com credenciais.
     * Retorna true se algo foi criado/alterado.
     */
    public boolean garantirOperadoresDasAtracoes(int minOperadoresPorAtracao) {
        boolean alterou = false;
        for (Atracao atracao : servicoAtracao.listarTodas()) {
            while (atracao.getOperadores().size() < minOperadoresPorAtracao) {
                int n = atracao.getOperadores().size() + 1;
                String nome = nomeLivre(atracao, n);
                atracao.adicionarOperador(new Operador(nome, "01/01/1990", null, atracao.getNome()));
                alterou = true;
            }
            for (Operador operador : atracao.getOperadores()) {
                if (!operador.temCredenciais()) {
                    operador.setUsuario(gerarUsuario(operador.getNome(), operador));
                    operador.definirSenha(SENHA_PADRAO_OPERADOR);
                    alterou = true;
                }
            }
        }
        return alterou;
    }

    /**
     * Gera um usuário livre a partir de um texto base.
     */
    public String gerarUsuario(String base, Pessoa ignorar) {
        String limpo = Normalizer.normalize(base == null ? "" : base, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
        if (limpo.length() < TAMANHO_MINIMO_USUARIO) {
            limpo = "operador" + limpo;
        }
        String candidato = limpo;
        for (int n = 2; !usuarioDisponivel(candidato, ignorar); n++) {
            candidato = limpo + n;
        }
        return candidato;
    }

    // "Operador <atração>" para o primeiro; "Operador 2 <atração>", ... para os demais
    private String nomeLivre(Atracao atracao, int n) {
        while (true) {
            String nome = (n == 1 ? "Operador " : "Operador " + n + " ") + atracao.getNome();
            if (servicoAtracao.buscarOperadorPorNome(nome, atracao.getNome()) == null) {
                return nome;
            }
            n++;
        }
    }

    private boolean iguais(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}
