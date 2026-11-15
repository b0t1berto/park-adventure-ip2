package com.ufrpe.parkadventure.service;

import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.repository.RepositorioAtracao;

import java.util.ArrayList;

/**
 * Serviço de atrações: regras de negócio para cadastro, consulta e gestão de atrações e operadores.
 */
public class ServicoAtracao {

    private final RepositorioAtracao repositorio;

    public ServicoAtracao(RepositorioAtracao repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Cadastra uma nova atração. Retorna false se já existir uma com o mesmo nome.
     */
    public boolean cadastrar(Atracao atracao) {
        if (atracao == null) {
            throw new IllegalArgumentException("Atração não pode ser nula");
        }
        return repositorio.cadastrar(atracao);
    }

    /**
     * Cadastra um operador vinculado a uma atração.
     */
    public boolean cadastrarOperador(String nomeAtracao, Operador operador) {
        if (nomeAtracao == null || operador == null) {
            throw new IllegalArgumentException("Nome da atração e operador não podem ser nulos");
        }
        return repositorio.cadastrarOperador(nomeAtracao, operador);
    }

    /**
     * Busca uma atração pelo nome (case-insensitive).
     */
    public Atracao buscar(String nome) {
        return repositorio.buscar(nome);
    }

    /**
     * Retorna o nome do operador, ou null se não estiver vinculado a nenhuma atração.
     */
    public String buscarNomeDoOperador(Operador operador) {
        return repositorio.buscarNomeDoOperador(operador);
    }

    /**
     * Retorna a atração em que o operador trabalha.
     */
    public Atracao buscarAtracaoPorOperador(Operador operador) {
        return repositorio.buscarAtracaoPorOperador(operador);
    }

    /**
     * Busca um operador pelo nome dentro de uma atração específica.
     */
    public Operador buscarOperadorPorNome(String nome, String nomeAtracao) {
        return repositorio.buscarOperadorPorNome(nome, nomeAtracao);
    }

    /**
     * Busca um operador pelo usuário (case-insensitive).
     */
    public Operador buscarOperadorPorUsuario(String usuario) {
        return repositorio.buscarOperadorPorUsuario(usuario);
    }

    /**
     * Retorna todos os operadores de todas as atrações.
     */
    public ArrayList<Operador> todosOsOperadores() {
        return repositorio.todosOsOperadores();
    }

    /**
     * Lista todas as atrações cadastradas.
     */
    public ArrayList<Atracao> listarTodas() {
        return repositorio.getAtracoes();
    }

    /**
     * Atualiza os dados de uma atração existente, mantendo ocupação e reservas.
     */
    public boolean atualizar(String nome, Atracao atracaoAtualizada) {
        if (nome == null || atracaoAtualizada == null) {
            throw new IllegalArgumentException("Nome e atração atualizada não podem ser nulos");
        }
        return repositorio.atualizar(nome, atracaoAtualizada);
    }

    /**
     * Remove uma atração pelo nome.
     */
    public boolean remover(String nome) {
        return repositorio.remover(nome);
    }
}
