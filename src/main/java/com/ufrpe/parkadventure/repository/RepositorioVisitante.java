package com.ufrpe.parkadventure.repository;

import com.ufrpe.parkadventure.model.pessoa.Visitante;
import java.util.ArrayList;

public class RepositorioVisitante {
    private static RepositorioVisitante instancia;
    private static final ArrayList<Visitante> visitantes = new ArrayList<>();

    private RepositorioVisitante() {}

    public static synchronized RepositorioVisitante getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioVisitante();
        }
        return instancia;
    }

    public boolean cadastrar(Visitante visitante) {
        if (visitante == null || visitante.getNome() == null || visitante.getNome().isBlank()
                || buscar(visitante.getNome()) != null) {
            return false;
        }
        return visitantes.add(visitante);
    }

    public Visitante buscar(String nome) {
        if (nome == null) return null;
        for (Visitante visitante : visitantes) {
            if (visitante.getNome() != null && visitante.getNome().trim().equalsIgnoreCase(nome.trim())) {
                return visitante;
            }
        }
        return null;
    }

    public boolean atualizar(String nome, Visitante visitanteAtualizado) {
        Visitante existente = buscar(nome);
        if (existente == null || visitanteAtualizado == null
                || visitanteAtualizado.getNome() == null || visitanteAtualizado.getNome().isBlank()) {
            return false;
        }
        Visitante duplicado = buscar(visitanteAtualizado.getNome());
        if (duplicado != null && duplicado != existente) return false;
        int indice = visitantes.indexOf(existente);
        visitantes.set(indice, visitanteAtualizado);
        return true;
    }

    public boolean remover(String nome) {
        Visitante visitante = buscar(nome);
        return visitante != null && visitantes.remove(visitante);
    }

    public ArrayList<Visitante> getVisitantes() {
        return visitantes;
    }

}
