package com.ufrpe.parkadventure.controller.controles;

import javafx.concurrent.Task;

public final class Tarefas {
    private Tarefas() { }
    public static void executar(Task<?> tarefa) {
        Thread thread = new Thread(tarefa, "park-adventure-tarefa");
        thread.setDaemon(true);
        thread.start();
    }
}
