package com.ufrpe.parkadventure.controller.controles;

import com.ufrpe.parkadventure.app.Contexto;
import com.ufrpe.parkadventure.view.controles.Tarefas;

import javafx.concurrent.Task;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Consumer;

// Busca os feriados de um ano na BrasilAPI SEM travar a tela (usa o cache do ServicoFeriados).
// Os dois callbacks rodam na thread do JavaFX, então já podem mexer nos componentes.
public final class CarregadorFeriados {
    private CarregadorFeriados() { }

    public static void carregar(int ano, Consumer<Map<LocalDate, String>> aoTerminar, Consumer<String> aoFalhar) {
        Task<Map<LocalDate, String>> tarefa = new Task<>() {
            @Override
            protected Map<LocalDate, String> call() throws Exception {
                return Contexto.FERIADOS.carregarAno(ano);
            }
        };
        tarefa.setOnSucceeded(e -> aoTerminar.accept(tarefa.getValue()));
        tarefa.setOnFailed(e -> aoFalhar.accept(tarefa.getException() == null
                ? "erro desconhecido" : tarefa.getException().getMessage()));
        Tarefas.executar(tarefa);
    }
}
