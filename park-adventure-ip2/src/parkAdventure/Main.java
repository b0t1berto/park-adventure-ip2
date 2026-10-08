
import model.atracao.*;
import model.pessoa.*;

public class Main {
    public static void main(String[] args) {
        Operador operadorUm = new Operador("Carlos", "22/01/1991", null, "Água Ardente");
        Aquatico atracaoUm = new Aquatico("Piratas do Caribe", 12, operadorUm, 130, 9);
        Operador operadorDois = new Operador("Caique", "22/01/2000", null, "Lula Voadora");
        Infantil atracaoDois = new Infantil("Lula Voadora", 10, operadorDois, 100, 10);
        Operador operadorTres = new Operador("Ivaldo", "20/01/1990", null, "Água Ardente");
        Radical atracaoTres = new Radical("Água Ardente", 20, operadorTres, 150, 16);
        Visitante visitanteUm = new Visitante("Miguel Ferreira", "30/09/2016", null, 136);
        Visitante visitanteDois = new Visitante("Edivan José", "21/04/2008", null, 181);
        Responsavel maeDoVisitanteUm = new Responsavel("Alexssandra", "11/11/1998", null);
        
        visitanteUm.setResponsavel(maeDoVisitanteUm);

        System.out.println(" ");
        System.out.println(atracaoUm.toString());
        System.out.println(" ");
        System.out.println(atracaoDois);
        System.out.println(" ");
        System.out.println(atracaoTres.toString());
        System.out.println(" ");
        atracaoUm.verificadorPodeEntrar(visitanteUm);
        atracaoUm.verificadorPodeEntrar(visitanteDois);
        System.out.println(" ");
        atracaoDois.verificadorPodeEntrar(visitanteUm);
        atracaoDois.verificadorPodeEntrar(visitanteDois);
        System.out.println(" ");
        atracaoTres.verificadorPodeEntrar(visitanteUm);
        atracaoTres.verificadorPodeEntrar(visitanteDois);
        System.out.println(" ");
    }
}
