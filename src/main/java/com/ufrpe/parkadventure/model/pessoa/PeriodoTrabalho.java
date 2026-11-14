package com.ufrpe.parkadventure.model.pessoa;

import java.time.Duration;
import java.time.LocalTime;

// Turnos de trabalho de um operador. O parque atende ao público das 11:00 às 18:00 (horários do fura-fila);
// cada turno vai de 30 minutos antes da abertura até 30 minutos depois do fechamento, em dois blocos de 4 horas.
public enum PeriodoTrabalho {
    MANHA("Manhã", LocalTime.of(10, 30), LocalTime.of(14, 30)),
    TARDE("Tarde", LocalTime.of(14, 30), LocalTime.of(18, 30));

    private final String rotulo;
    private final LocalTime inicio;
    private final LocalTime fim;

    PeriodoTrabalho(String rotulo, LocalTime inicio, LocalTime fim) {
        this.rotulo = rotulo;
        this.inicio = inicio;
        this.fim = fim;
    }

    public String getRotulo() { return rotulo; }
    public LocalTime getInicio() { return inicio; }
    public LocalTime getFim() { return fim; }

    public long horas() { return Duration.between(inicio, fim).toHours(); }

    @Override
    public String toString() {
        return rotulo + " (" + inicio + "–" + fim + ")";
    }
}
