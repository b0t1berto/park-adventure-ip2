package com.ufrpe.parkadventure.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.ufrpe.parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.model.ingresso.TipoIngresso;
import com.ufrpe.parkadventure.model.pessoa.Responsavel;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.repository.Persistencia;

public final class PedidoAtual {
    private PedidoAtual() { }

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static LocalDate dataVisita = LocalDate.now();
    private static Ingresso ultimoIngresso;

    private static boolean temComprador;
    private static String nome;
    private static String cpf;
    private static LocalDate nascimento;
    private static int altura;
    private static String email;
    private static String telefone;
    private static String nomeResponsavel;
    private static String cpfResponsavel;
    private static String contatoEmergencia;

    public static Ingresso getUltimoIngresso() {
        return ultimoIngresso;
    }

    public static LocalDate getDataVisita() {
        return dataVisita;
    }

    public static void setDataVisita(LocalDate data) {
        dataVisita = (data == null) ? LocalDate.now() : data;
    }

    public static void definirComprador(String nome, String cpf, LocalDate nascimento, int altura,
                                        String email, String telefone,
                                        String nomeResponsavel, String cpfResponsavel,
                                        String contatoEmergencia) {
        PedidoAtual.nome = nome.trim();
        PedidoAtual.cpf = cpf.trim();
        PedidoAtual.nascimento = nascimento;
        PedidoAtual.altura = altura;
        PedidoAtual.email = email.trim();
        PedidoAtual.telefone = telefone.trim();
        PedidoAtual.nomeResponsavel = nomeResponsavel;
        PedidoAtual.cpfResponsavel = cpfResponsavel;
        PedidoAtual.contatoEmergencia = contatoEmergencia;
        temComprador = true;
    }

    public static synchronized Ingresso concluir(String formaPagamento, double valor) {
        if (!temComprador) {
            return null;
        }

        // O ingresso é sempre do visitante que está logado. Antes o formulário buscava/criava um visitante
        Visitante visitante = ContextoController.SESSAO.getVisitanteAtual();
        if (visitante == null) {
            // ninguém logado
            visitante = RepositorioVisitanteController.buscarVisitante(nome);
            if (visitante == null) {
                visitante = new Visitante(nome, nascimento.format(DATA), cpf, altura);
                RepositorioVisitanteController.cadastrarVisitante(visitante);
            }
            ContextoController.SESSAO.setVisitanteAtual(visitante);
        }
        // a compra só completa o perfil
        if (visitante.getCpf() == null || visitante.getCpf().isBlank()) {
            visitante.setCpf(cpf);
        }
        visitante.setDataNascimento(nascimento);
        visitante.setAltura(altura);
        visitante.setEmail(email);
        visitante.setTelefone(telefone);

        if (nomeResponsavel != null && !nomeResponsavel.isBlank()) {
            Responsavel responsavel = new Responsavel(nomeResponsavel.trim(), (LocalDate) null,
                    cpfResponsavel == null ? null : cpfResponsavel.trim());
            responsavel.setContatoEmergencia(contatoEmergencia == null ? null : contatoEmergencia.trim());
            visitante.setResponsavel(responsavel);
        }

        LocalDate hoje = LocalDate.now();
        LocalDate validade = dataVisita.isBefore(hoje) ? hoje : dataVisita;
        Ingresso ingresso = new Ingresso(visitante, TipoIngresso.DIARIA, hoje, validade, valor);
        ingresso.setFormaPagamento(formaPagamento);
        RepositorioIngressoController.cadastrarIngresso(ingresso);

        Persistencia.salvar();
        limpar();
        ultimoIngresso = ingresso;
        return ingresso;
    }

    private static void limpar() {
        temComprador = false;
        nome = null;
        cpf = null;
        nascimento = null;
        altura = 0;
        email = null;
        telefone = null;
        nomeResponsavel = null;
        cpfResponsavel = null;
        contatoEmergencia = null;
        dataVisita = LocalDate.now();
    }
}
