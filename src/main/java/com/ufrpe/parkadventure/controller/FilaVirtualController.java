package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.atracao.FilaVirtual;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.view.Aviso;

import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FilaVirtualController {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML private Label lblVisitante, lblDestaque, lblAtualizado;
    @FXML private TableView<LinhaFila> tabela;
    @FXML private TableColumn<LinhaFila, String> colAtracao, colHorario, colPosicaoHorario,
            colPosicaoGeral, colTotal, colOcupacao;

    private record LinhaFila(String atracao, String horario, String posicaoHorario,
                             String posicaoGeral, String total, String ocupacao) { }

    private Timeline atualizador;

    @FXML
    private void initialize() {
        colAtracao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().atracao()));
        colHorario.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().horario()));
        colPosicaoHorario.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().posicaoHorario()));
        colPosicaoGeral.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().posicaoGeral()));
        colTotal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().total()));
        colOcupacao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().ocupacao()));
        tabela.setPlaceholder(new Label("Você não está em nenhuma fila virtual.\n"
                + "Entre em uma pela tela Verificação de atrações."));

        Visitante visitante = ContextoController.SESSAO.getVisitanteAtual();
        if (visitante == null) {
            lblVisitante.setText("Escolha um visitante na caixa do topo da janela.");
            return;
        }
        lblVisitante.setText("Filas virtuais de " + visitante.getNome());
        carregar();

        atualizador = new Timeline(new KeyFrame(Duration.seconds(5), evento -> carregar()));
        atualizador.setCycleCount(Timeline.INDEFINITE);
        atualizador.play();

        tabela.sceneProperty().addListener((observado, antiga, nova) -> {
            if (nova == null) {
                atualizador.stop();
            }
        });
    }

    private void carregar() {
        Visitante visitante = ContextoController.SESSAO.getVisitanteAtual();
        List<LinhaFila> linhas = new ArrayList<>();
        String atracaoDoProximo = null;

        for (Atracao atracao : RepositorioAtracaoController.listarTodas()) {
            FilaVirtual fila = atracao.getFilaVirtual();
            String horario = fila.horarioDe(visitante);
            if (horario == null) {
                continue;
            }
            int posicaoGeral = fila.posicaoGeral(visitante);
            if (posicaoGeral == 1) {
                atracaoDoProximo = atracao.getNome();
            }
            linhas.add(new LinhaFila(
                    atracao.getNome(),
                    horario,
                    fila.posicaoNoHorario(horario, visitante) + "º",
                    posicaoGeral + "º",
                    String.valueOf(fila.tamanhoTotal()),
                    atracao.getOcupacaoAtual() + "/" + atracao.getCapacidadeMaxima()));
        }
        tabela.setItems(FXCollections.observableArrayList(linhas));

        if (atracaoDoProximo != null) {
            Aviso.sucesso(lblDestaque, "Você é o próximo na fila de " + atracaoDoProximo + "!");
        } else {
            Aviso.limpar(lblDestaque);
        }
        lblAtualizado.setText("Atualizado às " + LocalTime.now().format(HORA) + " (a cada 5 segundos)");
    }

    @FXML
    private void onAtualizar() {
        carregar();
    }

    @FXML
    private void onVoltar(ActionEvent event) {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
