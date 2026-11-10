package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.atracao.EstadoReserva;
import com.ufrpe.parkadventure.model.atracao.FilaVirtual;
import com.ufrpe.parkadventure.model.atracao.Reserva;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.view.Aviso;
import com.ufrpe.parkadventure.view.Formatos;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// "Minhas reservas": os passes de fura-fila do visitante e a situação de cada um
public class FilaVirtualController {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter HORA_SEGUNDOS = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML private Label lblVisitante, lblDestaque, lblAtualizado, lblStatus;
    @FXML private TableView<LinhaReserva> tabela;
    @FXML private TableColumn<LinhaReserva, String> colAtracao, colData, colHorario, colSituacao, colObservacao;

    private record LinhaReserva(Atracao atracao, Reserva reserva, String nomeAtracao, String data,
                                String horario, String situacao, String observacao) { }

    private Timeline atualizador;

    @FXML
    private void initialize() {
        colAtracao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().nomeAtracao()));
        colData.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().data()));
        colHorario.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().horario()));
        colSituacao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().situacao()));
        colObservacao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().observacao()));
        lblDestaque.visibleProperty().bind(lblDestaque.textProperty().isNotEmpty());
        lblDestaque.managedProperty().bind(lblDestaque.visibleProperty());
        tabela.setPlaceholder(new Label("Você ainda não tem reservas de fura-fila.\n"
                + "Use “Nova reserva” para marcar um horário."));

        Visitante visitante = ContextoController.SESSAO.getVisitanteAtual();
        if (visitante == null) {
            lblVisitante.setText("Faça login para ver suas reservas.");
            return;
        }
        lblVisitante.setText("Reservas de fura-fila de " + visitante.getNome());
        carregar();

        // atualiza a cada 5 segundos e para sozinho quando a tela sai da janela
        atualizador = new Timeline(new KeyFrame(Duration.seconds(5), evento -> {
            if (tabela.getScene() == null || tabela.getScene().getWindow() == null) {
                atualizador.stop();
            } else {
                carregar();
            }
        }));
        atualizador.setCycleCount(Timeline.INDEFINITE);
        atualizador.play();
    }

    private void carregar() {
        Visitante visitante = ContextoController.SESSAO.getVisitanteAtual();
        LocalDateTime agora = LocalDateTime.now();
        LocalDate hoje = agora.toLocalDate();
        List<LinhaReserva> linhas = new ArrayList<>();
        String vezAgora = null;

        for (Atracao atracao : RepositorioAtracaoController.listarTodas()) {
            FilaVirtual fila = atracao.getFilaVirtual();
            fila.expirarNoShows(agora);
            for (Reserva r : fila.reservasDe(visitante)) {
                if (r.getData().isBefore(hoje)) {
                    continue;   // reservas de dias passados não aparecem
                }
                boolean naJanela = fila.elegivel(visitante, agora) == r;
                if (naJanela) {
                    vezAgora = "Sua vez em " + atracao.getNome() + "! Apresente-se à entrada de prioridade até "
                            + limite(r).format(HORA) + ".";
                }
                linhas.add(new LinhaReserva(atracao, r, atracao.getNome(), Formatos.data(r.getData()),
                        r.getHorario().format(HORA), situacao(r), observacao(fila, visitante, r, naJanela)));
            }
        }
        tabela.setItems(FXCollections.observableArrayList(linhas));

        if (vezAgora != null) {
            Aviso.sucesso(lblDestaque, vezAgora);
        } else {
            Aviso.limpar(lblDestaque);
        }
        lblAtualizado.setText("Atualizado às " + agora.format(HORA_SEGUNDOS) + " (a cada 5 segundos)");
    }

    private LocalTime limite(Reserva r) {
        return r.getHorario().plus(FilaVirtual.TOLERANCIA);
    }

    private String situacao(Reserva r) {
        return switch (r.getEstado()) {
            case RESERVADA -> "Reservada";
            case USADA -> "Utilizada";
            case NO_SHOW -> "Não compareceu";
            case CANCELADA -> "Cancelada";
        };
    }

    private String observacao(FilaVirtual fila, Visitante visitante, Reserva r, boolean naJanela) {
        if (r.getEstado() != EstadoReserva.RESERVADA) {
            return "";
        }
        if (naJanela) {
            return "Vá agora ao operador (até " + limite(r).format(HORA) + ")";
        }
        return "Apresente-se ao operador entre " + r.getHorario().format(HORA) + " e " + limite(r).format(HORA)
                + "  •  " + fila.posicaoNoHorario(visitante, r.getData()) + "º neste horário";
    }

    @FXML
    private void onCancelar() {
        LinhaReserva linha = tabela.getSelectionModel().getSelectedItem();
        if (linha == null) {
            Aviso.erro(lblStatus, "Selecione uma reserva na tabela.");
            return;
        }
        if (linha.reserva().getEstado() != EstadoReserva.RESERVADA) {
            Aviso.erro(lblStatus, "Só dá para cancelar uma reserva que ainda está ativa.");
            return;
        }
        Visitante visitante = ContextoController.SESSAO.getVisitanteAtual();
        if (ContextoController.CONTROLE_ACESSO.cancelarReserva(visitante, linha.atracao(), linha.reserva().getData())) {
            Aviso.sucesso(lblStatus, "Reserva em " + linha.nomeAtracao() + " cancelada.");
        } else {
            Aviso.erro(lblStatus, "Não foi possível cancelar essa reserva.");
        }
        carregar();
    }

    @FXML
    private void onAtualizar() {
        if (ContextoController.SESSAO.getVisitanteAtual() != null) {
            carregar();
        }
    }

    @FXML
    private void onNovaReserva() {
        try {
            ParkAdventure.proximaCena("view/fila-virtual-cadastro.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onVoltar() {
        try {
            if (atualizador != null) {
                atualizador.stop();
            }
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
