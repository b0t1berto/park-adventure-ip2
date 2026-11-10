package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.pessoa.Visitante;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LoginVisitanteController {

    @FXML
    Label lblBoasVindas;

    @FXML
    private void initialize() {
        Visitante visitante = ContextoController.SESSAO.getVisitanteAtual();
        if (visitante != null && lblBoasVindas != null) {
            lblBoasVindas.setText("Bem-vindo, " + visitante.getNome() + "!");
        }
    }

    @FXML
    void onComprarIngresso(ActionEvent event) {
        ir("view/compra-ingressos.fxml");
    }

    @FXML
    void onVerIngresso(ActionEvent event) {
        ir("view/ingressos-comprados.fxml");
    }

    @FXML
    void onVerAtracoes(ActionEvent event) {
        ir("view/verificacao-atracoes.fxml");
    }

    @FXML
    void onMinhasReservas(ActionEvent event) {
        ir("view/fila-virtual.fxml");
    }

    @FXML
    void onVoltar(ActionEvent event) {
        try {
            ContextoController.SESSAO.limpar();
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ir(String fxml) {
        try {
            ParkAdventure.proximaCena(fxml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
