package com.ufrpe.parkadventure;

import com.ufrpe.parkadventure.controller.RepositorioAtracaoController;
import com.ufrpe.parkadventure.controller.RepositorioEscalaDeTrabalhoController;
import com.ufrpe.parkadventure.controller.RepositorioIngressoController;
import com.ufrpe.parkadventure.controller.RepositorioVisitanteController;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.repository.EscalaTrabalho.Turno;
import com.ufrpe.parkadventure.repository.Persistencia;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Persistencia.carregar();

        List<Visitante> visitantes = RepositorioVisitanteController.listarTodos();
        List<Atracao> atracoes = RepositorioAtracaoController.listarTodas();
        List<Ingresso> ingressos = RepositorioIngressoController.listarTodos();
        List<Turno> turnos = RepositorioEscalaDeTrabalhoController.listarOrdenado();

        for (Visitante v : visitantes) {
            System.out.println(v.getNome() + " | " + v.getEmail() + " | " + v.getTelefone());
        }
        for (Atracao a : atracoes) {
            System.out.println(a.getTipo() + " " + a.getNome()
                    + " (operador: " + (a.getOperador() == null ? "-" : a.getOperador().getNome()) + ")");
        }
        for (Turno t : turnos) {
            System.out.println(t.data() + " " + t.operador().getNome());
        }
    }
}