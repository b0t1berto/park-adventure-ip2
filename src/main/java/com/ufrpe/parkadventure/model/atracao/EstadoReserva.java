package com.ufrpe.parkadventure.model.atracao;

// Situação de um passe fura-fila
public enum EstadoReserva {
    RESERVADA,   // marcada e ainda não usada
    USADA,       // o operador liberou a entrada
    NO_SHOW,     // o visitante não apareceu dentro da tolerância
    CANCELADA    // o visitante desistiu
}
