package parkAdventure.model.atracao;
import parkAdventure.model.pessoa.Visitante;

public interface IRestricao {
    public void verificadorPodeEntrar(Visitante visitante);
        // if (visitante.getIdade() < idadeMinima || visitante.getAltura() < alturaMinima) {
        //     System.out.println("O visitante " + visitante.getNome() + " não atinge os requesítos mínimos!");
        // }else if (visitante.getIdade() < 18 && !visitante.temResponsavel()) {
        //     System.out.println("O visitante  " + visitante.getNome() + " não atinge os requesítos mínimos!");
        // }else {
        //     System.out.println("Tudo certo " + visitante.getNome() + " pode avançar!");
        //     visitante.setVariavelDeControle();
        // }
    public void entrar(Visitante visitante);
        // if (visitante.getVariavlDeControle() == false) {
        //     System.out.println(visitante.getNome() + " não pode entrar nesta Atração!");
        // } else {
        //     System.out.println("Aproveite o Brinquedo " + visitante.getNome() + "!");
        // }
}
