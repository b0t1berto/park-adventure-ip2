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
import javafx.scene.control.TextField;

public class CriarContaController {

    @FXML
    private Label lblNome, lblCpf, lblCargo, lblTexto, lblTextoConfirmar;

    @FXML
    private TextField txtFieldNome, txtFieldCpf, txtFieldCargo;

    @FXML
    private Button btVoltar, btOk;

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

        if (txtFieldNome.getText().isBlank()|| txtFieldCpf.getText().isBlank()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERRO");
            alert.setHeaderText("Suas informações estão Inválidas!");
            alert.setContentText("Digite informações válidas para criar sua conta.");
            alert.showAndWait(); //Apenas quando apertar OK para sair da tela de aviso

            txtFieldNome.setText("");
            txtFieldCpf.setText("");
            txtFieldCargo.setText("");
            return;
        } else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("CONFIRMAÇÃO");
            alert.setHeaderText("Confirme os dados da sua conta");
            alert.setContentText("Seu nome é: " + this.txtFieldNome.getText() + "\nSeu CPF é: " + this.txtFieldCpf.getText() + " ?");
            alert.showAndWait(); //Apenas quando apertar OK para sair da tela de aviso
            lblTextoConfirmar.setText("Conta criada com sucesso!");

            if (txtFieldNome.getText().equalsIgnoreCase("leandro")) {
                Visitante leandro = new Visitante(txtFieldNome.getText(), "01/01/2000", txtFieldCpf.getText(), 0);
                ContextoController.VISITANTES.cadastrar(leandro);
            } else if (txtFieldCargo.getText().isBlank()) {
                Visitante visitante = new Visitante(txtFieldNome.getText(), "01/01/2000", txtFieldCpf.getText(), 0);
                ContextoController.VISITANTES.cadastrar(visitante);
            } else {
                Operador operador = new Operador(txtFieldNome.getText(), "01/01/2000", txtFieldCpf.getText(), txtFieldCargo.getText());
                ContextoController.atracoes.cadastrarOperador(txtFieldCargo.getText(), operador);
            }
            txtFieldNome.setText("");
            txtFieldCpf.setText("");
            txtFieldCargo.setText("");
        }
    }
}
