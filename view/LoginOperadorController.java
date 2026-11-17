package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.app.Contexto;
import com.ufrpe.parkadventure.app.TurnoDoOperador;

import com.ufrpe.parkadventure.ParkAdventure;

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
    void onControleAtracao(ActionEvent event) {
        // só entra na tela de controle quem está no próprio turno
        String motivo = TurnoDoOperador.motivoSemAcesso(Contexto.SESSAO.getOperadorAtual(), LocalDateTime.now());
        if (motivo != null) {
            Alert aviso = new Alert(Alert.AlertType.WARNING, motivo);
            aviso.setHeaderText("Fora do seu turno");
            aviso.initOwner(lblBoasVindas.getScene().getWindow());
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
            Contexto.SESSAO.limpar();
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
