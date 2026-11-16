package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

// Tela com as abas de relatório. Cada aba é um FXML próprio (fx:include) com seu controller;
// aqui só se decide qual aba atualizar e se faz o "Voltar".
public class RelatoriosController {

    @FXML private TabPane tabPane;
    @FXML private Tab tabGeral, tabIngresso, tabLucro, tabAtracao;

    // Injetados pelo fx:include: fx:id="geral" -> geralController, e assim por diante
    @FXML private RelatorioGeralController geralController;
    @FXML private RelatorioIngressoController ingressoController;
    @FXML private RelatorioLucroController lucroController;
    @FXML private RelatorioAtracaoController atracaoController;

    @FXML
    private void initialize() {
        // cada aba carrega os dados quando é aberta (os números sempre saem atualizados)
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, antiga, nova) -> atualizar(nova));
        atualizar(tabPane.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void onAtualizar() {
        atualizar(tabPane.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void onVoltar() {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void atualizar(Tab aba) {
        if (aba == tabGeral) geralController.atualizar();
        else if (aba == tabIngresso) ingressoController.atualizar();
        else if (aba == tabLucro) lucroController.atualizar();
        else if (aba == tabAtracao) atracaoController.atualizar();
    }
}
