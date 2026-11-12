package com.ufrpe.parkadventure;

import com.ufrpe.parkadventure.controller.AcessoController;
import com.ufrpe.parkadventure.controller.IngressoController;
import com.ufrpe.parkadventure.controller.RepositorioAtracaoController;
import com.ufrpe.parkadventure.controller.controles.DadosExemplo;
import com.ufrpe.parkadventure.repository.Persistencia;
import com.ufrpe.parkadventure.repository.Relatorio;
import com.ufrpe.parkadventure.repository.RepositorioAtracao;
import com.ufrpe.parkadventure.repository.RepositorioIngresso;
import com.ufrpe.parkadventure.repository.RepositorioVisitante;
import com.ufrpe.parkadventure.view.AcessoView;
import com.ufrpe.parkadventure.view.AtracaoView;
import com.ufrpe.parkadventure.view.ConsoleView;
import com.ufrpe.parkadventure.view.IngressoView;
import com.ufrpe.parkadventure.view.MenuView;
import com.ufrpe.parkadventure.view.RelatorioView;
import com.ufrpe.parkadventure.view.VisitanteView;

// Versão de console do sistema. Usa os mesmos repositórios e o mesmo arquivo de dados da versão JavaFX.
public class Main {
    private static final ConsoleView CONSOLE = new ConsoleView();

    private static final RepositorioAtracao ATRACOES = RepositorioAtracao.getInstancia();
    private static final RepositorioVisitante VISITANTES = RepositorioVisitante.getInstancia();
    private static final RepositorioIngresso INGRESSOS = RepositorioIngresso.getInstancia();

    private static final IngressoController INGRESSO = new IngressoController(INGRESSOS, 100.0, 800.0, 20.0);
    private static final AcessoController ACESSO = AcessoController.getInstancia();
    private static final Relatorio RELATORIO = new Relatorio();

    private static final VisitanteView VISITANTE_VIEW = new VisitanteView(CONSOLE);
    private static final AtracaoView ATRACAO_VIEW = new AtracaoView(CONSOLE, ATRACOES);
    private static final IngressoView INGRESSO_VIEW = new IngressoView(CONSOLE, INGRESSOS, VISITANTES);
    private static final AcessoView ACESSO_VIEW = new AcessoView(CONSOLE, ACESSO);
    private static final RelatorioView RELATORIO_VIEW = new RelatorioView(CONSOLE, RELATORIO, ATRACOES, INGRESSOS);

    public static void main(String[] args) {
        Persistencia.carregar();

        if (RepositorioAtracaoController.listarTodas().isEmpty()) {
            DadosExemplo.carregarAtracoesPadrao();
        }
        if (CONSOLE.lerSimNao("Carregar dados de demonstração (visitantes, ingressos e escala)? (s/n): ")) {
            DadosExemplo.carregar();
        }

        try {
            new MenuView(CONSOLE, VISITANTE_VIEW, ATRACAO_VIEW, INGRESSO_VIEW, ACESSO_VIEW, RELATORIO_VIEW).iniciar();
        } finally {
            Persistencia.salvar();
        }
    }
}
