package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.app.Contexto;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.repository.Persistencia;
import com.ufrpe.parkadventure.service.ServicoAutenticacao;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Tela de perfil do operador: exibe os dados pessoais (somente leitura) e permite trocar a senha.
 */
public class PerfilOperadorController {

    @FXML private Label lblUsuario, lblResultado;
    @FXML private TextField txtNome, txtCpf, txtCargo, txtUsuario;
    @FXML private PasswordField txtSenhaAtual, txtSenhaNova, txtSenhaConfirmar;

    @FXML
    private void initialize() {
        Operador operador = Contexto.SESSAO.getOperadorAtual();
        if (operador == null) {
            try {
                ParkAdventure.voltar();
            } catch (Exception ignored) { }
            return;
        }

        // Exibe o nome de usuário no cabeçalho
        String usuario = operador.getUsuario();
        if (usuario == null || usuario.isBlank()) {
            usuario = operador.getNome();
        }
        lblUsuario.setText("Operador: " + usuario);

        // Preenche os campos de dados (somente leitura)
        txtNome.setText(operador.getNome());
        txtNome.setEditable(false);

        String cpf = operador.getCpf();
        txtCpf.setText(cpf != null ? cpf : "");
        txtCpf.setEditable(false);

        txtCargo.setText(operador.getCargo() != null ? operador.getCargo() : "");
        txtCargo.setEditable(false);

        txtUsuario.setText(usuario);
        txtUsuario.setEditable(false);
    }

    @FXML
    private void onAlterarSenha() {
        lblResultado.setText("");

        Operador operador = Contexto.SESSAO.getOperadorAtual();
        if (operador == null) {
            lblResultado.setText("Sessão expirada. Faça login novamente.");
            return;
        }

        String senhaAtual = txtSenhaAtual.getText();
        String novaSenha = txtSenhaNova.getText();
        String confirmar = txtSenhaConfirmar.getText();

        // Validações
        if (senhaAtual == null || senhaAtual.isEmpty()) {
            lblResultado.setText("Informe a senha atual.");
            return;
        }

        if (!operador.senhaConfere(senhaAtual)) {
            lblResultado.setText("A senha atual está incorreta.");
            return;
        }

        if (novaSenha == null || novaSenha.length() < ServicoAutenticacao.TAMANHO_MINIMO_SENHA) {
            lblResultado.setText("A nova senha deve ter pelo menos " + ServicoAutenticacao.TAMANHO_MINIMO_SENHA + " caracteres.");
            return;
        }

        if (!novaSenha.equals(confirmar)) {
            lblResultado.setText("As senhas não coincidem. Verifique e tente novamente.");
            return;
        }

        // Aplica a nova senha
        operador.definirSenha(novaSenha);
        Persistencia.salvar();

        // Limpa os campos de senha
        txtSenhaAtual.clear();
        txtSenhaNova.clear();
        txtSenhaConfirmar.clear();

        lblResultado.setText("Senha alterada com sucesso!");
        lblResultado.setStyle("-fx-text-fill: green;");
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
