package parkAdventure.controller;

import parkAdventure.model.pessoa.Visitante;
import java.util.ArrayList;

public class ListaVisitante {
    private ArrayList<Visitante> visitantes;

    public ListaVisitante() {
        this.visitantes = new ArrayList<>();
    }

    public boolean cadastrar(Visitante visitante) {
        if (buscar(visitante.getNome()) != null) {
            return false;
        }
        return visitantes.add(visitante);
    }

    public ArrayList<Visitante> listarTodos() {
        return new ArrayList<>(visitantes);
    }

    public Visitante buscar(String nome) {
        for (Visitante v : visitantes) {
            if (v.getNome().equals(nome)) {
                return v;
            }
        }
        return null;
    }

    public boolean atualizar(String nome, Visitante visitanteAtualizado) {
        Visitante visitanteExistente = buscar(nome);
        if (visitanteExistente != null) {
            int index = visitantes.indexOf(visitanteExistente);
            visitantes.set(index, visitanteAtualizado);
            return true;
        }
        return false;
    }

    public boolean remover(String nome) {
        Visitante v = buscar(nome);
        if (v != null) {
            return visitantes.remove(v);
        }
        return false;
    }
}