package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

// Login por usuário e senha: o sistema descobre sozinho se é administrador, operador ou visitante.
public class TelaLoginController {

    @FXML
    Label lblUsuario, lblSenha, lblParkAdventure, lblAcesseConta;

    @FXML
    TextField txtUsuario;

    @FXML
    PasswordField txtSenha;

    @FXML
    Button btOk;

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

        if (usuario.isEmpty() || senha.isEmpty()) {
            avisar("Suas informações estão inválidas!", "Digite o usuário e a senha para entrar.");
            txtSenha.setText("");
            return;
        }

        AutenticacaoController.Resultado resultado = AutenticacaoController.entrar(usuario, senha);
        if (resultado == null) {
            avisar("Usuário ou senha incorretos!", "Confira os dados e tente novamente.");
            txtSenha.setText("");
            return;
        }

        try {
            ContextoController.SESSAO.limpar();
            switch (resultado.papel()) {
                case ADMIN -> ParkAdventure.proximaCena("view/menu-adm.fxml");
                case OPERADOR -> {
                    ContextoController.SESSAO.setOperadorAtual((Operador) resultado.pessoa());
                    ParkAdventure.proximaCena("view/menu-operador.fxml");
                }
                case VISITANTE -> {
                    ContextoController.SESSAO.setVisitanteAtual((Visitante) resultado.pessoa());
                    ParkAdventure.proximaCena("view/menu-visitante.fxml");
                }
            }
            txtSenha.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void avisar(String titulo, String mensagem) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("AVISO");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
