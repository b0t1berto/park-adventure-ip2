package com.ufrpe.parkadventure.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.ufrpe.parkadventure.model.pessoa.*;
import com.ufrpe.parkadventure.repository.Persistencia;
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
    String nome = txtNome.getText().trim();
    String cpf = txtCpf.getText().replaceAll("[^0-9]", "").trim(); // Remove pontos e traços do CPF
    String cargo = txtCargo.getText().trim();

    if (nome.isBlank() || cpf.isBlank()) {
        Alert alert = new Alert(AlertType.ERROR); 
        alert.setTitle("ERRO");
        alert.setHeaderText("Suas informações estão Inválidas!");
        alert.setContentText("Digite informações válidas para criar sua conta.");
        alert.showAndWait();

        txtNome.setText("");
        txtCpf.setText("");
        txtCargo.setText("");
        return;
    } 

    Alert alert = new Alert(AlertType.INFORMATION); 
    alert.setTitle("CONFIRMAÇÃO");
    alert.setHeaderText("Confirme os dados da sua conta");
    alert.setContentText("Seu nome é: " + nome + "\nSeu CPF é: " + cpf + " ?");
    alert.showAndWait();

    if (cargo.isBlank()) {
        Visitante visitante = new Visitante(nome, "01/01/2000", cpf, 0);
        RepositorioVisitanteController.cadastrarVisitante(visitante);
    } else {
        Operador operador = new Operador(nome, "01/01/2000", cpf, cargo);
        RepositorioAtracaoController.cadastrarOperador(cargo, operador);
    }

    Persistencia.salvar();
    
    txtNome.setText("");
    txtCpf.setText("");
    txtCargo.setText("");
}
}
