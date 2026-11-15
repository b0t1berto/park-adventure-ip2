package com.ufrpe.parkadventure.service;

import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.model.pessoa.PeriodoTrabalho;
import com.ufrpe.parkadventure.repository.RepositorioEscalaTrabalho;
import com.ufrpe.parkadventure.repository.RepositorioEscalaTrabalho.Turno;

import java.time.LocalDate;
import java.util.List;

/**
 * Serviço de escala de trabalho: regras de negócio para escalonamento de operadores.
 * Regras: no máximo um turno por operador por dia; no máximo 6 dias por semana com folga garantida.
 */
public class ServicoEscala {

    public static final int MAX_DIAS_POR_SEMANA = 6;

    private final RepositorioEscalaTrabalho repositorio;

    public ServicoEscala(RepositorioEscalaTrabalho repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Retorna o motivo pelo qual o operador NÃO pode ser escalado, ou null se pode.
     */
    public String motivoRecusa(LocalDate data, Operador operador, PeriodoTrabalho periodo) {
        return repositorio.motivoRecusa(data, operador, periodo);
    }

    /**
     * Escala um operador em um dia e turno, respeitando as regras de carga horária.
     */
    public boolean escalar(LocalDate data, Operador operador, PeriodoTrabalho periodo) {
        if (data == null || operador == null || periodo == null) {
            throw new IllegalArgumentException("Data, operador e turno não podem ser nulos");
        }
        return repositorio.escalar(data, operador, periodo);
    }

    /**
     * Remove um turno da escala.
     */
    public boolean remover(Turno turno) {
        return repositorio.remover(turno);
    }

    /**
     * Remove todos os turnos de um operador (quando ele deixa o parque).
     */
    public void removerDoOperador(Operador operador) {
        repositorio.removerDoOperador(operador);
    }

    /**
     * Verifica se um operador já tem turno em um dia.
     */
    public boolean estaEscalado(LocalDate data, Operador operador) {
        return repositorio.estaEscalado(data, operador);
    }

    /**
     * Quantos dias o operador já trabalha na semana em que 'data' está.
     */
    public int diasTrabalhadosNaSemana(LocalDate data, Operador operador) {
        return repositorio.diasTrabalhadosNaSemana(data, operador);
    }

    /**
     * Conta o total de turnos de um operador.
     */
    public int contarTurnos(Operador operador) {
        return repositorio.contarTurnos(operador);
    }

    /**
     * Lista os operadores escalados em um dia.
     */
    public List<Operador> operadoresDoDia(LocalDate data) {
        return repositorio.operadoresDoDia(data);
    }

    /**
     * Lista os operadores escalados em um dia e turno específicos.
     */
    public List<Operador> operadoresDoTurno(LocalDate data, PeriodoTrabalho periodo) {
        return repositorio.operadoresDoTurno(data, periodo);
    }

    /**
     * Lista os turnos de um dia.
     */
    public List<Turno> turnosDoDia(LocalDate data) {
        return repositorio.turnosDoDia(data);
    }

    /**
     * Lista todos os turnos ordenados por data, turno e nome do operador.
     */
    public List<Turno> listarOrdenado() {
        return repositorio.listarOrdenado();
    }
}
