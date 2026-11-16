package com.ufrpe.parkadventure.service;

import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.model.pessoa.Responsavel;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.repository.Persistencia;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de perfil dos visitantes: busca, edição dos dados e do acesso (usuário e senha) e remoção
 * com tudo que pertence ao visitante. Usado por "Meu Perfil" e pela tela de visitantes do administrador.
 */
public class ServicoPerfilVisitante {

    private final ServicoVisitante servicoVisitante;
    private final ServicoIngresso servicoIngresso;
    private final ServicoAtracao servicoAtracao;
    private final ServicoAutenticacao servicoAutenticacao;

    public ServicoPerfilVisitante(ServicoVisitante servicoVisitante, ServicoIngresso servicoIngresso,
                                  ServicoAtracao servicoAtracao, ServicoAutenticacao servicoAutenticacao) {
        this.servicoVisitante = servicoVisitante;
        this.servicoIngresso = servicoIngresso;
        this.servicoAtracao = servicoAtracao;
        this.servicoAutenticacao = servicoAutenticacao;
    }

    /**
     * O login do visitante; contas antigas, sem usuário, aparecem pelo nome de cadastro.
     */
    public String usuarioOuNome(Visitante visitante) {
        if (visitante.getUsuario() != null && !visitante.getUsuario().isBlank()) {
            return visitante.getUsuario();
        }
        return visitante.getNome();
    }

    /**
     * Procura pelo usuário (login) e, se não achar, pelo nome de cadastro.
     */
    public Visitante buscarPorUsuarioOuNome(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        Visitante achado = servicoVisitante.buscarPorUsuario(texto.trim());
        return achado != null ? achado : servicoVisitante.buscar(texto.trim());
    }

    /**
     * Visitantes cujo usuário ou nome contém o texto (sem diferenciar maiúsculas); vazio = todos.
     */
    public List<Visitante> buscarPorTrecho(String trecho) {
        String t = trecho == null ? "" : trecho.trim().toLowerCase();
        List<Visitante> achados = new ArrayList<>();
        for (Visitante v : servicoVisitante.listarTodos()) {
            if (t.isEmpty() || contem(usuarioOuNome(v), t) || contem(v.getNome(), t) || contem(v.getNomeCompleto(), t)) {
                achados.add(v);
            }
        }
        return achados;
    }

    /**
     * Confere os dados do perfil. Devolve a mensagem de erro, ou null se estiver tudo certo.
     * As mesmas regras do formulário de compra de ingresso (menor de idade precisa do responsável).
     */
    public String validarPerfil(String nomeCompleto, String cpf, LocalDate nascimento, int altura,
                                String email, String telefone, String nomeResponsavel,
                                String cpfResponsavel, String contatoEmergencia) {
        if (vazio(nomeCompleto)) return "Informe o nome completo.";
        if (digitos(cpf) != 11) return "CPF inválido.";
        if (nascimento == null) return "Informe a data de nascimento.";
        if (nascimento.isAfter(LocalDate.now())) return "A data de nascimento não pode ser no futuro.";
        if (altura < 50 || altura > 250) return "A altura deve estar entre 50 e 250 cm.";
        if (email == null || !email.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) return "E-mail inválido.";
        if (digitos(telefone) < 10) return "Telefone inválido.";
        if (Visitante.menorDeIdadeEm(nascimento)) {
            if (vazio(nomeResponsavel)) return "Menor de idade: informe o nome do responsável.";
            if (digitos(cpfResponsavel) != 11) return "CPF do responsável inválido.";
            if (vazio(contatoEmergencia)) return "Menor de idade: informe o contato de emergência.";
        }
        return null;
    }

