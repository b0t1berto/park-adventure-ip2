package com.ufrpe.parkadventure.service;

import com.ufrpe.parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.repository.RepositorioVisitante;

import java.util.ArrayList;

/**
 * Serviço de visitantes: regras de negócio para cadastro, consulta e gestão de visitantes.
 */
public class ServicoVisitante {

    private final RepositorioVisitante repositorio;

    public ServicoVisitante(RepositorioVisitante repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Cadastra um novo visitante. Retorna false se já existir um com o mesmo nome.
     */
    public boolean cadastrar(Visitante visitante) {
        if (visitante == null) {
            throw new IllegalArgumentException("Visitante não pode ser nulo");
        }
        return repositorio.cadastrar(visitante);
    }

    /**
     * Busca um visitante pelo usuário (case-insensitive).
     */
    public Visitante buscarPorUsuario(String usuario) {
        return repositorio.buscarPorUsuario(usuario);
    }

    /**
     * Busca um visitante pelo nome (case-insensitive).
     */
    public Visitante buscar(String nome) {
        return repositorio.buscar(nome);
    }

    /**
     * Atualiza os dados de um visitante existente, mantendo ingressos e reservas vinculados.
     */
    public boolean atualizar(String nome, Visitante visitanteAtualizado) {
        if (nome == null || visitanteAtualizado == null) {
            throw new IllegalArgumentException("Nome e visitante atualizado não podem ser nulos");
        }
        return repositorio.atualizar(nome, visitanteAtualizado);
    }

    /**
     * Remove um visitante pelo nome.
     */
    public boolean remover(String nome) {
        return repositorio.remover(nome);
    }

    /**
     * Lista todos os visitantes cadastrados.
     */
    public ArrayList<Visitante> listarTodos() {
        return repositorio.getVisitantes();
    }
}
