package com.ufrpe.parkadventure.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.ufrpe.parkadventure.ParkAdventure;

public class EscolhaDataController {

    private static final int DIAS_MAXIMOS = 180;

    @FXML private DatePicker dpDataVisita;
    @FXML private Label lblData, lblErro;
    @FXML private Button btnVoltar, btnContinuar;

    @FXML
    private void initialize() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(DIAS_MAXIMOS);

        dpDataVisita.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate data, boolean vazio) {
                super.updateItem(data, vazio);
                if (data != null && (data.isBefore(hoje.plusDays(1)) || data.isAfter(limite))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #eeeeee; -fx-text-fill: #aaaaaa;");
                }
            }
        });

        btnContinuar.disableProperty().bind(dpDataVisita.valueProperty().isNull());

        dpDataVisita.valueProperty().addListener((obs, antiga, nova) -> {
            lblErro.setText("");
            lblData.setText(nova == null
                    ? "Nenhuma data selecionada"
                    : "Visita em " + nova.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        });
    }

    @FXML
    private void aoContinuar() {
        LocalDate data = dpDataVisita.getValue();
        if (data == null || !data.isAfter(LocalDate.now())) {
            lblErro.setText("Escolha uma data a partir de amanhã.");
            return;
        }
        try {
            // guarda a data escolhida: sem isso o ingresso "programado" era emitido para hoje
            PedidoAtual.setDataVisita(data);
            ParkAdventure.proximaCena("view/redirecionamento-pagamento-ingresso.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void aoVoltar() {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}