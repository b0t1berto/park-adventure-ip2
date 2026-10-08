package parkAdventure;

import classesExtras.Pessoa;
import java.util.ArrayList;
import java.time.LocalDate;

public class Visitante extends Pessoa implements IVisitante {
    public Visitante (String nome, LocalDate nascimento, int altura) {
        super(nome, nascimento, altura);   
    }
    private ArrayList <Visitante> visitantes = new ArrayList<>();

    public void adicionar(Visitante visitante){
        visitantes.add(visitante);
    }
    public Visitante buscar(String nome){
        for (Visitante v: visitantes){
            if (v.getNome().equals(nome)){
                return v;
            }
        }
        return null;
    }
    public void atualizar(Visitante visitante){
        for(int i = 0; i < visitantes.size(); i++){
            if (visitantes.get(i).getNome().equals(visitante.getNome())){
                visitantes.set(i, visitante);
                return;
            }
        }
    }
    public void remover(String nome){
        for (int i = 0; i < visitantes.size(); i++){
            if (visitantes.get(i).getNome().equals(nome)){
                visitantes.remove(i);
                return;
            }
        }
    }
}