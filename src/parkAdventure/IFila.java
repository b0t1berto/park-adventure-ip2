package parkAdventure;

public interface IFila {
    public Visitante[] fila = null;
    int capacidadeMax = 0;
    void cadastrar();
    void remover();
}
