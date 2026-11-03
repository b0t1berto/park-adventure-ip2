package com.ufrpe.parkadventure;

import com.ufrpe.parkadventure.controller.AcessoController;
import com.ufrpe.parkadventure.controller.IngressoController;
import com.ufrpe.parkadventure.model.atracao.Aquatico;
import com.ufrpe.parkadventure.model.atracao.Infantil;
import com.ufrpe.parkadventure.model.atracao.Radical;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.model.pessoa.Responsavel;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.repository.ListaAtracao;
import com.ufrpe.parkadventure.repository.ListaIngresso;
import com.ufrpe.parkadventure.repository.ListaVisitante;
import com.ufrpe.parkadventure.repository.Relatorio;
// import recom.ufrpe.parkadventure.view.AcessoView;
// import com.ufrpe.parkadventure.view.AtracaoView;
// import com.ufrpe.parkadventure.view.ConsoleView;
// import com.ufrpe.parkadventure.view.IngressoView;
// import com.ufrpe.parkadventure.view.MenuView;
// import com.ufrpe.parkadventure.view.RelatorioView;
// import com.ufrpe.parkadventure.view.VisitanteView;

public class Main {
    private static final ListaAtracao ATRACOES = new ListaAtracao();
    private static final ListaVisitante VISITANTES = new ListaVisitante();
    private static final ListaIngresso INGRESSOS = new ListaIngresso();
    private static final IngressoController INGRESSO = new IngressoController(INGRESSOS, 100.0, 800.0, 20.0);
    private static final AcessoController ACESSO = new AcessoController();
    private static final Relatorio RELATORIO = new Relatorio();

    public static void main(String[] args) {
        //     if (CONSOLE.lerSimNao("Carregar dados de exemplo (atrações e visitantes)? (s/n): ")) {
        //         carregarDadosDeExemplo();
        //     }
        //     new MenuView(CONSOLE, VISITANTE_VIEW, ATRACAO_VIEW, INGRESSO_VIEW, ACESSO_VIEW, RELATORIO_VIEW).iniciar();
        // }

        // private static void carregarDadosDeExemplo() {
        //     Operador carlos = new Operador("Carlos", "22/01/1991", null, "Piratas do Caribe");
        //     Operador caique = new Operador("Caique", "22/01/2000", null, "Lula Voadora");
        //     Operador ivaldo = new Operador("Ivaldo", "20/01/1990", null, "Água Ardente");

        //     // Piratas do Caribe tem capacidade 2 de propósito, para a fila virtual aparecer logo
        //     ATRACOES.cadastrar(new Aquatico("Piratas do Caribe", 2, carlos, 130, 9));
        //     ATRACOES.cadastrar(new Infantil("Lula Voadora", 3, caique, 140, 12));
        //     ATRACOES.cadastrar(new Radical("Água Ardente", 20, ivaldo, 150, 16));

        //     Visitante miguel = new Visitante("Miguel Ferreira", "30/09/2016", null, 136);
        //     miguel.setResponsavel(new Responsavel("Alexssandra", "11/11/1998", null));
        //     VISITANTES.cadastrar(miguel);
        //     VISITANTES.cadastrar(new Visitante("Pedro Neto", "12/12/2014", null, 140)); // menor sem responsável
        //     VISITANTES.cadastrar(new Visitante("Edivan José", "21/04/2008", null, 181));
        //     VISITANTES.cadastrar(new Visitante("Ana Souza", "15/05/2001", null, 165));
        //     VISITANTES.cadastrar(new Visitante("Bruno Lima", "03/03/1995", null, 178));
        //     VISITANTES.cadastrar(new Visitante("Carla Dias", "20/08/2000", null, 160));
        //     VISITANTES.cadastrar(new Visitante("Diego Alves", "07/07/1992", null, 175));

    }
}
