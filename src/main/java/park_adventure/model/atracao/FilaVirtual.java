package park_adventure.model.atracao;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import park_adventure.model.pessoa.Visitante;

// filas virtuais divididas apenas por horários (das 11:00 às 18:00, de 30 em 30 minutos)
public class FilaVirtual {
    private final int capacidadeMaximaPorHora;
    // fila de visitantes para aquele horário específico
    private final Map<String, Queue<Visitante>> filasPorHorario = new TreeMap<>(); // TreeMap mantém os horários em ordem

    public FilaVirtual(int capacidadeMaximaPorHora) {
        this.capacidadeMaximaPorHora = capacidadeMaximaPorHora;
        inicializarHorarios();
    }

    private void inicializarHorarios() {
        String[] horariosValidos = {
                "11:00", "11:30", "12:00", "12:30",
                "13:00", "13:30", "14:00", "14:30",
                "15:00", "15:30", "16:00", "16:30",
                "17:00", "17:30", "18:00"
        };

        for (String horario : horariosValidos) {
            filasPorHorario.put(horario, new LinkedList<>());
        }
    }

    public boolean validarHorario(String horario) { // Horários válidos das 11:00 às 18:00 de 30 em 30 min
        return horario != null && horario.matches("^(1[1-7]:(00|30)|18:00)$");
    }
    // Adiciona visitante a uma fila específica de um horário
    public boolean adicionar(String horario, Visitante visitante) {
        if (visitante == null || !validarHorario(horario)) {
            return false;
        }
        // Cria a fila se ela ainda não existir
        Queue<Visitante> filaHora = filasPorHorario.computeIfAbsent(horario, k -> new LinkedList<>());

        if (filaHora.size() >= capacidadeMaximaPorHora) {
            return false; // cheia
        }

        return filaHora.add(visitante);
    }
    // Remove e retorna o próximo visitante de um determinado horário
    public Visitante proximo(String horario) {
        if (!validarHorario(horario)) {
            return null;
        }
        Queue<Visitante> filaHora = filasPorHorario.get(horario);
        if (filaHora != null) {
            return filaHora.poll();
        }
        return null;
    }
    // Retorna o tamanho da fila de um horário específico
    public int tamanho(String horario) {
        if (!validarHorario(horario)) {
            return 0;
        }
        Queue<Visitante> filaHora = filasPorHorario.get(horario);
        return (filaHora != null) ? filaHora.size() : 0;
    }
    // Verifica se a fila de um horário está vazia
    public boolean estaVazia(String horario) {
        if (!validarHorario(horario)) {
            return true;
        }
        Queue<Visitante> filaHora = filasPorHorario.get(horario);
        return filaHora == null || filaHora.isEmpty();
    }
    // Chama o próximo da fila: o horário mais cedo que tiver gente esperando (FIFO dentro do horário)
    public Visitante proximo() {
        for (Queue<Visitante> filaHora : filasPorHorario.values()) {
            if (!filaHora.isEmpty()) {
                return filaHora.poll();
            }
        }
        return null;
    }
    // Verifica se não há ninguém esperando em nenhum horário
    public boolean estaVazia() {
        return tamanhoTotal() == 0;
    }
    // Total de visitantes esperando, somando todos os horários
    public int tamanhoTotal() {
        int total = 0;
        for (Queue<Visitante> filaHora : filasPorHorario.values()) {
            total += filaHora.size();
        }
        return total;
    }
    // Horários disponíveis, em ordem
    public List<String> getHorarios() {
        return new ArrayList<>(filasPorHorario.keySet());
    }
    public int getCapacidadeMaximaPorHora() {
        return capacidadeMaximaPorHora;
    }
}