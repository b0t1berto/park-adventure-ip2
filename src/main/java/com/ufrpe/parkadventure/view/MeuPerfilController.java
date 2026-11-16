package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.app.Contexto;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

// Tela do visitante: ele mesmo confere e atualiza os próprios dados e troca a senha
public class MeuPerfilController {

    @FXML private TextField txtNomeCompleto, txtCpf, txtAltura, txtEmail, txtTelefone,
            txtNomeResponsavel, txtCpfResponsavel, txtContatoEmergencia;
    @FXML private PasswordField txtSenhaAtual, txtSenhaNova;
    @FXML private DatePicker dpNascimento;
    @FXML private Label lblUsuario, lblResultado;

    private PerfilFormulario formulario;
    private Visitante visitante;

    @FXML
    private void initialize() {
        formulario = new PerfilFormulario(txtNomeCompleto, txtCpf, dpNascimento, txtAltura, txtEmail, txtTelefone,
                txtNomeResponsavel, txtCpfResponsavel, txtContatoEmergencia);
        visitante = Contexto.SESSAO.getVisitanteAtual();
        if (visitante == null) {
            lblUsuario.setText("Nenhum visitante logado.");
            Aviso.erro(lblResultado, "Entre na sua conta para ver o perfil.");
            return;
        }
        lblUsuario.setText("Usuário: " + Contexto.SERVICO_PERFIL_VISITANTE.usuarioOuNome(visitante));
        formulario.preencher(visitante);
        if (visitante.getAltura() <= 0) {
            Aviso.info(lblResultado, "Complete seus dados: a altura e o nascimento definem em quais atrações você pode entrar.");
        }
    }

    @FXML
    private void onSalvar() {
        if (visitante == null) {
            return;
        }
        String erro = formulario.salvarEm(visitante);
        if (erro != null) {
            Aviso.erro(lblResultado, erro);
        } else {
            Aviso.sucesso(lblResultado, "Seus dados foram salvos.");
        }
    }

    @FXML
    private void onAlterarSenha() {
        if (visitante == null) {
            return;
        }
        String erro = Contexto.SERVICO_PERFIL_VISITANTE.alterarSenha(visitante, txtSenhaAtual.getText(), txtSenhaNova.getText());
        if (erro != null) {
            Aviso.erro(lblResultado, erro);
            return;
        }
        txtSenhaAtual.clear();
        txtSenhaNova.clear();
        Aviso.sucesso(lblResultado, "Senha alterada.");
    }

    @FXML
    private void onVoltar() {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
