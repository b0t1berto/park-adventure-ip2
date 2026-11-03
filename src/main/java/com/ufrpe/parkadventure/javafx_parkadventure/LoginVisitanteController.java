package com.ufrpe.parkadventure.javafx_parkadventure;

import com.ufrpe.parkadventure.javafx_parkadventure.controller.ParkAdventure;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class LoginVisitanteController {

    @FXML
    Button btVoltar;

    @FXML
    Label lblBoasVindas;

    @FXML
    void initialize() {
        lblBoasVindas.setText("Bem-vindo a tela de Visitante!");
    }

    @FXML
    void onVoltar(ActionEvent event) {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
