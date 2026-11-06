package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.repository.EscalaTrabalho;
import com.ufrpe.parkadventure.repository.ListaAtracao;
import com.ufrpe.parkadventure.repository.ListaIngresso;
import com.ufrpe.parkadventure.repository.ListaVisitante;

public class RepositorioController {
    private RepositorioController() { }
    public static final ListaAtracao ATRACOES = ListaAtracao.getInstancia();
    public static final ListaVisitante VISITANTES = ListaVisitante.getInstancia();
    public static final ListaIngresso INGRESSOS = ListaIngresso.getInstancia();
    public static final EscalaTrabalho ESCALA = EscalaTrabalho.getInstancia();   
}
