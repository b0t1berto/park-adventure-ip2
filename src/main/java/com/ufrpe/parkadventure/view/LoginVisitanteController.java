package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.app.Contexto;

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
        Visitante visitante = Contexto.SESSAO.getVisitanteAtual();
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

    // Passe anual: escolher o dia de cada mês em que o visitante vai usar a entrada
    @FXML
    void onUsarPasse(ActionEvent event) {
        ir("view/usar-passe.fxml");
    }

    @FXML
    void onMinhasReservas(ActionEvent event) {
        ir("view/fila-virtual.fxml");
    }

    @FXML
    void onMeuPerfil(ActionEvent event) {
        ir("view/meu-perfil.fxml");
    }

    // Item "Configurações" do menu: abre a tela de configurações da aplicação
    @FXML
    void onConfiguracoes(ActionEvent event) {
        ir("view/configuracoes.fxml");
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
