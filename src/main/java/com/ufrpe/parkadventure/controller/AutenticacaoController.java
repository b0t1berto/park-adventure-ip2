package com.ufrpe.parkadventure.controller;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Locale;

import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.model.pessoa.Pessoa;
import com.ufrpe.parkadventure.model.pessoa.Visitante;

// Login por usuário e senha (administrador, operador e visitante) e regras das contas
public final class AutenticacaoController {
    private AutenticacaoController() { }

    public enum Papel { ADMIN, OPERADOR, VISITANTE }

    public record Resultado(Papel papel, Pessoa pessoa) { }

    // Conta do administrador (fixa; não é criada pela tela de cadastro)
    public static final String ADMIN_USUARIO = "leandro";
    private static final String ADMIN_SENHA = "admin123";

    // Senha inicial dos operadores criados automaticamente; o administrador pode trocá-la
    public static final String SENHA_PADRAO_OPERADOR = "operador123";

    public static final int TAMANHO_MINIMO_USUARIO = 3;
    public static final int TAMANHO_MINIMO_SENHA = 6;

    // null quando o usuário/senha não conferem
    public static Resultado entrar(String usuario, String senha) {
        if (usuario == null || senha == null || usuario.isBlank()) {
            return null;
        }
        String u = usuario.trim();
        if (u.equalsIgnoreCase(ADMIN_USUARIO)) {
            return iguais(senha, ADMIN_SENHA) ? new Resultado(Papel.ADMIN, null) : null;
        }
        Operador operador = RepositorioAtracaoController.buscarOperadorPorUsuario(u);
        if (operador != null && operador.senhaConfere(senha)) {
            return new Resultado(Papel.OPERADOR, operador);
        }
        Visitante visitante = RepositorioVisitanteController.buscarPorUsuario(u);
        if (visitante != null && visitante.senhaConfere(senha)) {
            return new Resultado(Papel.VISITANTE, visitante);
        }
        return null;
    }

    // Mensagem de erro, ou null se usuário e senha são aceitáveis
    public static String validarCredenciais(String usuario, String senha) {
        String u = usuario == null ? "" : usuario.trim();
        if (u.length() < TAMANHO_MINIMO_USUARIO || u.matches(".*\\s.*")) {
            return "O usuário deve ter pelo menos " + TAMANHO_MINIMO_USUARIO + " caracteres e não pode ter espaços.";
        }
        if (senha == null || senha.length() < TAMANHO_MINIMO_SENHA) {
            return "A senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.";
        }
        return null;
    }

    // O usuário é único entre visitantes, operadores e o administrador ('ignorar' = a própria pessoa, ao editar)
    public static boolean usuarioDisponivel(String usuario, Pessoa ignorar) {
        if (usuario == null || usuario.isBlank() || usuario.trim().equalsIgnoreCase(ADMIN_USUARIO)) {
            return false;
        }
        Operador operador = RepositorioAtracaoController.buscarOperadorPorUsuario(usuario);
        if (operador != null && operador != ignorar) {
            return false;
        }
        Visitante visitante = RepositorioVisitanteController.buscarPorUsuario(usuario);
        return visitante == null || visitante == ignorar;
    }

    // Toda atração precisa de operadores salvos, com usuário e senha. Cria o que faltar:
    //  - atração com menos de GeradorEscala.MIN_OPERADORES_POR_ATRACAO operadores -> "Operador <n> <atração>"
    //    (são necessários para cobrir os dois turnos todos os dias, com folga semanal)
    //  - operador sem credencial (dados antigos) -> usuário gerado a partir do nome e senha inicial
    // Retorna true se algo foi criado/alterado (para o chamador salvar).
    public static boolean garantirOperadoresDasAtracoes() {
        boolean alterou = false;
        for (Atracao atracao : RepositorioAtracaoController.listarTodas()) {
            while (atracao.getOperadores().size() < GeradorEscala.MIN_OPERADORES_POR_ATRACAO) {
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

    // "Operador <atração>" para o primeiro; "Operador 2 <atração>", ... para os demais (sem repetir nomes)
    private static String nomeLivre(Atracao atracao, int n) {
        while (true) {
            String nome = (n == 1 ? "Operador " : "Operador " + n + " ") + atracao.getNome();
            if (RepositorioAtracaoController.buscarOperadorPorNome(nome, atracao.getNome()) == null) {
                return nome;
            }
            n++;
        }
    }

    // Usuário livre a partir de um texto: minúsculas, sem acentos; acrescenta número se já existir
    public static String gerarUsuario(String base, Pessoa ignorar) {
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

    private static boolean iguais(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}
