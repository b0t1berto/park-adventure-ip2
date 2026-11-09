package com.ufrpe.parkadventure.repository;

import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import java.util.ArrayList;

public class RepositorioAtracao{
    public static RepositorioAtracao instancia;
    public static final ArrayList<Atracao> atracoes = new ArrayList<>();

    public RepositorioAtracao() {}

    public static synchronized RepositorioAtracao getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioAtracao();
        }
        return instancia;
    }

    public boolean cadastrar(Atracao atracao) {
        if (atracao == null || atracao.getNome() == null || atracao.getNome().isBlank()
                || buscar(atracao.getNome()) != null) {
            return false;
        }
        return atracoes.add(atracao);
    }

    public boolean cadastrarOperador(String nomeAtracao, Operador operador) {
        Atracao atracao = buscar(nomeAtracao);
        if (atracao == null || operador == null) {
            return false;
        }
        atracao.setOperador(operador);
        return true;
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

    public String buscarOperador(Operador operador) {
        if (operador == null) return null;
        for (Atracao atracao : atracoes) {
            if (atracao.getOperador() != null && atracao.getOperador().equals(operador)) {
                return atracao.getOperador().getNome();
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

    public ArrayList<Atracao> getAtracoes() {
        return new ArrayList<>(atracoes);
    }
}
