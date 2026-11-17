package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.app.PedidoAtual;
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
        // a compra terminou: "voltar" não deve levar de volta para o pagamento nem para a compra
        ParkAdventure.descartarHistorico("view/compra-ingressos.fxml", "view/escolha-data.fxml");
        String codigo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        lblCodigo.setText("#" + codigo);
        lblData.setText(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    public void setDados(String forma, double valor, String situacao) {
        lblForma.setText(forma);
        lblValor.setText(String.format("R$ %.2f", valor));
        lblSituacao.setText(situacao);
        if (PedidoAtual.concluir(forma, valor) == null && PedidoAtual.ultimaTentativaEsgotada()) {
            // a capacidade do dia foi atingida entre o pagamento e a emissão: o ingresso não é emitido
            lblSituacao.setText("Capacidade esgotada: ingresso não emitido");
            btnReservar.setDisable(true);
        }
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

    // A compra terminou: volta para o menu do visitante (e não para o menu inicial, com o login)
    @FXML
    private void aoConcluir() {
        try {
            ParkAdventure.voltarAte("view/menu-visitante.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ir(String fxml) {
        try {
            ParkAdventure.substituirCenaPadrao(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
