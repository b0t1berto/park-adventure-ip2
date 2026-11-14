package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.pessoa.Operador;

import java.time.LocalDateTime;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
        // só entra na tela de controle quem está no próprio turno
        String motivo = TurnoDoOperador.motivoSemAcesso(ContextoController.SESSAO.getOperadorAtual(), LocalDateTime.now());
        if (motivo != null) {
            Alert aviso = new Alert(Alert.AlertType.WARNING, motivo);
            aviso.setHeaderText("Fora do seu turno");
            aviso.showAndWait();
            return;
        }
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
