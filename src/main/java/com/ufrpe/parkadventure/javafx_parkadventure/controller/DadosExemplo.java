package com.ufrpe.parkadventure.javafx_parkadventure.controller;

import com.ufrpe.parkadventure.javafx_parkadventure.model.atracao.Aquatico;
import com.ufrpe.parkadventure.javafx_parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.javafx_parkadventure.model.atracao.Infantil;
import com.ufrpe.parkadventure.javafx_parkadventure.model.atracao.Radical;
import com.ufrpe.parkadventure.javafx_parkadventure.model.ingresso.TipoIngresso;
import com.ufrpe.parkadventure.javafx_parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.javafx_parkadventure.model.pessoa.Responsavel;
import com.ufrpe.parkadventure.javafx_parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.javafx_parkadventure.repository.ListaVisitante;

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
        Contexto.ATRACOES.cadastrar(piratas);
        Contexto.ATRACOES.cadastrar(new Infantil("Lula Voadora", 3, caique, 140, 12));
        Contexto.ATRACOES.cadastrar(new Radical("Água Ardente", 20, ivaldo, 150, 16));
        Contexto.ATRACOES.cadastrar(new Radical("Montanha Fantasma", 10, null, 140, 12));

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
            ListaVisitante.cadastrar(v);
        }

        // Ingressos (o 4º parâmetro 'false' evita consultar a internet aqui)
        ControleIngresso ingressos = Contexto.CONTROLE_INGRESSO;
        ingressos.emitirIngresso(bruno, TipoIngresso.DIARIA, hoje.minusDays(10), false);   // expirado
        for (Visitante v : new Visitante[]{miguel, pedro, edivan, ana, bruno, carla}) {
            ingressos.emitirIngresso(v, TipoIngresso.DIARIA, hoje, false);
        }
        ingressos.emitirIngresso(ana, TipoIngresso.PASSE_ANUAL, hoje, false);

        // Atração lotada + fila virtual (usa as regras reais do ControleAcesso)
        ControleAcesso acesso = Contexto.CONTROLE_ACESSO;
        acesso.registrarAcesso(ana, Contexto.INGRESSOS.buscarUltimo(ana), piratas);       // 1/2
        acesso.registrarAcesso(bruno, Contexto.INGRESSOS.buscarUltimo(bruno), piratas);   // 2/2 (lotada)
        acesso.entrarNaFilaVirtual(miguel, Contexto.INGRESSOS.buscarUltimo(miguel), piratas, "13:30");
        acesso.entrarNaFilaVirtual(carla, Contexto.INGRESSOS.buscarUltimo(carla), piratas, "14:00");
        acesso.entrarNaFilaVirtual(edivan, Contexto.INGRESSOS.buscarUltimo(edivan), piratas, "14:00");

        Contexto.ESCALA.escalar(hoje.plusDays(1), caique);
        Contexto.ESCALA.escalar(hoje.plusDays(2), carlos);
        Contexto.ESCALA.escalar(hoje.plusDays(2), ivaldo);
        Contexto.ESCALA.escalar(hoje.plusDays(6), caique);
        Contexto.ESCALA.escalar(LocalDate.of(2026, 11, 20), carlos);   // Consciência Negra
        Contexto.ESCALA.escalar(LocalDate.of(2026, 11, 20), caique);
        Contexto.ESCALA.escalar(LocalDate.of(2026, 12, 25), ivaldo);   // Natal
        Contexto.SESSAO.setVisitanteAtual(carla);
    }
}