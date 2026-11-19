package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.ParkAdventure;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class TelaMenuController {

    @FXML private Label lblDesejaReservar, lblReservar, lblParkAdventure;

    @FXML private Button bntComeceAqui, bntAcesseAqui;

    // O fundo do menu agora é desenhado pelo CSS (.fundo-menu, com "cover"), então ele
    // acompanha o tamanho da janela sozinho e não precisa mais de código aqui.

    @FXML
    void onCriarConta(ActionEvent event) {
        try {
            ParkAdventure.proximaCena("view/tela-criar-conta.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onFazerLogin(ActionEvent event) {
        try {
            ParkAdventure.proximaCena("view/tela-de-login.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
