package com.ufrpe.parkadventure.javafx_parkadventure.repository;

import com.ufrpe.parkadventure.javafx_parkadventure.model.pessoa.Visitante;
import java.util.ArrayList;

public class ListaVisitante {
    private final ArrayList<Visitante> visitantes = new ArrayList<>();

    public boolean cadastrar(Visitante visitante) {
        if (visitante == null || visitante.getNome() == null || visitante.getNome().isBlank()
                || buscar(visitante.getNome()) != null) {
            return false;
        }
        return visitantes.add(visitante);
    }

    public ArrayList<Visitante> listarTodos() {
        return new ArrayList<>(visitantes);
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
}
