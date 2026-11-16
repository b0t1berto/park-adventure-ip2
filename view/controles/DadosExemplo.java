package com.ufrpe.parkadventure.view.controles;

import com.ufrpe.parkadventure.app.Contexto;
import com.ufrpe.parkadventure.model.atracao.Aquatico;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.atracao.FilaVirtual;
import com.ufrpe.parkadventure.model.atracao.Infantil;
import com.ufrpe.parkadventure.model.atracao.Radical;
import com.ufrpe.parkadventure.model.ingresso.Ingresso;
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

        Contexto.SERVICO_ATRACAO.cadastrar(new Aquatico("Piratas do Caribe", 2, carlos, 130, 9));
        Contexto.SERVICO_ATRACAO.cadastrar(new Infantil("Lula Voadora", 3, caique, 140, 12));
        Contexto.SERVICO_ATRACAO.cadastrar(new Radical("Água Ardente", 20, ivaldo, 150, 16));
        Contexto.SERVICO_ATRACAO.cadastrar(new Radical("Montanha Fantasma", 10, null, 140, 12));
    }

    // Demonstração completa (visitantes, ingressos, ocupação, reservas e escala).
    // Não é chamada automaticamente: use só para testes manuais.
    public static void carregar() {
        LocalDate hoje = LocalDate.now();
        if (Contexto.SERVICO_ATRACAO.listarTodas().isEmpty()) {
            carregarAtracoesPadrao();
        }
        Atracao piratas = Contexto.SERVICO_ATRACAO.buscar("Piratas do Caribe");
        Operador carlos = piratas.getOperador();
        Operador caique = Contexto.SERVICO_ATRACAO.buscar("Lula Voadora").getOperador();
        Operador ivaldo = Contexto.SERVICO_ATRACAO.buscar("Água Ardente").getOperador();

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
            Contexto.SERVICO_VISITANTE.cadastrar(v);
        }

        // Ingressos (o 4º parâmetro 'false' evita consultar a internet aqui)
        Contexto.SERVICO_EMISSAO_INGRESSO.emitirIngresso(bruno, TipoIngresso.DIARIA, hoje.minusDays(10), false);   // expirado
        for (Visitante v : new Visitante[]{miguel, pedro, edivan, ana, bruno, carla}) {
            Contexto.SERVICO_EMISSAO_INGRESSO.emitirIngresso(v, TipoIngresso.DIARIA, hoje, false);
        }
        Contexto.SERVICO_EMISSAO_INGRESSO.emitirIngresso(ana, TipoIngresso.PASSE_ANUAL, hoje, false);

        // Atração lotada + reservas do fura-fila (usa as regras reais do ServicoAcesso)
        Contexto.SERVICO_ACESSO.registrarAcesso(ana, Contexto.SERVICO_INGRESSO.buscarUltimo(ana.getNome()), piratas);       // 1/2
        Contexto.SERVICO_ACESSO.registrarAcesso(bruno, Contexto.SERVICO_INGRESSO.buscarUltimo(bruno.getNome()), piratas);   // 2/2 (lotada)
        // Carla tenta entrar com a atração lotada: recebe ATRACAO_LOTADA e fica na fila física
        Contexto.SERVICO_ACESSO.registrarAcesso(carla, Contexto.SERVICO_INGRESSO.buscarUltimo(carla.getNome()), piratas);

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
            Contexto.SERVICO_ACESSO.reservarFuraFila(miguel, Contexto.SERVICO_INGRESSO.buscarUltimo(miguel.getNome()), piratas, hoje, horario);
            Contexto.SERVICO_ACESSO.reservarFuraFila(edivan, Contexto.SERVICO_INGRESSO.buscarUltimo(edivan.getNome()), piratas, hoje, horario);
        }

        // escala dos próximos dias: operadores revezando nos turnos de manhã e tarde de cada atração
        Contexto.SERVICO_AUTENTICACAO.garantirOperadoresDasAtracoes(3);
        Contexto.SERVICO_GERADOR_ESCALA.gerarProximosDias();
        Contexto.SESSAO.setVisitanteAtual(carla);
    }
}