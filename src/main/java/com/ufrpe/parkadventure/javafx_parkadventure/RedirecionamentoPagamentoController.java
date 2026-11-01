package com.ufrpe.parkadventure.javafx_parkadventure;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;

public class RedirecionamentoPagamentoController {

    @FXML
    private Label lblMensagem;

    @FXML
    public void initialize() {
        PauseTransition pausa = new PauseTransition(Duration.seconds(2));
        pausa.setOnFinished(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        HelloApplication.class.getResource("pagamento.fxml"));
                Scene scene = new Scene(loader.load());

                PagamentoController pagamento = loader.getController();
                pagamento.setResumo("Ingresso day use", LocalDate.now(), 1, 120.00);

                Stage stage = (Stage) lblMensagem.getScene().getWindow();
                stage.setScene(scene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        pausa.play();
    }
}
