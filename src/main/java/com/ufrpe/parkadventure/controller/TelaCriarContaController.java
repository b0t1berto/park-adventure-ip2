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
import com.ufrpe.parkadventure.repository.Persistencia;

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
            alert.showAndWait();

            if (txtNome.getText().equalsIgnoreCase("leandro")) {
                Visitante leandro = new Visitante(txtNome.getText(), "01/01/2000", txtCpf.getText(), 0);
                System.out.println("funcionou");
                RepositorioVisitanteController.cadastrarVisitante(leandro);
            } else if (txtCargo.getText().isBlank()) {
                Visitante visitante = new Visitante(txtNome.getText(), "01/01/2000", txtCpf.getText(), 0);
                RepositorioVisitanteController.cadastrarVisitante(visitante);
            } else {
                Operador operador = new Operador(txtNome.getText(), "01/01/2000", txtCpf.getText(), txtCargo.getText());
                RepositorioAtracaoController.cadastrarOperador(txtCargo.getText(), operador);
            }
            Persistencia.salvar();
            txtNome.setText("");
            txtCpf.setText("");
            txtCargo.setText("");
        }
    }
}
