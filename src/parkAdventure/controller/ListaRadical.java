package parkAdventure.controller;

import parkAdventure.model.atracao.Radical;
import java.util.ArrayList;

public class ListaRadical {
    private final ArrayList<Radical> radicais;

    public ListaRadical() {
        this.radicais = new ArrayList<>();
    }

    public boolean cadastrar(Radical radical) {
        if (buscar(radical.getNome()) != null) {
            return false;
        }
        return radicais.add(radical);
    }

    public ArrayList<Radical> listarTodas() {
        return new ArrayList<>(radicais);
    }

    public Radical buscar(String nome) {
        for (Radical a : radicais) {
            if (a.getNome().equals(nome)) {
                return a;
            }
        }
        return null;
    }

    public boolean atualizar(String nome, Radical radicalAtualizada) {
        Radical radicalExistente = buscar(nome);
        if (radicalExistente != null) {
            int index = radicais.indexOf(radicalExistente);
            radicais.set(index, radicalAtualizada);
            return true;
        }
        return false;
    }

    public boolean remover(String nome) {
        Radical a = buscar(nome);
        if (a != null) {
            return radicais.remove(a);
        }
        return false;
    }
}