package com.ufrpe.parkadventure.model.atracao;

import com.ufrpe.parkadventure.model.pessoa.Visitante;

import java.time.LocalDate;
import java.time.LocalTime;

// Um passe fura-fila: vale para UM horário de UM dia em UMA atração.
public class Reserva {
    private final Visitante visitante;
    private final LocalDate data;
    private final LocalTime horario;
    private EstadoReserva estado = EstadoReserva.RESERVADA;

    public Reserva(Visitante visitante, LocalDate data, LocalTime horario) {
        this.visitante = visitante;
        this.data = data;
        this.horario = horario;
    }

    public Visitante getVisitante() { return visitante; }
    public LocalDate getData() { return data; }
    public LocalTime getHorario() { return horario; }
    public EstadoReserva getEstado() { return estado; }
    public boolean estaAtiva() { return estado == EstadoReserva.RESERVADA; }

    // Só a FilaVirtual muda o estado (mesmo pacote)
    void marcar(EstadoReserva novo) { this.estado = novo; }
}