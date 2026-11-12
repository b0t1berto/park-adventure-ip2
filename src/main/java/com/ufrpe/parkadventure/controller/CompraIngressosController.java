package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class CompraIngressosController {

    @FXML
    private void onIngressoProgramado(ActionEvent event) throws IOException {
        try {
            PedidoAtual.setDataVisita(null);   // volta para "hoje" (null = hoje)
            ParkAdventure.proximaCena("view/escolha-data.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void onIngressoInstantaneo(ActionEvent event) throws IOException {
        try {
            // ingresso instantâneo é sempre para hoje (a data de uma tentativa "programada" anterior não vale)
            PedidoAtual.setDataVisita(null);
            ParkAdventure.proximaCena("view/redirecionamento-pagamento-ingresso.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
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