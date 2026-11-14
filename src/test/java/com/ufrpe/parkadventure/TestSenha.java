package com.ufrpe.parkadventure;

import com.ufrpe.parkadventure.model.pessoa.Senha;

public class TestSenha {
    public static void main(String[] args) {
        String hashSalvo = "vA+szmj+6eyMtkDKojaeQQ==$UBU96FK3cWi2mJbWjqSPHFcVaVZ+aNYXzN1a+Qpudx8=";
        
        String[] senhas = {"Taylorseries", "taylorseries", "TaylorSeries", "TAYLORSERIES"};
        for (String s : senhas) {
            System.out.println("Senha '" + s + "' confere: " + Senha.confere(s, hashSalvo));
        }
    }
}
