package parkAdventure.controller;

import parkAdventure.model.atracao.Atracao;
import parkAdventure.model.pessoa.Visitante;

import java.time.LocalDateTime;


public class Cadastro{
    static public void cadastrar(Atracao brinquedo, Visitante visitante){
        double preco = 0;

        String ingresso = ("Atração é "+brinquedo +"visitante"+ visitante+"data:"+LocalDateTime.now());
        System.out.println(ingresso);
        



    }






}