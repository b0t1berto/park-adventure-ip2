package com.ufrpe.parkadventure.javafx_parkadventure;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class PagamentoConfirmadoController {

    @FXML private Label lblSituacao, lblCodigo, lblForma, lblValor, lblData;
    @FXML private Button btnNovoPedido, btnConcluir;

    @FXML
    private void initialize() {
        // código fictício: em um sistema real, viria do banco de dados
        String codigo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        lblCodigo.setText("#" + codigo);
        lblData.setText(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    // chamado pela tela anterior, depois do load()
    public void setDados(String forma, double valor, String situacao) {
        lblForma.setText(forma);
        lblValor.setText(String.format("R$ %.2f", valor));
        lblSituacao.setText(situacao);
    }

    @FXML
    private void aoNovoPedido() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource("menu.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnNovoPedido.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void aoConcluir() {
        Platform.exit();
    }
}