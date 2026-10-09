package parkAdventure.controller;

import parkAdventure.model.atracao.Atracao;
import java.util.ArrayList;

public class ListaAtracao {
    private ArrayList<Atracao> atracoes;

    public ListaAtracao() {
        this.atracoes = new ArrayList<>();
    }

    public boolean cadastrar(Atracao atracao) {
        if (buscar(atracao.getNome()) != null) {
            return false;
        }
        return atracoes.add(atracao);
    }

    public ArrayList<Atracao> listarTodas() {
        return new ArrayList<>(atracoes);
    }

    public Atracao buscar(String nome) {
        for (Atracao a : atracoes) {
            if (a.getNome().equals(nome)) {
                return a;
            }
        }
        return null;
    }

    public boolean atualizar(String nome, Atracao atracaoAtualizada) {
        Atracao atracaoExistente = buscar(nome);
        if (atracaoExistente != null) {
            int index = atracoes.indexOf(atracaoExistente);
            atracoes.set(index, atracaoAtualizada);
            return true;
        }
        return false;
    }

    public boolean remover(String nome) {
        Atracao a = buscar(nome);
        if (a != null) {
            return atracoes.remove(a);
        }
        return false;
    }
}