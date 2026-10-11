package parkAdventure;

import java.time.LocalDate;

import parkAdventure.controller.Cadastro;
import parkAdventure.controller.ControleAcesso;
import parkAdventure.controller.ControleIngressos;
import parkAdventure.controller.ListaAtracao;
import parkAdventure.controller.ListaVisitante;
import parkAdventure.controller.ResultadoAcesso;
import parkAdventure.model.atracao.Aquatico;
import parkAdventure.model.atracao.Atracao;
import parkAdventure.model.atracao.Infantil;
import parkAdventure.model.atracao.Radical;
import parkAdventure.model.ingresso.Ingresso;
import parkAdventure.model.ingresso.TipoIngresso;
import parkAdventure.model.pessoa.Operador;
import parkAdventure.model.pessoa.Responsavel;
import parkAdventure.model.pessoa.Visitante;
import parkAdventure.view.AcessoView;
import parkAdventure.view.AtracaoView;
import parkAdventure.view.ConsoleView;
import parkAdventure.view.IngressoView;

// Monta o cenário e liga model, controller e view
public class Main {
    public static void main(String[] args) {
        // Model
        Operador operadorUm = new Operador("Carlos", "22/01/1991", null, "Água Ardente");
        Operador operadorDois = new Operador("Caique", "22/01/2000", null, "Lula Voadora");
        Operador operadorTres = new Operador("Ivaldo", "20/01/1990", null, "Água Ardente");

        Aquatico atracaoUm = new Aquatico("Piratas do Caribe", 12, operadorUm, 130, 9);
        Infantil atracaoDois = new Infantil("Lula Voadora", 10, operadorDois, 100, 10);
        Radical atracaoTres = new Radical("Água Ardente", 20, operadorTres, 150, 16);

        Visitante visitanteUm = new Visitante("Miguel Ferreira", "30/09/2016", null, 136);
        Visitante visitanteDois = new Visitante("Edivan José", "21/04/2008", null, 181);
        visitanteUm.setResponsavel(new Responsavel("Alexssandra", "11/11/1998", null));

        // Controllers
        ListaAtracao atracoes = new ListaAtracao();
        atracoes.cadastrar(atracaoUm);
        atracoes.cadastrar(atracaoDois);
        atracoes.cadastrar(atracaoTres);

        ListaVisitante visitantes = new ListaVisitante();
        visitantes.cadastrar(visitanteUm);
        visitantes.cadastrar(visitanteDois);

        ControleIngressos controleIngressos = new ControleIngressos();
        Cadastro cadastro = new Cadastro(controleIngressos, 100.0, 800.0, 20.0);
        ControleAcesso controleAcesso = new ControleAcesso();

        // Views
        AtracaoView atracaoView = new AtracaoView();
        IngressoView ingressoView = new IngressoView();
        AcessoView acessoView = new AcessoView();
        ConsoleView console = new ConsoleView();

        for (Atracao a : atracoes.listarTodas()) {
            atracaoView.exibirDetalhes(a);
            console.pularLinha();
        }

        LocalDate hoje = LocalDate.now();
        Ingresso ingressoUm = cadastro.emitirIngresso(visitanteUm, TipoIngresso.DIARIA, hoje);
        Ingresso ingressoDois = cadastro.emitirIngresso(visitanteDois, TipoIngresso.DIARIA, hoje);
        ingressoView.exibirIngresso(ingressoUm, cadastro.ehFeriado(hoje));
        ingressoView.exibirIngresso(ingressoDois, cadastro.ehFeriado(hoje));
        console.pularLinha();

        for (Atracao a : atracoes.listarTodas()) {
            ResultadoAcesso r1 = controleAcesso.registrarAcesso(visitanteUm, ingressoUm, a);
            acessoView.exibirResultado(visitanteUm, a, r1);
            ResultadoAcesso r2 = controleAcesso.registrarAcesso(visitanteDois, ingressoDois, a);
            acessoView.exibirResultado(visitanteDois, a, r2);
            console.pularLinha();
        }

        atracaoView.exibirOcupacao(atracaoUm);
        acessoView.exibirLiberacao(atracaoUm, controleAcesso.finalizarUso(visitanteUm, atracaoUm));
        atracaoView.exibirOcupacao(atracaoUm);
    }
}
