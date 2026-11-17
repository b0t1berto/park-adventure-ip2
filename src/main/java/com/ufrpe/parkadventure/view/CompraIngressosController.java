package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.app.Contexto;
import com.ufrpe.parkadventure.app.PedidoAtual;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.time.LocalDate;

public class CompraIngressosController {

    @FXML
    private void onIngressoProgramado(ActionEvent event) throws IOException {
        try {
            PedidoAtual.setDataVisita(null);   // volta para "hoje" (null = hoje)
            ParkAdventure.proximaCena("view/escolha-data.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void onIngressoInstantaneo(ActionEvent event) throws IOException {
        // capacidade do dia atingida: não vende ingresso para hoje
        LocalDate hoje = LocalDate.now();
        if (Contexto.SERVICO_CAPACIDADE.estaEsgotado(hoje)) {
            Alert alerta = new Alert(Alert.AlertType.WARNING, Contexto.SERVICO_CAPACIDADE.mensagemEsgotado(hoje));
            alerta.setHeaderText("Ingressos esgotados");
            alerta.initOwner(((Node) event.getSource()).getScene().getWindow());
            alerta.showAndWait();
            return;
        }
        try {
            // ingresso instantâneo é sempre para hoje (a data de uma tentativa "programada" anterior não vale)
            PedidoAtual.setDataVisita(null);
            ParkAdventure.proximaCena("view/redirecionamento-pagamento-ingresso.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML 
    void onVoltar(ActionEvent event) {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}