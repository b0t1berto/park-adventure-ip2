package parkAdventure.controller;

import parkAdventure.model.ingresso.Ingresso;
import java.util.ArrayList;

public class ListaIngresso {
    private final ArrayList<Ingresso> ingressos = new ArrayList<>();

    public boolean cadastrar(Ingresso ingresso) {
        if (ingresso == null || ingresso.getNome() == null || ingresso.getNome().isBlank()
                || buscar(ingresso.getNome()) != null) {
            return false;
        }
        return ingressos.add(ingresso);
    }

    public ArrayList<Ingresso> listarTodas() {
        return new ArrayList<>(ingressos);
    }

    public Ingresso buscar(String nome) {
        if (nome == null) return null;
        for (Ingresso ingresso : ingressos) {
            if (ingresso.getNome() != null && ingresso.getNome().trim().equalsIgnoreCase(nome.trim())) {
                return ingresso;
            }
        }
        return null;
    }

    public boolean atualizar(String nome, Ingresso ingressoAtualizado) {
        Ingresso existente = buscar(nome);
        if (existente == null || ingressoAtualizado == null
                || ingressoAtualizado.getNome() == null || ingressoAtualizado.getNome().isBlank()) {
            return false;
        }
        Ingresso duplicada = buscar(ingressoAtualizado.getNome());
        if (duplicada != null && duplicada != existente) return false;
        int indice = ingressos.indexOf(existente);
        ingressos.set(indice, ingressoAtualizado);
        return true;
    }

    public boolean remover(String nome) {
        Ingresso ingresso = buscar(nome);
        return ingresso != null && ingressos.remove(ingresso);
    }
}