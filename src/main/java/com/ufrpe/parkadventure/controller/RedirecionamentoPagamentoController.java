package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
                // a tela de redirecionamento é só de passagem: não entra no histórico
                PagamentoController pagamento = ParkAdventure.substituirCena("view/pagamento.fxml");
                pagamento.setResumo("Ingresso day use", PedidoAtual.getDataVisita(), 1, 120.00);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        pausa.play();
    }
}
