package com.ufrpe.parkadventure.javafx_parkadventure;

import javafx.fxml.FXML;

public class CompraIngressosController {

    @FXML
    private void onIngressoInstantaneo() {
        System.out.println("Ingresso instantâneo");
    }

    @FXML
    private void onIngressoProgramado() {
        System.out.println("Ingresso programado");
    }
}