package com.ufrpe.parkadventure.javafx_parkadventure;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EscolhaDataController {

    private static final int DIAS_MAXIMOS = 180;

    @FXML private DatePicker dpDataVisita;
    @FXML private Label lblData, lblErro;
    @FXML private Button btnVoltar, btnContinuar;

    @FXML
    private void initialize() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(DIAS_MAXIMOS);

        dpDataVisita.setEditable(false);

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
            FXMLLoader loader = new FXMLLoader(HelloApplication.class
                    .getResource("entrar-fila-virtual.fxml"));
            Scene scene = new Scene(loader.load());
            EntrarFilaVirtualController fila = loader.getController();
            fila.setDataVisita(data);
            ((Stage) btnContinuar.getScene().getWindow()).setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void aoVoltar() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource("compra-ingressos.fxml"));
            ((Stage) btnVoltar.getScene().getWindow()).setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}