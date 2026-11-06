package com.ufrpe.parkadventure.controller;

// import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import com.ufrpe.parkadventure.ParkAdventure;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
// import javafx.scene.control.TextField;
// import javafx.scene.control.Alert;
// import javafx.scene.control.Alert.AlertType;
// import javafx.scene.control.Label;
import javafx.fxml.FXML;
// import com.ufrpe.parkadventure.ParkAdventure;

public class LoginAdmController {

    @FXML
    MenuItem menuVoltar;

    @FXML
    Label lblBoasVindas;

    @FXML
    void onVoltar(ActionEvent event) {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
