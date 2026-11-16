package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.app.Contexto;
import com.ufrpe.parkadventure.model.pessoa.Responsavel;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

// Campos do perfil do visitante, usados na tela do administrador (Visitantes) e em "Meu Perfil"
public class PerfilFormulario {

    private final TextField nomeCompleto, cpf, altura, email, telefone, nomeResponsavel, cpfResponsavel, contatoEmergencia;
    private final DatePicker nascimento;

    public PerfilFormulario(TextField nomeCompleto, TextField cpf, DatePicker nascimento, TextField altura,
                            TextField email, TextField telefone, TextField nomeResponsavel,
                            TextField cpfResponsavel, TextField contatoEmergencia) {
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.nascimento = nascimento;
        this.altura = altura;
        this.email = email;
        this.telefone = telefone;
        this.nomeResponsavel = nomeResponsavel;
        this.cpfResponsavel = cpfResponsavel;
        this.contatoEmergencia = contatoEmergencia;
    }

    public void limpar() {
        for (TextField campo : new TextField[]{nomeCompleto, cpf, altura, email, telefone,
                nomeResponsavel, cpfResponsavel, contatoEmergencia}) {
            campo.setText("");
        }
        nascimento.setValue(null);
    }

    public void preencher(Visitante v) {
        limpar();
        if (v == null) {
            return;
        }
        if (v.getNomeCompleto() != null) nomeCompleto.setText(v.getNomeCompleto());
        if (v.getCpf() != null) cpf.setText(v.getCpf());
        if (v.getEmail() != null) email.setText(v.getEmail());
        if (v.getTelefone() != null) telefone.setText(v.getTelefone());
        // conta nova: altura 0 e nascimento 01/01/2000 são só marcadores de "dado ainda não informado"
        if (v.getAltura() > 0) {
            altura.setText(String.valueOf(v.getAltura()));
            nascimento.setValue(v.getDataNascimento());
        }
        Responsavel r = v.getResponsavel();
        if (r != null) {
            if (r.getNome() != null) nomeResponsavel.setText(r.getNome());
            if (r.getCpf() != null) cpfResponsavel.setText(r.getCpf());
            if (r.getContatoEmergencia() != null) contatoEmergencia.setText(r.getContatoEmergencia());
        }
    }

    // Salva os campos no visitante. Devolve null se salvou, ou a mensagem de erro.
    public String salvarEm(Visitante v) {
        int alturaLida;
        try {
            alturaLida = Integer.parseInt(altura.getText().trim());
        } catch (NumberFormatException e) {
            return "A altura deve ser um número em centímetros.";
        }
        return Contexto.SERVICO_PERFIL_VISITANTE.atualizarPerfil(v, nomeCompleto.getText(), cpf.getText(),
                nascimento.getValue(), alturaLida, email.getText(), telefone.getText(), nomeResponsavel.getText(),
                cpfResponsavel.getText(), contatoEmergencia.getText());
    }
}
