package com.ufrpe.parkadventure.service;

import com.ufrpe.parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.repository.RepositorioIngresso;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Serviço de ingressos: regras de negócio para emissão, consulta e gestão de ingressos.
 */
public class ServicoIngresso {

    private final RepositorioIngresso repositorio;

    public ServicoIngresso(RepositorioIngresso repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Cadastra um novo ingresso.
     */
    public boolean cadastrar(Ingresso ingresso) {
        if (ingresso == null) {
            throw new IllegalArgumentException("Ingresso não pode ser nulo");
        }
        return repositorio.cadastrar(ingresso);
    }

    /**
     * Lista os ingressos de um visitante pelo nome.
     */
    public ArrayList<Ingresso> listarPorVisitante(String nomeVisitante) {
        return repositorio.listarPorVisitante(nomeVisitante);
    }

    /**
     * Retorna o ingresso mais recente de um visitante.
     */
    public Ingresso buscarUltimo(String nomeVisitante) {
        return repositorio.buscarUltimo(nomeVisitante);
    }

    /**
     * Consulta a quantidade de ingressos emitidos em uma data específica.
     */
    public int consultarQuantidadePorData(LocalDate data) {
        return repositorio.consultarQuantidadePorData(data);
    }

    /**
     * Remove um ingresso.
     */
    public boolean remover(Ingresso ingresso) {
        return repositorio.remover(ingresso);
    }

    /**
     * Lista todos os ingressos cadastrados.
     */
    public ArrayList<Ingresso> listarTodos() {
        return repositorio.listarTodos();
    }
}
