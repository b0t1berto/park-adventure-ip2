package com.ufrpe.parkadventure.model.pessoa;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class Pessoa {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String nome;
    private LocalDate dataNascimento;
    private String cpf;
    private String usuario;
    private String senhaHash;   // nunca a senha em texto puro (ver Senha)

    public Pessoa(String nome, LocalDate dataNascimento, String cpf) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.cpf = cpf;
    }

    // Aceita a data como texto no formato dd/MM/yyyy
    public Pessoa(String nome, String dataNascimentoTexto, String cpf) {
        this(nome, LocalDate.parse(dataNascimentoTexto, FORMATO_DATA), cpf);
    }

    public int getIdade() {
        if (dataNascimento == null) {
            return 0;
        }
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario == null ? null : usuario.trim(); }

    // Hash guardado no arquivo de dados ("salt$hash")
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    public void definirSenha(String senha) { this.senhaHash = Senha.gerarHash(senha); }

    public boolean senhaConfere(String senha) { return Senha.confere(senha, senhaHash); }

    public boolean temCredenciais() {
        return usuario != null && !usuario.isBlank() && senhaHash != null && !senhaHash.isBlank();
    }

    @Override
    public String toString() {
        return "Pessoa [nome=" + nome + ", cpf=" + cpf + ", getIdade()=" + getIdade() + "]";
    }

    
}
