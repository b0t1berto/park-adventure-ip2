package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

// Menu do administrador: leva para relatórios, cadastro de atrações e cadastro de operadores
public class LoginAdmController {

    @FXML
    MenuItem menuVoltar;

    @FXML
    Label lblBoasVindas;

    @FXML
    void onRelatorios(ActionEvent event) {
        abrir("view/relatorios.fxml");
    }

    @FXML
    void onCadastroAtracao(ActionEvent event) {
        abrir("view/cadastro-atracao.fxml");
    }

    @FXML
    void onCadastroOperador(ActionEvent event) {
        abrir("view/cadastro-operador.fxml");
    }

    @FXML
    void onVoltar(ActionEvent event) {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrir(String fxml) {
        try {
            ParkAdventure.proximaCena(fxml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
