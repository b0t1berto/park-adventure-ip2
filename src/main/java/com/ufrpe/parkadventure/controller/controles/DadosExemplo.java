package com.ufrpe.parkadventure.controller.controles;

import com.ufrpe.parkadventure.controller.AcessoController;
import com.ufrpe.parkadventure.controller.RepositorioAtracaoController;
import com.ufrpe.parkadventure.controller.RepositorioIngressoController;
import com.ufrpe.parkadventure.controller.RepositorioVisitanteController;
import com.ufrpe.parkadventure.controller.RepositorioEscalaDeTrabalhoController;
import com.ufrpe.parkadventure.controller.ContextoController;
import com.ufrpe.parkadventure.controller.IngressoController;
import com.ufrpe.parkadventure.model.atracao.Aquatico;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.atracao.Infantil;
import com.ufrpe.parkadventure.model.atracao.Radical;
import com.ufrpe.parkadventure.model.ingresso.TipoIngresso;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.model.pessoa.Responsavel;
import com.ufrpe.parkadventure.model.pessoa.Visitante;

import java.time.LocalDate;

public final class DadosExemplo {
    private DadosExemplo() { }

    public static void carregar() {
        LocalDate hoje = LocalDate.now();

        // Operadores e atrações
        Operador carlos = new Operador("Carlos", "22/01/1991", null, "Piratas do Caribe");
        Operador caique = new Operador("Caique", "22/01/2000", null, "Lula Voadora");
        Operador ivaldo = new Operador("Ivaldo", "20/01/1990", null, "Água Ardente");

        Atracao piratas = new Aquatico("Piratas do Caribe", 2, carlos, 130, 9);
        RepositorioAtracaoController.cadastrar(piratas);
        RepositorioAtracaoController.cadastrar(new Infantil("Lula Voadora", 3, caique, 140, 12));
        RepositorioAtracaoController.cadastrar(new Radical("Água Ardente", 20, ivaldo, 150, 16));
        RepositorioAtracaoController.cadastrar(new Radical("Montanha Fantasma", 10, null, 140, 12));

        // Visitantes
        Visitante miguel = new Visitante("Miguel Ferreira", "30/09/2016", null, 136);
        miguel.setResponsavel(new Responsavel("Alexssandra", "11/11/1998", null));
        Visitante pedro = new Visitante("Pedro Neto", "12/12/2014", null, 140);   // menor SEM responsável
        Visitante edivan = new Visitante("Edivan José", "21/04/2008", null, 181);
        Visitante ana = new Visitante("Ana Souza", "15/05/2001", null, 165);
        Visitante bruno = new Visitante("Bruno Lima", "03/03/1995", null, 178);
        Visitante carla = new Visitante("Carla Dias", "20/08/2000", null, 160);
        Visitante diego = new Visitante("Diego Alves", "07/07/1992", null, 175);   // sem ingresso
        for (Visitante v : new Visitante[]{miguel, pedro, edivan, ana, bruno, carla, diego}) {
            RepositorioVisitanteController.cadastrarVisitante(v);
        }

        // Ingressos (o 4º parâmetro 'false' evita consultar a internet aqui)
        IngressoController ingressos = ContextoController.CONTROLE_INGRESSO;
        ingressos.emitirIngresso(bruno, TipoIngresso.DIARIA, hoje.minusDays(10), false);   // expirado
        for (Visitante v : new Visitante[]{miguel, pedro, edivan, ana, bruno, carla}) {
            ingressos.emitirIngresso(v, TipoIngresso.DIARIA, hoje, false);
        }
        ingressos.emitirIngresso(ana, TipoIngresso.PASSE_ANUAL, hoje, false);

        // Atração lotada + fila virtual (usa as regras reais do ControleAcesso)
        AcessoController acesso = ContextoController.CONTROLE_ACESSO;
        acesso.registrarAcesso(ana, RepositorioIngressoController.buscarUltimo(ana.getNome()), piratas);       // 1/2
        acesso.registrarAcesso(bruno, RepositorioIngressoController.buscarUltimo(bruno.getNome()), piratas);   // 2/2 (lotada)
        acesso.entrarNaFilaVirtual(miguel, RepositorioIngressoController.buscarUltimo(miguel.getNome()), piratas, "13:30");
        acesso.entrarNaFilaVirtual(carla, RepositorioIngressoController.buscarUltimo(carla.getNome()), piratas, "14:00");
        acesso.entrarNaFilaVirtual(edivan, RepositorioIngressoController.buscarUltimo(edivan.getNome()), piratas, "14:00");

        RepositorioEscalaDeTrabalhoController.escalar(hoje.plusDays(1), caique);
        RepositorioEscalaDeTrabalhoController.escalar(hoje.plusDays(2), carlos);
        RepositorioEscalaDeTrabalhoController.escalar(hoje.plusDays(2), ivaldo);
        RepositorioEscalaDeTrabalhoController.escalar(hoje.plusDays(6), caique);
        RepositorioEscalaDeTrabalhoController.escalar(LocalDate.of(2026, 11, 20), carlos);   // Consciência Negra
        RepositorioEscalaDeTrabalhoController.escalar(LocalDate.of(2026, 11, 20), caique);
        RepositorioEscalaDeTrabalhoController.escalar(LocalDate.of(2026, 12, 25), ivaldo);   // Natal
        ContextoController.SESSAO.setVisitanteAtual(carla);
    }
}