package com.ufrpe.parkadventure.model.atracao;

import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.model.pessoa.Visitante;

public abstract class Atracao implements IRestricao {
    // A cota do fura-fila acompanha a vazão da atração: capacidade x ciclos em 30 min x % de prioridade.
    // Ajuste estas duas constantes conforme a duração real de cada ciclo.
    private static final int CICLOS_POR_MEIA_HORA = 6;
    private static final double PERCENTUAL_FURA_FILA = 0.20;

    private String nome;
    private int capacidadeMaxima;
    private Operador operador;
    private int alturaMinima;
    private int idadeMinima;
    private int ocupacaoAtual;
    private final FilaVirtual filaVirtual = new FilaVirtual(this::vagasFuraFilaPorHorario);

    protected Atracao(String nome, int capacidadeMaxima, Operador operador, int alturaMinima, int idadeMinima) {
        this.nome = nome;
        this.capacidadeMaxima = capacidadeMaxima;
        this.operador = operador;
        this.alturaMinima = alturaMinima;
        this.idadeMinima = idadeMinima;
    }

    // Regras de cada tipo; as subclasses podem sobrescrever
    protected boolean atendeRequisitos(Visitante visitante) {
        return visitante.getIdade() >= idadeMinima && visitante.getAltura() >= alturaMinima;
    }

    @Override
    public MotivoBloqueio verificarRestricao(Visitante visitante) {
        if (!atendeRequisitos(visitante)) {
            return MotivoBloqueio.FORA_DOS_REQUISITOS;
        }
        if (visitante.precisaDeResponsavel()) {
            return MotivoBloqueio.SEM_RESPONSAVEL;
        }
        return MotivoBloqueio.NENHUM;
    }

    public boolean podeEntrar(Visitante visitante) {
        return verificarRestricao(visitante) == MotivoBloqueio.NENHUM;
    }

    // Registra a entrada de uma pessoa; false se estiver lotado
    public boolean entrar() {
        if (estaLotada()) {
            return false;
        }
        ocupacaoAtual++;
        return true;
    }

    // Registra a saída de uma pessoa
    public void sair() {
        if (ocupacaoAtual > 0) {
            ocupacaoAtual--;
        }
    }

    // Correção manual feita pelo operador (ex.: o programa foi reiniciado com gente dentro)
    public boolean ajustarOcupacao(int novaOcupacao) {
        if (novaOcupacao < 0 || novaOcupacao > capacidadeMaxima) {
            return false;
        }
        ocupacaoAtual = novaOcupacao;
        return true;
    }

    // Vagas de fura-fila por horário (meia hora), sempre pelo menos 1
    public final int vagasFuraFilaPorHorario() {
        return Math.max(1, (int) Math.round(capacidadeMaxima * CICLOS_POR_MEIA_HORA * PERCENTUAL_FURA_FILA));
    }

    // Ao editar uma atração, a nova versão continua com a ocupação e as reservas da anterior
    public void herdarEstadoDe(Atracao anterior) {
        if (anterior == null || anterior == this) {
            return;
        }
        this.ocupacaoAtual = Math.min(anterior.ocupacaoAtual, this.capacidadeMaxima);
        this.filaVirtual.copiarReservasDe(anterior.filaVirtual);
    }

    public boolean estaLotada() { return ocupacaoAtual >= capacidadeMaxima; }

    public boolean temOperador() { return operador != null; }

    public String getTipo() { return getClass().getSimpleName(); }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getCapacidadeMaxima() { return capacidadeMaxima; }
    public void setCapacidadeMaxima(int capacidadeMaxima) { this.capacidadeMaxima = capacidadeMaxima; }

    public Operador getOperador() { return operador; }
    public void setOperador(Operador operador) { this.operador = operador; }

    public int getAlturaMinima() { return alturaMinima; }
    public void setAlturaMinima(int alturaMinima) { this.alturaMinima = alturaMinima; }

    public int getIdadeMinima() { return idadeMinima; }
    public void setIdadeMinima(int idadeMinima) { this.idadeMinima = idadeMinima; }

    public int getOcupacaoAtual() { return ocupacaoAtual; }

    public FilaVirtual getFilaVirtual() { return filaVirtual; }
}
