package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.app.Contexto;
import com.ufrpe.parkadventure.app.PedidoAtual;
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
                // as células são reaproveitadas ao trocar de mês: limpa o que a data anterior deixou
                setDisable(false);
                setStyle("");
                setTooltip(null);
                if (data != null && (data.isBefore(hoje.plusDays(1)) || data.isAfter(limite))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #eeeeee; -fx-text-fill: #aaaaaa;");
                } else if (data != null && !vazio && Contexto.SERVICO_CAPACIDADE.estaEsgotado(data)) {
                    // capacidade do dia atingida: não dá para escolher
                    setDisable(true);
                    setStyle("-fx-background-color: #ffd6d6; -fx-text-fill: #b3261e;");
                    setTooltip(new Tooltip("Esgotado: capacidade máxima do parque atingida"));
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
        if (Contexto.SERVICO_CAPACIDADE.estaEsgotado(data)) {
            lblErro.setText(Contexto.SERVICO_CAPACIDADE.mensagemEsgotado(data));
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