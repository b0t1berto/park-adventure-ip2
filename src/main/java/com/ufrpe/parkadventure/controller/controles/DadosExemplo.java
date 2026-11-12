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
import com.ufrpe.parkadventure.model.atracao.FilaVirtual;
import com.ufrpe.parkadventure.model.atracao.Infantil;
import com.ufrpe.parkadventure.model.atracao.Radical;
import com.ufrpe.parkadventure.model.ingresso.TipoIngresso;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.model.pessoa.Responsavel;
import com.ufrpe.parkadventure.model.pessoa.Visitante;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class DadosExemplo {
    private DadosExemplo() { }

    // Só o parque: as atrações e os operadores. É chamado na inicialização quando o arquivo de dados
    // ainda não tem atrações (não cria visitantes nem ingressos de teste).
    public static void carregarAtracoesPadrao() {
        Operador carlos = new Operador("Carlos", "22/01/1991", null, "Piratas do Caribe");
        Operador caique = new Operador("Caique", "22/01/2000", null, "Lula Voadora");
        Operador ivaldo = new Operador("Ivaldo", "20/01/1990", null, "Água Ardente");

        RepositorioAtracaoController.cadastrar(new Aquatico("Piratas do Caribe", 2, carlos, 130, 9));
        RepositorioAtracaoController.cadastrar(new Infantil("Lula Voadora", 3, caique, 140, 12));
        RepositorioAtracaoController.cadastrar(new Radical("Água Ardente", 20, ivaldo, 150, 16));
        RepositorioAtracaoController.cadastrar(new Radical("Montanha Fantasma", 10, null, 140, 12));
    }

    // Demonstração completa (visitantes, ingressos, ocupação, reservas e escala).
    // Não é chamada automaticamente: use só para testes manuais.
    public static void carregar() {
        LocalDate hoje = LocalDate.now();
        if (RepositorioAtracaoController.listarTodas().isEmpty()) {
            carregarAtracoesPadrao();
        }
        Atracao piratas = RepositorioAtracaoController.buscar("Piratas do Caribe");
        Operador carlos = piratas.getOperador();
        Operador caique = RepositorioAtracaoController.buscar("Lula Voadora").getOperador();
        Operador ivaldo = RepositorioAtracaoController.buscar("Água Ardente").getOperador();

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

        // Atração lotada + reservas do fura-fila (usa as regras reais do AcessoController)
        AcessoController acesso = ContextoController.CONTROLE_ACESSO;
        // Piratas do Caribe tem capacidade 2: as duas primeiras entradas a deixam lotada
        acesso.registrarAcesso(ana, RepositorioIngressoController.buscarUltimo(ana.getNome()), piratas);       // 1/2
        acesso.registrarAcesso(bruno, RepositorioIngressoController.buscarUltimo(bruno.getNome()), piratas);   // 2/2 (lotada)
        // Carla tenta entrar com a atração lotada: recebe ATRACAO_LOTADA e fica na fila física
        acesso.registrarAcesso(carla, RepositorioIngressoController.buscarUltimo(carla.getNome()), piratas);

        // primeiro horário de hoje cuja janela ainda não terminou (depois das 18:15 não há reservas de teste)
        LocalDateTime agora = LocalDateTime.now();
        LocalTime horario = null;
        for (LocalTime h : FilaVirtual.horarios()) {
            if (LocalDateTime.of(hoje, h).plus(FilaVirtual.TOLERANCIA).isAfter(agora)) {
                horario = h;
                break;
            }
        }
        if (horario != null) {
            acesso.reservarFuraFila(miguel, RepositorioIngressoController.buscarUltimo(miguel.getNome()), piratas, hoje, horario);
            acesso.reservarFuraFila(edivan, RepositorioIngressoController.buscarUltimo(edivan.getNome()), piratas, hoje, horario);
        }

        RepositorioEscalaDeTrabalhoController.escalar(hoje.plusDays(1), caique);
        RepositorioEscalaDeTrabalhoController.escalar(hoje.plusDays(2), carlos);
        RepositorioEscalaDeTrabalhoController.escalar(hoje.plusDays(2), ivaldo);
        RepositorioEscalaDeTrabalhoController.escalar(hoje.plusDays(6), caique);
        // feriados nacionais fixos: só entram se ainda não passaram no ano corrente (antes eram datas de 2026)
        LocalDate consciencia = LocalDate.of(hoje.getYear(), 11, 20);   // Consciência Negra
        LocalDate natal = LocalDate.of(hoje.getYear(), 12, 25);         // Natal
        if (!consciencia.isBefore(hoje)) {
            RepositorioEscalaDeTrabalhoController.escalar(consciencia, carlos);
            RepositorioEscalaDeTrabalhoController.escalar(consciencia, caique);
        }
        if (!natal.isBefore(hoje)) {
            RepositorioEscalaDeTrabalhoController.escalar(natal, ivaldo);
        }
        ContextoController.SESSAO.setVisitanteAtual(carla);
    }
}