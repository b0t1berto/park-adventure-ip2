package parkAdventure.controller;

import parkAdventure.model.atracao.Infantil;
import java.util.ArrayList;

public class ListaInfantil {
    private final ArrayList<Infantil> infantils;

    public ListaInfantil() {
        this.infantils = new ArrayList<>();
    }

    public boolean cadastrar(Infantil infantil) {
        if (buscar(infantil.getNome()) != null) {
            return false;
        }
        return infantils.add(infantil);
    }

    public ArrayList<Infantil> listarTodas() {
        return new ArrayList<>(infantils);
    }

    public Infantil buscar(String nome) {
        for (Infantil a : infantils) {
            if (a.getNome().equals(nome)) {
                return a;
            }
        }
        return null;
    }

    public boolean atualizar(String nome, Infantil infantilAtualizada) {
        Infantil infantilExistente = buscar(nome);
        if (infantilExistente != null) {
            int index = infantils.indexOf(infantilExistente);
            infantils.set(index, infantilAtualizada);
            return true;
        }
        return false;
    }

    public boolean remover(String nome) {
        Infantil a = buscar(nome);
        if (a != null) {
            return infantils.remove(a);
        }
        return false;
    }
}