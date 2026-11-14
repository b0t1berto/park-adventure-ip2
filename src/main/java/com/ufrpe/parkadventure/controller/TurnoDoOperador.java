package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.repository.RepositorioEscalaTrabalho.Turno;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// O operador só controla a atração durante o PRÓPRIO turno da escala (hoje, entre o início e o fim do turno).
public final class TurnoDoOperador {
    private TurnoDoOperador() { }

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM");

    // Turno em andamento agora para esse operador; null se não há
    public static Turno turnoEmAndamento(Operador operador, LocalDateTime agora) {
        LocalTime hora = agora.toLocalTime();
        for (Turno turno : RepositorioEscalaDeTrabalhoController.turnosDoDia(agora.toLocalDate())) {
            if (turno.operador() == operador
                    && !hora.isBefore(turno.periodo().getInicio()) && hora.isBefore(turno.periodo().getFim())) {
                return turno;
            }
        }
        return null;
    }

    // Mensagem explicando por que o operador não pode controlar a atração agora; null se pode
    public static String motivoSemAcesso(Operador operador, LocalDateTime agora) {
        if (operador == null) {
            return "Nenhum operador logado.";
        }
        if (turnoEmAndamento(operador, agora) != null) {
            return null;
        }
        for (Turno turno : RepositorioEscalaDeTrabalhoController.turnosDoDia(agora.toLocalDate())) {
            if (turno.operador() == operador) {
                return "O controle da atração só funciona durante o seu turno. Hoje você trabalha no turno da "
                        + turno.periodo() + ".";
            }
        }
        return "Você não está escalado para hoje (" + agora.format(DATA)
                + "). O controle da atração só funciona durante o seu turno.";
    }
}
