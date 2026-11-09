package com.ufrpe.parkadventure.controller;

import java.util.ArrayList;

import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.repository.RepositorioAtracao;

public class RepositorioAtracaoController {
    private static final RepositorioAtracao repositorioAtracao = RepositorioAtracao.getInstancia();

    public static boolean cadastrar(Atracao atracao) {
        return repositorioAtracao.cadastrar(atracao);
    }

    public static boolean cadastrarOperador(String nomeAtracao, Operador nomeOperador) {
        return repositorioAtracao.cadastrarOperador(nomeAtracao, nomeOperador);
    }

    public static ArrayList<Atracao> listarTodas() {
        return repositorioAtracao.getAtracoes();
    }

    public static Atracao buscar(String nome) {
        return repositorioAtracao.buscar(nome);
    }

    public static String buscarOperador(Operador nomeOperador) {
        return repositorioAtracao.buscarOperador(nomeOperador);
    }

    public static boolean atualizar(String nome, Atracao atracaoAtualizada) {
        return repositorioAtracao.atualizar(nome, atracaoAtualizada);
    }

    public static boolean remover(String nome) {
        return repositorioAtracao.remover(nome);
    }
    
}
