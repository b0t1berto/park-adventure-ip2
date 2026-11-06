package com.ufrpe.parkadventure.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.ufrpe.parkadventure.ParkAdventure;

public class TelaMenuController {

    // @FXML 
    // Label 

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