package com.ufrpe.parkadventure.javafx_parkadventure;

import com.ufrpe.parkadventure.javafx_parkadventure.controller.ParkAdventure;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MenuController {

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