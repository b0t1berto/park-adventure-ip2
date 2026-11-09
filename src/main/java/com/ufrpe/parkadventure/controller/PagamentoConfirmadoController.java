package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class PagamentoConfirmadoController {

    @FXML private Label lblSituacao, lblCodigo, lblForma, lblValor, lblData;
    @FXML private Button btnReservar, btnNovoPedido, btnConcluir;

    @FXML
    private void initialize() {
        String codigo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        lblCodigo.setText("#" + codigo);
        lblData.setText(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    public void setDados(String forma, double valor, String situacao) {
        lblForma.setText(forma);
        lblValor.setText(String.format("R$ %.2f", valor));
        lblSituacao.setText(situacao);
        PedidoAtual.concluir(forma, valor);
    }

    // Com o ingresso emitido já dá para reservar o fura-fila
    @FXML
    private void aoReservarFilaVirtual() {
        ir("view/fila-virtual-cadastro.fxml");
    }

    @FXML
    private void aoNovoPedido() {
        ir("view/compra-ingressos.fxml");
    }

    @FXML
    private void aoConcluir() {
        Platform.exit();
    }

    private void ir(String fxml) {
        try {
            ParkAdventure.proximaCena(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
