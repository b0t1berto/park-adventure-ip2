package com.ufrpe.parkadventure.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.repository.Persistencia;
import com.ufrpe.parkadventure.ParkAdventure;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class TelaCriarContaController {

    private static final int TAMANHO_MINIMO_SENHA = 4;

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
    public void onConfirmar (ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        String senha = txtSenha.getText();

        if (usuario.isBlank() || senha.length() < TAMANHO_MINIMO_SENHA) {
            erro("Suas informações estão inválidas!",
                    "Informe um usuário e uma senha com pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
            return;
        }

        // o administrador e os operadores também entram pelo login: o usuário deles não pode ser reaproveitado
        if (usuario.equalsIgnoreCase(TelaLoginController.USUARIO_ADM)
                || RepositorioAtracaoController.buscarOperadorPorUsuario(usuario) != null) {
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

        // nascimento e altura são só marcadores: a pessoa informa os dados de verdade na compra do ingresso
        Visitante visitante = new Visitante(usuario, "01/01/2000", null, 0);
        visitante.setSenha(senha);
        if (!RepositorioVisitanteController.cadastrarVisitante(visitante)) {
            erro("Conta não criada", "Esse usuário não está disponível. Escolha outro.");
            return;
        }

        Persistencia.salvar();

        Alert pronto = new Alert(AlertType.INFORMATION);
        pronto.setTitle("CONTA CRIADA");
        pronto.setHeaderText("Conta criada com sucesso!");
        pronto.setContentText("Seu nome, CPF, altura e data de nascimento serão pedidos na compra do ingresso.");
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
