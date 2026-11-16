package com.ufrpe.parkadventure.service;

import com.ufrpe.parkadventure.model.atracao.Aquatico;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.atracao.Infantil;
import com.ufrpe.parkadventure.model.atracao.Radical;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.repository.Persistencia;

import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de gestão de atrações pelo administrador: validar, cadastrar, editar, remover e buscar.
 * Quem cadastra ganha operadores e escala; quem edita mantém operadores, ocupação e reservas;
 * quem remove leva junto os operadores e os turnos deles.
 */
public class ServicoGestaoAtracao {

    public static final String AQUATICA = "Aquática";
    public static final String RADICAL = "Radical";
    public static final String INFANTIL = "Infantil";

    private final ServicoAtracao servicoAtracao;
    private final ServicoEscala servicoEscala;
    private final ServicoAutenticacao servicoAutenticacao;
    private final ServicoGeradorEscala servicoGeradorEscala;

    public ServicoGestaoAtracao(ServicoAtracao servicoAtracao, ServicoEscala servicoEscala,
                                ServicoAutenticacao servicoAutenticacao, ServicoGeradorEscala servicoGeradorEscala) {
        this.servicoAtracao = servicoAtracao;
        this.servicoEscala = servicoEscala;
        this.servicoAutenticacao = servicoAutenticacao;
        this.servicoGeradorEscala = servicoGeradorEscala;
    }

    public List<String> tipos() {
        return List.of(AQUATICA, RADICAL, INFANTIL);
    }

    public String nomeDoTipo(Atracao atracao) {
        if (atracao instanceof Infantil) return INFANTIL;
        if (atracao instanceof Aquatico) return AQUATICA;
        return RADICAL;
    }

    /**
     * Atrações cujo nome contém o texto (sem diferenciar maiúsculas); vazio = todas.
     */
    public List<Atracao> buscarPorTrecho(String trecho) {
        String t = trecho == null ? "" : trecho.trim().toLowerCase();
        List<Atracao> achadas = new ArrayList<>();
        for (Atracao atracao : servicoAtracao.listarTodas()) {
            if (t.isEmpty() || atracao.getNome().toLowerCase().contains(t)) {
                achadas.add(atracao);
            }
        }
        return achadas;
    }

    /**
     * Confere os dados de uma atração. Devolve a mensagem de erro, ou null se estiver tudo certo.
     */
    public String validar(String tipo, String nome, int capacidade, int altura, int idade) {
        if (nome == null || nome.isBlank()) return "Informe o nome da atração.";
        if (tipo == null || !tipos().contains(tipo)) return "Escolha o tipo da atração.";
        if (capacidade <= 0) return "A capacidade deve ser maior que zero.";
        if (altura < 0 || idade < 0) return "Altura e idade não podem ser negativas (use 0 para \"sem limite\").";
        if (tipo.equals(INFANTIL) && altura == 0) return "Na atração infantil, informe a altura máxima em centímetros.";
        return null;
    }

    // Os operadores não são informados aqui: nascem em garantirOperadoresDasAtracoes (cadastro)
    // ou são herdados da atração antiga (edição)
    private Atracao criar(String tipo, String nome, int capacidade, int altura, int idade) {
        if (tipo.equals(INFANTIL)) return new Infantil(nome, capacidade, null, altura, idade);
        if (tipo.equals(AQUATICA)) return new Aquatico(nome, capacidade, null, altura, idade);
        return new Radical(nome, capacidade, null, altura, idade);
    }

    /**
     * Cadastra uma atração nova; ela nasce com operadores (usuário e senha inicial) e já entra na escala.
     * Devolve null se cadastrou, ou a mensagem de erro.
     */
    public String cadastrarAtracao(String tipo, String nome, int capacidade, int altura, int idade) {
        String erro = validar(tipo, nome, capacidade, altura, idade);
        if (erro != null) {
            return erro;
        }
        if (!servicoAtracao.cadastrar(criar(tipo, nome.trim(), capacidade, altura, idade))) {
            return "Já existe uma atração com esse nome.";
        }
        servicoAutenticacao.garantirOperadoresDasAtracoes(ServicoGeradorEscala.MIN_OPERADORES_POR_ATRACAO);
        servicoGeradorEscala.gerarProximosDias();
        Persistencia.salvar();
        return null;
    }

    /**
     * Edita uma atração: mantém os operadores, a ocupação e as reservas.
     * Devolve null se salvou, ou a mensagem de erro.
     */
    public String editarAtracao(Atracao atual, String tipo, String nome, int capacidade, int altura, int idade) {
        if (atual == null) {
            return "Selecione uma atração na tabela.";
        }
        String erro = validar(tipo, nome, capacidade, altura, idade);
        if (erro != null) {
            return erro;
        }
        Atracao nova = criar(tipo, nome.trim(), capacidade, altura, idade);
        if (!servicoAtracao.atualizar(atual.getNome(), nova)) {
            return "Já existe outra atração com esse nome.";
        }
        for (Operador operador : nova.getOperadores()) {
            operador.setCargo(nova.getNome());   // o cargo guarda o nome da atração
        }
        Persistencia.salvar();
        return null;
    }

    /**
     * Remove a atração, os operadores dela e os turnos deles na escala.
     * Devolve null se removeu, ou a mensagem de erro.
     */
    public String removerAtracao(Atracao atracao) {
        if (atracao == null) {
            return "Selecione uma atração na tabela.";
        }
        for (Operador operador : new ArrayList<>(atracao.getOperadores())) {
            servicoEscala.removerDoOperador(operador);
        }
        if (!servicoAtracao.remover(atracao.getNome())) {
            return "Não foi possível remover a atração.";
        }
        Persistencia.salvar();
        return null;
    }
}
