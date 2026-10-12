package parkAdventure.controller;

import parkAdventure.model.atracao.Aquatico;
import java.util.ArrayList;

public class ListaAquatico {
    private final ArrayList<Aquatico> aquaticos;

    public ListaAquatico() {
        this.aquaticos = new ArrayList<>();
    }

    public boolean cadastrar(Aquatico aquatico) {
        if (buscar(aquatico.getNome()) != null) {
            return false;
        }
        return aquaticos.add(aquatico);
    }

    public ArrayList<Aquatico> listarTodas() {
        return new ArrayList<>(aquaticos);
    }

    public Aquatico buscar(String nome) {
        for (Aquatico a : aquaticos) {
            if (a.getNome().equals(nome)) {
                return a;
            }
        }
        return null;
    }

    public boolean atualizar(String nome, Aquatico aquaticoAtualizada) {
        Aquatico aquaticoExistente = buscar(nome);
        if (aquaticoExistente != null) {
            int index = aquaticos.indexOf(aquaticoExistente);
            aquaticos.set(index, aquaticoAtualizada);
            return true;
        }
        return false;
    }

    public boolean remover(String nome) {
        Aquatico a = buscar(nome);
        if (a != null) {
            return aquaticos.remove(a);
        }
        return false;
    }
}
