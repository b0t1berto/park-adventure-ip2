package com.ufrpe.parkadventure.controller;

import java.util.ArrayList;

import com.ufrpe.parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.repository.RepositorioVisitante;

public class RepositorioVisitanteController {
    private static final RepositorioVisitante repositorioVisitante = RepositorioVisitante.getInstancia();
    
    public static boolean cadastrarVisitante (Visitante visitante) {
        return repositorioVisitante.cadastrar(visitante);
    }

    public static Visitante buscarVisitante(String nome) {
        return repositorioVisitante.buscar(nome);
    }

    public static boolean atualizarVisitante(String nome, Visitante visitanteAtualizado) {
        return repositorioVisitante.atualizar(nome, visitanteAtualizado);
    }

    public static boolean removerVisitante(String nome) {
        return repositorioVisitante.remover(nome);
    }

    public static ArrayList<Visitante> listarTodos() {
        return repositorioVisitante.getVisitantes();
    }

}