    /**
     * Atualiza os dados do perfil (o nome de cadastro, que é a chave de ingressos e reservas, não muda).
     * Devolve null se salvou, ou a mensagem de erro.
     */
    public String atualizarPerfil(Visitante visitante, String nomeCompleto, String cpf, LocalDate nascimento,
                                  int altura, String email, String telefone, String nomeResponsavel,
                                  String cpfResponsavel, String contatoEmergencia) {
        if (visitante == null) {
            return "Selecione um visitante.";
        }
        String erro = validarPerfil(nomeCompleto, cpf, nascimento, altura, email, telefone,
                nomeResponsavel, cpfResponsavel, contatoEmergencia);
        if (erro != null) {
            return erro;
        }
        visitante.setNomeCompleto(nomeCompleto.trim());
        visitante.setCpf(cpf.trim());
        visitante.setDataNascimento(nascimento);
        visitante.setAltura(altura);
        visitante.setEmail(email.trim());
        visitante.setTelefone(telefone.trim());
        if (vazio(nomeResponsavel)) {
            visitante.setResponsavel(null);   // só chega aqui sendo maior de idade
        } else {
            Responsavel responsavel = new Responsavel(nomeResponsavel.trim(), (LocalDate) null,
                    cpfResponsavel == null ? null : cpfResponsavel.trim());
            responsavel.setContatoEmergencia(contatoEmergencia == null ? null : contatoEmergencia.trim());
            visitante.setResponsavel(responsavel);
        }
        Persistencia.salvar();
        return null;
    }

    /**
     * O próprio visitante troca a senha. Devolve null se trocou, ou a mensagem de erro.
     */
    public String alterarSenha(Visitante visitante, String senhaAtual, String novaSenha) {
        if (visitante == null) {
            return "Faça login para trocar a senha.";
        }
        if (!visitante.senhaConfere(senhaAtual)) {
            return "A senha atual está incorreta.";
        }
        String erro = validarSenha(novaSenha);
        if (erro != null) {
            return erro;
        }
        visitante.definirSenha(novaSenha);
        Persistencia.salvar();
        return null;
    }

    /**
     * O administrador define o usuário (login) e, se quiser, uma senha nova (também serve para contas
     * antigas que ficaram sem login). Devolve null se salvou, ou a mensagem de erro.
     */
    public String atualizarAcesso(Visitante visitante, String usuario, String novaSenha) {
        if (visitante == null) {
            return "Selecione um visitante.";
        }
        String u = usuario == null ? "" : usuario.trim();
        String erro = validarUsuario(u);
        if (erro != null) {
            return erro;
        }
        if (!servicoAutenticacao.usuarioDisponivel(u, visitante)) {
            return "Esse usuário não está disponível.";
        }
        boolean trocaSenha = novaSenha != null && !novaSenha.isEmpty();
        if (trocaSenha) {
            erro = validarSenha(novaSenha);
            if (erro != null) {
                return erro;
            }
        } else if (visitante.getSenhaHash() == null || visitante.getSenhaHash().isBlank()) {
            return "Esta conta ainda não tem senha: informe uma.";
        }
        visitante.setUsuario(u);
        if (trocaSenha) {
            visitante.definirSenha(novaSenha);
        }
        Persistencia.salvar();
        return null;
    }

    /**
     * Remove o visitante e tudo que pertence a ele (ingressos e reservas do fura-fila), para não sobrar dado órfão.
     */
    public boolean removerComDependencias(Visitante visitante) {
        if (visitante == null) {
            return false;
        }
        for (Ingresso ingresso : new ArrayList<>(servicoIngresso.listarPorVisitante(visitante.getNome()))) {
            servicoIngresso.remover(ingresso);
        }
        for (Atracao atracao : servicoAtracao.listarTodas()) {
            atracao.getFilaVirtual().removerReservasDe(visitante);
        }
        boolean removido = servicoVisitante.remover(visitante.getNome());
        if (removido) {
            Persistencia.salvar();
        }
        return removido;
    }

    // As mesmas regras de usuário e senha do login (ServicoAutenticacao)
    private String validarUsuario(String usuario) {
        String u = usuario == null ? "" : usuario.trim();
        if (u.length() < ServicoAutenticacao.TAMANHO_MINIMO_USUARIO || u.matches(".*\\s.*")) {
            return "O usuário deve ter pelo menos " + ServicoAutenticacao.TAMANHO_MINIMO_USUARIO
                    + " caracteres e não pode ter espaços.";
        }
        return null;
    }

    private String validarSenha(String senha) {
        if (senha == null || senha.length() < ServicoAutenticacao.TAMANHO_MINIMO_SENHA) {
            return "A senha deve ter pelo menos " + ServicoAutenticacao.TAMANHO_MINIMO_SENHA + " caracteres.";
        }
        return null;
    }

    private boolean contem(String texto, String trecho) {
        return texto != null && texto.toLowerCase().contains(trecho);
    }

    private boolean vazio(String texto) {
        return texto == null || texto.isBlank();
    }

    private int digitos(String texto) {
        return texto == null ? 0 : texto.replaceAll("\\D", "").length();
    }
}
