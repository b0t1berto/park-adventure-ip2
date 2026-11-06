package com.ufrpe.parkadventure.model.pessoa;

import java.time.LocalDate;

public class Responsavel extends Pessoa {
    private String contatoEmergencia;

    public Responsavel(String nome, String dataNascimentoTexto, String cpf) {
        super(nome, dataNascimentoTexto, cpf);
    }

    // Para quando a data de nascimento do responsável não é informada (pode ser null)
    public Responsavel(String nome, LocalDate dataNascimento, String cpf) {
        super(nome, dataNascimento, cpf);
    }

    public String getContatoEmergencia() { return contatoEmergencia; }
    public void setContatoEmergencia(String contatoEmergencia) { this.contatoEmergencia = contatoEmergencia; }
}
