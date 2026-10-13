package parkAdventure.controller;

import parkAdventure.model.atracao.Atracao;
import java.util.ArrayList;

public class ListaAtracao {
    private final ArrayList<Atracao> atracoes = new ArrayList<>();

    public boolean cadastrar(Atracao atracao) {
        if (atracao == null || atracao.getNome() == null || atracao.getNome().isBlank()
                || buscar(atracao.getNome()) != null) {
            return false;
        }
        return atracoes.add(atracao);
    }

    public ArrayList<Atracao> listarTodas() {
        return new ArrayList<>(atracoes);
    }

    public Atracao buscar(String nome) {
        if (nome == null) return null;
        for (Atracao atracao : atracoes) {
            if (atracao.getNome() != null && atracao.getNome().trim().equalsIgnoreCase(nome.trim())) {
                return atracao;
            }
        }
        return null;
    }

    public boolean atualizar(String nome, Atracao atracaoAtualizada) {
        Atracao existente = buscar(nome);
        if (existente == null || atracaoAtualizada == null
                || atracaoAtualizada.getNome() == null || atracaoAtualizada.getNome().isBlank()) {
            return false;
        }
        Atracao duplicada = buscar(atracaoAtualizada.getNome());
        if (duplicada != null && duplicada != existente) return false;
        int indice = atracoes.indexOf(existente);
        atracoes.set(indice, atracaoAtualizada);
        return true;
    }

    public boolean remover(String nome) {
        Atracao atracao = buscar(nome);
        return atracao != null && atracoes.remove(atracao);
    }
}
