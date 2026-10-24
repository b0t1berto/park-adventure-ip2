package park_adventure;

import park_adventure.controller.ControleIngresso;
import park_adventure.controller.ControleAcesso;
import park_adventure.model.atracao.Aquatico;
import park_adventure.model.atracao.Infantil;
import park_adventure.model.atracao.Radical;
import park_adventure.model.pessoa.Operador;
import park_adventure.model.pessoa.Responsavel;
import park_adventure.model.pessoa.Visitante;
import park_adventure.repository.ListaAtracao;
import park_adventure.repository.ListaIngresso;
import park_adventure.repository.ListaVisitante;
import park_adventure.repository.Relatorio;
import park_adventure.view.AcessoView;
import park_adventure.view.AtracaoView;
import park_adventure.view.ConsoleView;
import park_adventure.view.IngressoView;
import park_adventure.view.MenuView;
import park_adventure.view.RelatorioView;
import park_adventure.view.VisitanteView;

// Monta o sistema (model + controller + view) e abre o menu interativo.
// Não tem regra de negócio nem entrada/saída: isso fica nos controllers e nas views.
public class Main {
    // Repositórios e controllers
    private static final ListaAtracao ATRACOES = new ListaAtracao();
    private static final ListaVisitante VISITANTES = new ListaVisitante();
    private static final ListaIngresso INGRESSOS = new ListaIngresso();
    private static final ControleIngresso INGRESSO = new ControleIngresso(INGRESSOS, 100.0, 800.0, 20.0);
    private static final ControleAcesso ACESSO = new ControleAcesso();
    private static final Relatorio RELATORIO = new Relatorio();

    // Views (todas usam o mesmo ConsoleView, que é o único dono do Scanner)
    private static final ConsoleView CONSOLE = new ConsoleView();
    private static final VisitanteView VISITANTE_VIEW = new VisitanteView(CONSOLE, VISITANTES);
    private static final AtracaoView ATRACAO_VIEW = new AtracaoView(CONSOLE, ATRACOES);
    private static final IngressoView INGRESSO_VIEW = new IngressoView(CONSOLE, INGRESSOS, INGRESSO, VISITANTES);
    private static final AcessoView ACESSO_VIEW = new AcessoView(CONSOLE, ATRACOES, VISITANTES, INGRESSOS, ACESSO);
    private static final RelatorioView RELATORIO_VIEW = new RelatorioView(CONSOLE, RELATORIO, ATRACOES, INGRESSOS);

    public static void main(String[] args) {
        if (CONSOLE.lerSimNao("Carregar dados de exemplo (atrações e visitantes)? (s/n): ")) {
            carregarDadosDeExemplo();
        }
        new MenuView(CONSOLE, VISITANTE_VIEW, ATRACAO_VIEW, INGRESSO_VIEW, ACESSO_VIEW, RELATORIO_VIEW).iniciar();
    }

    private static void carregarDadosDeExemplo() {
        Operador carlos = new Operador("Carlos", "22/01/1991", null, "Piratas do Caribe");
        Operador caique = new Operador("Caique", "22/01/2000", null, "Lula Voadora");
        Operador ivaldo = new Operador("Ivaldo", "20/01/1990", null, "Água Ardente");

        // Piratas do Caribe tem capacidade 2 de propósito, para a fila virtual aparecer logo
        ATRACOES.cadastrar(new Aquatico("Piratas do Caribe", 2, carlos, 130, 9));
        ATRACOES.cadastrar(new Infantil("Lula Voadora", 3, caique, 140, 12));
        ATRACOES.cadastrar(new Radical("Água Ardente", 20, ivaldo, 150, 16));

        Visitante miguel = new Visitante("Miguel Ferreira", "30/09/2016", null, 136);
        miguel.setResponsavel(new Responsavel("Alexssandra", "11/11/1998", null));
        VISITANTES.cadastrar(miguel);
        VISITANTES.cadastrar(new Visitante("Pedro Neto", "12/12/2014", null, 140)); // menor sem responsável
        VISITANTES.cadastrar(new Visitante("Edivan José", "21/04/2008", null, 181));
        VISITANTES.cadastrar(new Visitante("Ana Souza", "15/05/2001", null, 165));
        VISITANTES.cadastrar(new Visitante("Bruno Lima", "03/03/1995", null, 178));
        VISITANTES.cadastrar(new Visitante("Carla Dias", "20/08/2000", null, 160));
        VISITANTES.cadastrar(new Visitante("Diego Alves", "07/07/1992", null, 175));
    }
}
