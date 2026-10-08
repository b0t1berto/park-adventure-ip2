package parkAdventure;

import java.time.LocalDate;

public interface IVisitante {
    public String nome = "";
    public LocalDate nascimento = null;
    public int altura = 0;

    void adiciona(Visitante visitante);
    Visitante buscar(String nome);
    void atualizar(Visitante visitante);
    void remover(String nome);
}
