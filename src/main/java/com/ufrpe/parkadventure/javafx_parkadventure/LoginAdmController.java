package com.ufrpe.parkadventure.javafx_parkadventure;

// import javafx.event.ActionEvent;
import com.ufrpe.parkadventure.javafx_parkadventure.controller.ParkAdventure;
import com.ufrpe.parkadventure.javafx_parkadventure.repository.ListaVisitante;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
// import javafx.scene.control.TextField;
// import javafx.scene.control.Alert;
// import javafx.scene.control.Alert.AlertType;
// import javafx.scene.control.Label;
import javafx.fxml.FXML;
// import com.ufrpe.parkadventure.ParkAdventure;

public class LoginAdmController {

    @FXML
    Button btVoltar;

    @FXML
    Label lblBoasVindas;

    @FXML
    void initialize() {
        lblBoasVindas.setText("Bem-vindo a tela de Administrador, " + ListaVisitante.buscar("Leandro").getNome() + "!");
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
