package parkAdventure.repository;

import parkAdventure.model.atracao.Atracao;
import parkAdventure.model.pessoa.Operador;
import java.util.ArrayList;

public class ListaAtracao {
    private final ArrayList<Atracao> atracoes = new ArrayList<>();

    public boolean cadastrar(Atracao atracao) {
        if (atracao == null || atracao.getNome() == null || atracao.getNome().isBlank()
                || buscar(atracao.getNome()) != null) {
            return false;
        }
        return atracoes.add(atracao);
    }

    public ArrayList<Atracao> listarTodas() {
        return new ArrayList<>(atracoes);
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

    // REQ19: soma das capacidades de todas as atrações
    public int capacidadeTotal() {
        int total = 0;
        for (Atracao atracao : atracoes) {
            total += atracao.getCapacidadeMaxima();
        }
        return total;
    }

    // Operador vinculado a alguma atração, pelo nome
    public Operador buscarOperador(String nome) {
        if (nome == null) return null;
        for (Atracao atracao : atracoes) {
            if (atracao.temOperador() && atracao.getOperador().getNome().trim().equalsIgnoreCase(nome.trim())) {
                return atracao.getOperador();
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
}
