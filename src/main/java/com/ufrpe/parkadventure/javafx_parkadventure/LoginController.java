package com.ufrpe.parkadventure.javafx_parkadventure;

import com.ufrpe.parkadventure.javafx_parkadventure.controller.ParkAdventure;
import com.ufrpe.parkadventure.javafx_parkadventure.repository.ListaVisitante;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.fxml.FXML;

public class LoginController {

    @FXML 
    Label lblNome, lblCpf, lblCargo;

    @FXML 
    TextField txtFieldNome, txtFieldCpf, txtFieldCargo;

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
    public void onConfirmar (ActionEvent event) {
        String nome = txtFieldNome.getText();

        if (txtFieldNome.getText().isBlank() || txtFieldCpf.getText().isBlank()) {
            Alert alert = new Alert(AlertType.ERROR); 
            alert.setTitle("AVISO");
            alert.setHeaderText("Suas informações estão Inválidas!");
            alert.setContentText("Digite informações válidas para entrar na conta.");
            alert.showAndWait(); //Apenas quando apertar OK para sair da tela de aviso

            txtFieldNome.setText("");
            txtFieldCpf.setText("");
            txtFieldCargo.setText("");
        } else {
            if (txtFieldNome.getText().equalsIgnoreCase("leandro")) {
                try {
                    ParkAdventure.proximaCena("view/menu-adm.fxml");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (txtFieldCargo.getText().isBlank())  {
                try {
                    ParkAdventure.proximaCena("view/menu-visitante.fxml");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (ListaVisitante.buscar(nome) == null) {
                try {                
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("AVISO");
                    alert.setHeaderText("Visitante não encontrado!");
                    alert.setContentText("O visitante não está cadastrado.");
                    alert.showAndWait();
                    
                    txtFieldNome.setText("");
                    txtFieldCpf.setText("");
                    txtFieldCargo.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    ParkAdventure.proximaCena("view/menu-operador.fxml");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
