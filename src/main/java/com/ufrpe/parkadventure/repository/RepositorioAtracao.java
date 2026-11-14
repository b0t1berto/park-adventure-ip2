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
        return atracao.adicionarOperador(operador);
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

    // Devolve o NOME do operador (não a atração). Para a atração use buscarAtracaoPorOperador.
    public String buscarNomeDoOperador(Operador operador) {
        return buscarAtracaoPorOperador(operador) == null ? null : operador.getNome();
    }

    // Atração em que o operador trabalha (null se ele não estiver vinculado a nenhuma)
    public Atracao buscarAtracaoPorOperador(Operador operador) {
        if (operador == null) return null;
        for (Atracao atracao : atracoes) {
            if (atracao.getOperadores().contains(operador)) {
                return atracao;
            }
        }
        return null;
    }

    // Procura o operador pelo nome dentro da atração informada
    public Operador buscarOperadorPorNome(String nome, String nomeAtracao) {
        if (nome == null || nomeAtracao == null) return null;
        for (Atracao atracao : atracoes) {
            if (atracao.getNome() == null || !atracao.getNome().trim().equalsIgnoreCase(nomeAtracao.trim())) {
                continue;
            }
            for (Operador operador : atracao.getOperadores()) {
                if (operador.getNome() != null && operador.getNome().trim().equalsIgnoreCase(nome.trim())) {
                    return operador;
                }
            }
        }
        return null;
    }

    // Todos os operadores de todas as atrações
    public ArrayList<Operador> todosOsOperadores() {
        ArrayList<Operador> todos = new ArrayList<>();
        for (Atracao atracao : atracoes) {
            todos.addAll(atracao.getOperadores());
        }
        return todos;
    }

    // Operador com esse usuário (sem diferenciar maiúsculas); null se não existir
    public Operador buscarOperadorPorUsuario(String usuario) {
        if (usuario == null) return null;
        for (Atracao atracao : atracoes) {
            for (Operador operador : atracao.getOperadores()) {
                if (operador.getUsuario() != null && operador.getUsuario().equalsIgnoreCase(usuario.trim())) {
                    return operador;
                }
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
        // a nova versão mantém a ocupação e as reservas da antiga (antes elas eram perdidas)
        atracaoAtualizada.herdarEstadoDe(existente);
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
