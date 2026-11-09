package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
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

    private LocalDate dataVisita = LocalDate.now();

    public void setDataVisita(LocalDate dataVisita) {
        this.dataVisita = dataVisita;
    }

    @FXML
    public void initialize() {
        PauseTransition pausa = new PauseTransition(Duration.seconds(2));
        pausa.setOnFinished(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        ParkAdventure.class.getResource("view/pagamento.fxml"));
                Scene scene = ParkAdventure.criarCena(loader.load());

                PagamentoController pagamento = loader.getController();
                pagamento.setResumo("Ingresso day use", dataVisita, 1, 120.00);

                Stage stage = (Stage) lblMensagem.getScene().getWindow();
                stage.setScene(scene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        pausa.play();
    }
}
