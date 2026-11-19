package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.app.Contexto;
import com.ufrpe.parkadventure.app.PedidoAtual;
import com.ufrpe.parkadventure.model.ingresso.TipoIngresso;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.io.IOException;
import java.time.LocalDate;

public class CompraIngressosController {

    @FXML private Button btnInstantaneo;
    @FXML private Button btnProgramado;
    @FXML private Button btnPasseAnual;

    @FXML
    private void initialize() {
        double precoDiaria = Contexto.SERVICO_EMISSAO_INGRESSO.calcularValor(TipoIngresso.DIARIA, false);
        double precoAnual = Contexto.SERVICO_EMISSAO_INGRESSO.calcularValor(TipoIngresso.PASSE_ANUAL, false);
        String moedaDiaria = Formatos.moeda(precoDiaria);
        String moedaAnual = Formatos.moeda(precoAnual);
        btnInstantaneo.setText("Ingresso instantâneo\n" + moedaDiaria);
        btnProgramado.setText("Ingresso programado\n" + moedaDiaria);
        btnPasseAnual.setText("Passe Anual — 12 entradas (1 por mês)\n" + moedaAnual);
    }

    @FXML
    private void onIngressoProgramado(ActionEvent event) throws IOException {
        try {
            PedidoAtual.setDataVisita(null);   // volta para "hoje" (null = hoje)
            PedidoAtual.setTipoIngresso(TipoIngresso.DIARIA);
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
            PedidoAtual.setTipoIngresso(TipoIngresso.DIARIA);
            ParkAdventure.proximaCena("view/redirecionamento-pagamento-ingresso.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onPasseAnual(ActionEvent event) throws IOException {
        // Verifica se o visitante já comprou um passe anual neste mês
        Visitante visitante = Contexto.SESSAO.getVisitanteAtual();
        if (visitante != null && !Contexto.SERVICO_EMISSAO_INGRESSO.podeComprarPasseAnual(visitante)) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.initOwner(((Node) event.getSource()).getScene().getWindow());
            alerta.setHeaderText("Limite atingido");
            alerta.setContentText("Você já comprou um passe anual este mês. O limite é de 1 compra por mês.");
            alerta.showAndWait();
            return;
        }
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.initOwner(((Node) event.getSource()).getScene().getWindow());
        alerta.setHeaderText("Passe Anual");
        alerta.setContentText("O passe anual inclui 12 entradas, com limite de 1 entrada por mês. Deseja continuar?");
        alerta.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    PedidoAtual.setDataVisita(null);
                    PedidoAtual.setTipoIngresso(TipoIngresso.PASSE_ANUAL);
                    ParkAdventure.proximaCena("view/pagamento.fxml");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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