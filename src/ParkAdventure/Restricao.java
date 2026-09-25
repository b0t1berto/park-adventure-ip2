package ParkAdventure;

public class Restricao {
    private int alturaMinima;
    private int idadeMinima;

    public Restricao(int alturaMinima, int idadeMinima) {
        this.alturaMinima = alturaMinima;
        this.idadeMinima = idadeMinima;
    }

    public int getAlturaMinima() {
        return alturaMinima;
    }

    public void setAlturaMinima(int alturaMinima) {
        this.alturaMinima = alturaMinima;
    }

    public int getIdadeMinima() {
        return idadeMinima;
    }

    public void setIdadeMinima(int idadeMinima) {
        this.idadeMinima = idadeMinima;
    }

    public boolean podeEntrar(Visitante visitante) {
        return visitante.getAltura() >= alturaMinima && visitante.getIdade() >= idadeMinima;
    }
}
