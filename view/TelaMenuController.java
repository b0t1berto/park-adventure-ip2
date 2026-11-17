package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.ParkAdventure;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class TelaMenuController {

    @FXML private Label lblDesejaReservar, lblReservar, lblParkAdventure;

    @FXML private Button bntComeceAqui, bntAcesseAqui;

    @FXML private StackPane cabecalhoImagem;
    @FXML private ImageView imgFundo;

    @FXML
    private void initialize() {
    }


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