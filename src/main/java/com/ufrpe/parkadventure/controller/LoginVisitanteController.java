package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
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
    void onComprarIngresso(ActionEvent event) {
        try {
            ParkAdventure.proximaCena("view/compra-ingressos.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML 
    void onVerIngresso(ActionEvent event) {
        try {
            ParkAdventure.proximaCena("view/ingressos-comprados.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // @FXML 
    // void onComprarIngresso(ActionEvent event) {
    //     try {
    //         ParkAdventure.proximaCena("view/compra-ingressos.fxml");
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }


    @FXML
    void onVoltar(ActionEvent event) {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
