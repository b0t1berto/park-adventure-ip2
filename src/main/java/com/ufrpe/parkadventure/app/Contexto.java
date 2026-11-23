package com.ufrpe.parkadventure.app;

import com.ufrpe.parkadventure.repository.RepositorioAtracao;
import com.ufrpe.parkadventure.repository.RepositorioEscalaTrabalho;
import com.ufrpe.parkadventure.repository.RepositorioIngresso;
import com.ufrpe.parkadventure.repository.RepositorioVisitante;
import com.ufrpe.parkadventure.service.*;

/**
 * Ponto central de acesso aos serviços da aplicação.
 * Esta é a única classe que instancia os repositórios e os serviços.
 * Todos os controllers de view devem acessar os serviços através desta classe.
 */
public final class Contexto {
    private Contexto() { }

    // --- Repositórios (somente aqui) ---
    private static final RepositorioAtracao repositorioAtracao = RepositorioAtracao.getInstancia();
    private static final RepositorioVisitante repositorioVisitante = RepositorioVisitante.getInstancia();
    private static final RepositorioIngresso repositorioIngresso = RepositorioIngresso.getInstancia();
    private static final RepositorioEscalaTrabalho repositorioEscala = RepositorioEscalaTrabalho.getInstancia();

    // --- Serviços de domínio ---
    public static final ServicoAtracao SERVICO_ATRACAO = new ServicoAtracao(repositorioAtracao);
    public static final ServicoVisitante SERVICO_VISITANTE = new ServicoVisitante(repositorioVisitante);
    public static final ServicoIngresso SERVICO_INGRESSO = new ServicoIngresso(repositorioIngresso);
    public static final ServicoEscala SERVICO_ESCALA = new ServicoEscala(repositorioEscala);
    public static final ServicoCapacidade SERVICO_CAPACIDADE = new ServicoCapacidade(SERVICO_ATRACAO, SERVICO_INGRESSO);

    // --- Serviços compostos ---
    public static final ServicoAcesso SERVICO_ACESSO = new ServicoAcesso(SERVICO_ATRACAO, SERVICO_INGRESSO);
    public static final ServicoAutenticacao SERVICO_AUTENTICACAO = new ServicoAutenticacao(SERVICO_ATRACAO, SERVICO_VISITANTE);
    public static final ServicoEmissaoIngresso SERVICO_EMISSAO_INGRESSO = new ServicoEmissaoIngresso(
            SERVICO_INGRESSO, 100.0, 800.0, 20.0);
    public static final ServicoPasseAnual SERVICO_PASSE_ANUAL = new ServicoPasseAnual(
            SERVICO_INGRESSO, SERVICO_CAPACIDADE, SERVICO_ATRACAO);
    public static final ServicoGeradorEscala SERVICO_GERADOR_ESCALA = new ServicoGeradorEscala(SERVICO_ATRACAO, SERVICO_ESCALA);
    public static final ServicoPerfilVisitante SERVICO_PERFIL_VISITANTE = new ServicoPerfilVisitante(
            SERVICO_VISITANTE, SERVICO_INGRESSO, SERVICO_ATRACAO, SERVICO_AUTENTICACAO);
    public static final ServicoGestaoAtracao SERVICO_GESTAO_ATRACAO = new ServicoGestaoAtracao(
            SERVICO_ATRACAO, SERVICO_ESCALA, SERVICO_AUTENTICACAO, SERVICO_GERADOR_ESCALA);
    public static final ServicoDemonstracao SERVICO_DEMONSTRACAO = new ServicoDemonstracao(
            SERVICO_ATRACAO, SERVICO_VISITANTE, SERVICO_AUTENTICACAO);

    // --- Sessão e feriados ---
    public static final ServicoFeriados FERIADOS = ServicoFeriados.getInstancia();
    public static final Sessao SESSAO = Sessao.getInstancia();
}
