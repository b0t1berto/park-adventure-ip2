package com.ufrpe.parkadventure.controller;

import java.util.ArrayList;

import com.ufrpe.parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.repository.RepositorioIngresso;

public class RepositorioIngressoController {
    private static final RepositorioIngresso repositorioIngresso = RepositorioIngresso.getInstancia();
    
    public static boolean cadastrarIngresso(Ingresso ingresso) {
        return repositorioIngresso.cadastrar(ingresso);
    }

    public static ArrayList<Ingresso> listarPorVisitante(String nomeVisitante) {
        return repositorioIngresso.listarPorVisitante(nomeVisitante);
    }

    public static Ingresso buscarUltimo(String nomeVisitante) {
        return repositorioIngresso.buscarUltimo(nomeVisitante);
    }

    public static int consultarQuantidadePorData(java.time.LocalDate data) {
        return repositorioIngresso.consultarQuantidadePorData(data);
    }
    
    public static boolean removerIngresso(Ingresso ingresso) {
        return repositorioIngresso.remover(ingresso);
    }

    public static ArrayList<Ingresso> listarTodos() {
        return repositorioIngresso.listarTodos();
    }


}
