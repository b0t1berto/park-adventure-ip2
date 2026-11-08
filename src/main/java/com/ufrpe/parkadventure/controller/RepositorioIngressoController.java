package com.ufrpe.parkadventure.controller;

import java.util.ArrayList;

import com.ufrpe.parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.repository.RepositorioIngresso;

public class RepositorioIngressoController {
    private static final RepositorioIngresso repositorioIngresso = RepositorioIngresso.getInstancia();
    
    public static boolean cadastrarIngresso(Ingresso ingresso) {
        return repositorioIngresso.cadastrar(ingresso);
    }

    public static ArrayList<Ingresso> listarPorVisitante(String visitante) {
        return repositorioIngresso.listarPorVisitante(visitante);
    }

    public static Ingresso buscarUltimo(String visitante) {
        return repositorioIngresso.buscarUltimo(visitante);
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
