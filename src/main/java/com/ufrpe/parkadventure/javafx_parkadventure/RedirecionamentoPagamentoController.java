package com.ufrpe.parkadventure.javafx_parkadventure;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class RedirecionamentoPagamentoController {

    @FXML
    private Label lblMensagem;

    @FXML
    public void initialize() {
        PauseTransition pausa = new PauseTransition(Duration.seconds(2));
        pausa.setOnFinished(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        HelloApplication.class.getResource(""));
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) lblMensagem.getScene().getWindow();
                stage.setScene(scene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        pausa.play();
    }
}
