package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.repository.Persistencia;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

// Criação de conta de VISITANTE: só usuário e senha (nome completo e CPF são informados na primeira compra).
// Operadores não são cadastrados aqui: só o administrador cadastra operadores.
public class TelaCriarContaController {

    @FXML
    private Label lblUsuario, lblSenha, lblMensagem, lblDivertir, lblParkAdventure;

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private Button bntVoltar, bntConfirmar;

    @FXML
    void onVoltar(ActionEvent event) {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onConfirmar(ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        String senha = txtSenha.getText();

        String problema = AutenticacaoController.validarCredenciais(usuario, senha);
        if (problema != null) {
            erro("Suas informações estão inválidas!", problema);
            return;
        }
        // administrador, visitantes e operadores entram pelo mesmo login: o usuário é único no sistema
        if (!AutenticacaoController.usuarioDisponivel(usuario, null)) {
            erro("Conta não criada", "Esse usuário não está disponível. Escolha outro.");
            return;
        }

        Alert confirmacao = new Alert(AlertType.CONFIRMATION);
        confirmacao.setTitle("CONFIRMAÇÃO");
        confirmacao.setHeaderText("Confirme os dados da sua conta");
        confirmacao.setContentText("Seu usuário será: " + usuario + " ?");
        if (confirmacao.showAndWait().filter(botao -> botao == ButtonType.OK).isEmpty()) {
            return;
        }

        // o nome da conta é o usuário. Nascimento e altura são só marcadores: a pessoa informa os dados de
        // verdade (nome completo, CPF...) na compra do ingresso.
        Visitante visitante = new Visitante(usuario, "01/01/2000", null, 0);
        visitante.setUsuario(usuario);
        visitante.definirSenha(senha);   // só o hash da senha é guardado (ver Senha)
        if (!RepositorioVisitanteController.cadastrarVisitante(visitante)) {
            erro("Conta não criada", "Esse usuário não está disponível. Escolha outro.");
            return;
        }
        Persistencia.salvar();

        Alert pronto = new Alert(AlertType.INFORMATION);
        pronto.setTitle("CONTA CRIADA");
        pronto.setHeaderText("Conta criada com sucesso!");
        pronto.setContentText("Seu nome completo, CPF, altura e data de nascimento serão pedidos na compra do ingresso.");
        pronto.showAndWait();

        txtUsuario.setText("");
        txtSenha.setText("");
    }

    private void erro(String titulo, String mensagem) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("ERRO");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
