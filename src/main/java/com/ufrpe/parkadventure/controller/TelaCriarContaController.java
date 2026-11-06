package com.ufrpe.parkadventure.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.ufrpe.parkadventure.model.pessoa.*;
import com.ufrpe.parkadventure.ParkAdventure;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;

public class TelaCriarContaController {

    @FXML
    private Label lblNome, lblCpf, lblCargo, lblMensagem, lblDivertir, lblParkAdventure;

    @FXML
    private TextField txtNome, txtCpf, txtCargo;

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
    //     String nome = txtFieldNome.getText();
    //     String cpf = txtFieldCpf.getText();

        if (txtNome.getText().isBlank()|| txtCpf.getText().isBlank()) {
            Alert alert = new Alert(AlertType.ERROR); 
            alert.setTitle("ERRO");
            alert.setHeaderText("Suas informações estão Inválidas!");
            alert.setContentText("Digite informações válidas para criar sua conta.");
            alert.showAndWait(); //Apenas quando apertar OK para sair da tela de aviso

            txtNome.setText("");
            txtCpf.setText("");
            txtCargo.setText("");
            return;
        } else {
            Alert alert = new Alert(AlertType.INFORMATION); 
            alert.setTitle("CONFIRMAÇÃO");
            alert.setHeaderText("Confirme os dados da sua conta");
            alert.setContentText("Seu nome é: " + this.txtNome.getText() + "\nSeu CPF é: " + this.txtCpf.getText() + " ?");
            alert.showAndWait(); //Apenas quando apertar OK para sair da tela de avis

            if (txtNome.getText().equalsIgnoreCase("leandro")) {
                Visitante leandro = new Visitante(txtNome.getText(), "01/01/2000", txtCpf.getText(), 0);
                System.out.println("funcionou");
                ContextoController.VISITANTES.cadastrar(leandro);
            } else if (txtCargo.getText().isBlank()) {
                Visitante visitante = new Visitante(txtNome.getText(), "01/01/2000", txtCpf.getText(), 0);
                ContextoController.VISITANTES.cadastrar(visitante);
            } else {
                Operador operador = new Operador(txtNome.getText(), "01/01/2000", txtCpf.getText(), txtCargo.getText());
                ContextoController.ATRACOES.cadastrarOperador(txtCargo.getText(), operador);
            }
            txtNome.setText("");
            txtCpf.setText("");
            txtCargo.setText("");
        }
    }
}
