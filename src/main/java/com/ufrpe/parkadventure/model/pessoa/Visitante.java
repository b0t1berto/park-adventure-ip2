package com.ufrpe.parkadventure.model.pessoa;

public class Visitante extends Pessoa {
    private static final int IDADE_MAIORIDADE = 18;

    private int altura;
    private Responsavel responsavel;
    private String email;
    private String telefone;
    // Nome real, informado na compra do ingresso
    private String nomeCompleto;

    public Visitante(String nome, String dataNascimentoTexto, String cpf, int altura) {
        super(nome, dataNascimentoTexto, cpf);
        this.altura = altura;
    }

    public int getAltura() { return altura; }
    public void setAltura(int altura) { this.altura = altura; }

    public Responsavel getResponsavel() { return responsavel; }
    public void setResponsavel(Responsavel responsavel) { this.responsavel = responsavel; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public boolean temResponsavel() {
        return responsavel != null;
    }

    public boolean ehMenorDeIdade() {
        return getIdade() < IDADE_MAIORIDADE;
    }

    // Menor de idade sem responsável não pode acessar atrações
    public boolean precisaDeResponsavel() {
        return ehMenorDeIdade() && !temResponsavel();
    }
}
