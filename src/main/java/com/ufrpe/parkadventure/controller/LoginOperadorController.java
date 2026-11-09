package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.pessoa.Operador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

public class LoginOperadorController {

    @FXML
    MenuItem menuVoltar;

    @FXML
    Label lblBoasVindas;

    @FXML
    private void initialize() {
        Operador operador = ContextoController.SESSAO.getOperadorAtual();
        if (operador != null && lblBoasVindas != null) {
            lblBoasVindas.setText("Olá, " + operador.getNome() + "! Menu de Operador");
        }
    }

    @FXML
    void onControleAtracao(ActionEvent event) {
        ir("view/controle-atracao.fxml");
    }

    @FXML
    void onEscala(ActionEvent event) {
        ir("view/escala-trabalho.fxml");
    }

    @FXML
    void onCalendario(ActionEvent event) {
        ir("view/calendario.fxml");
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
