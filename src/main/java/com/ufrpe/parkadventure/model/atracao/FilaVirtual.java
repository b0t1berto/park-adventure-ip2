package com.ufrpe.parkadventure.model.atracao;

import com.ufrpe.parkadventure.model.pessoa.Visitante;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.IntSupplier;

// Fura-fila FÍSICO: o visitante reserva um horário; no dia, apresenta-se ao operador
// dentro da janela [horário, horário + TOLERANCIA] e passa na frente da fila comum.
public class FilaVirtual {
    public enum ResultadoReserva { OK, HORARIO_INVALIDO, JA_RESERVOU_NESTA_ATRACAO, HORARIO_CHEIO }

    public static final Duration TOLERANCIA = Duration.ofMinutes(15);
    private static final LocalTime ABERTURA = LocalTime.of(11, 0);
    private static final LocalTime FECHAMENTO = LocalTime.of(18, 0);

    private final IntSupplier vagasPorHorario;
    private final List<Reserva> reservas = new ArrayList<>();

    public FilaVirtual(int vagasPorHorario) {
        this(() -> vagasPorHorario);
    }

    // Aceita um fornecedor para que a cota acompanhe a capacidade da atração, mesmo se ela for editada
    public FilaVirtual(IntSupplier vagasPorHorario) {
        this.vagasPorHorario = vagasPorHorario;
    }

    // Única fonte dos horários (a tela e a validação usam esta lista)
    public static List<LocalTime> horarios() {
        List<LocalTime> lista = new ArrayList<>();
        for (LocalTime h = ABERTURA; !h.isAfter(FECHAMENTO); h = h.plusMinutes(30)) {
            lista.add(h);
        }
        return lista;
    }

    public static boolean horarioValido(LocalTime horario) {
        return horario != null && horarios().contains(horario);
    }

    public int getVagasPorHorario() { return vagasPorHorario.getAsInt(); }

    public ResultadoReserva reservar(Visitante visitante, LocalDate data, LocalTime horario) {
        if (visitante == null || data == null || !horarioValido(horario)) {
            return ResultadoReserva.HORARIO_INVALIDO;
        }
        if (reservaAtiva(visitante, data) != null) {
            return ResultadoReserva.JA_RESERVOU_NESTA_ATRACAO;
        }
        if (vagasRestantes(data, horario) <= 0) {
            return ResultadoReserva.HORARIO_CHEIO;
        }
        reservas.add(new Reserva(visitante, data, horario));
        return ResultadoReserva.OK;
    }

    public boolean cancelar(Visitante visitante, LocalDate data) {
        Reserva r = reservaAtiva(visitante, data);
        if (r == null) {
            return false;
        }
        r.marcar(EstadoReserva.CANCELADA);
        return true;
    }

    public Reserva reservaAtiva(Visitante visitante, LocalDate data) {
        for (Reserva r : reservas) {
            if (r.estaAtiva() && r.getVisitante() == visitante && r.getData().equals(data)) {
                return r;
            }
        }
        return null;
    }

    public boolean temReservaEm(Visitante visitante, LocalDate data, LocalTime horario) {
        Reserva r = reservaAtiva(visitante, data);
        return r != null && r.getHorario().equals(horario);
    }

    public int vagasRestantes(LocalDate data, LocalTime horario) {
        long usadas = reservas.stream()
                .filter(r -> r.estaAtiva() && r.getData().equals(data) && r.getHorario().equals(horario))
                .count();
        return getVagasPorHorario() - (int) usadas;
    }

    // Reserva que PODE passar agora: ativa e dentro da janela. null se não pode.
    public Reserva elegivel(Visitante visitante, LocalDateTime agora) {
        Reserva r = reservaAtiva(visitante, agora.toLocalDate());
        if (r == null) {
            return null;
        }
        LocalDateTime inicio = LocalDateTime.of(r.getData(), r.getHorario());
        boolean dentro = !agora.isBefore(inicio) && !agora.isAfter(inicio.plus(TOLERANCIA));
        return dentro ? r : null;
    }

    public void marcarUsada(Reserva reserva) {
        reserva.marcar(EstadoReserva.USADA);
    }

    // Quem passou da tolerância sem aparecer vira NO_SHOW (libera a vaga)
    public int expirarNoShows(LocalDateTime agora) {
        int total = 0;
        for (Reserva r : reservas) {
            LocalDateTime fim = LocalDateTime.of(r.getData(), r.getHorario()).plus(TOLERANCIA);
            if (r.estaAtiva() && agora.isAfter(fim)) {
                r.marcar(EstadoReserva.NO_SHOW);
                total++;
            }
        }
        return total;
    }

    // Quem o operador pode esperar agora, em ordem de horário
    public List<Reserva> chamadaAtual(LocalDateTime agora) {
        List<Reserva> lista = new ArrayList<>();
        for (Reserva r : reservas) {
            if (elegivel(r.getVisitante(), agora) == r) {
                lista.add(r);
            }
        }
        lista.sort(Comparator.comparing(Reserva::getHorario));
        return lista;
    }

    // Todas as reservas de um dia (qualquer estado), em ordem de horário
    public List<Reserva> reservasDoDia(LocalDate data) {
        List<Reserva> lista = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getData().equals(data)) {
                lista.add(r);
            }
        }
        lista.sort(Comparator.comparing(Reserva::getHorario));
        return lista;
    }

    // Todas as reservas de um visitante (qualquer estado e data)
    public List<Reserva> reservasDe(Visitante visitante) {
        List<Reserva> lista = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getVisitante() == visitante) {
                lista.add(r);
            }
        }
        lista.sort(Comparator.comparing(Reserva::getData).thenComparing(Reserva::getHorario));
        return lista;
    }

    // Posição (1, 2, 3...) do visitante entre as reservas ativas do mesmo horário; 0 se não tiver
    public int posicaoNoHorario(Visitante visitante, LocalDate data) {
        Reserva alvo = reservaAtiva(visitante, data);
        if (alvo == null) {
            return 0;
        }
        int posicao = 0;
        for (Reserva r : reservas) {
            if (r.estaAtiva() && r.getData().equals(data) && r.getHorario().equals(alvo.getHorario())) {
                posicao++;
                if (r == alvo) {
                    return posicao;
                }
            }
        }
        return 0;
    }

    public int totalAtivas(LocalDate data) {
        return (int) reservas.stream().filter(r -> r.estaAtiva() && r.getData().equals(data)).count();
    }

    // Quantidade de reservas ativas (qualquer dia)
    public int tamanhoTotal() {
        return (int) reservas.stream().filter(Reserva::estaAtiva).count();
    }

    public List<Reserva> getReservas() {
        return Collections.unmodifiableList(reservas);
    }

    // Usado ao carregar o arquivo de dados: recoloca uma reserva com o estado que ela tinha
    public void restaurar(Visitante visitante, LocalDate data, LocalTime horario, EstadoReserva estado) {
        if (visitante == null || data == null || !horarioValido(horario) || estado == null) {
            return;
        }
        Reserva r = new Reserva(visitante, data, horario);
        r.marcar(estado);
        reservas.add(r);
    }

    // Usado quando uma atração é editada: a nova versão herda as reservas da antiga
    public void copiarReservasDe(FilaVirtual outra) {
        if (outra != null && outra != this) {
            reservas.addAll(outra.reservas);
        }
    }
}
