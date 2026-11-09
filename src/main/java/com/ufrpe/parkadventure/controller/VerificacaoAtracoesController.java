package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.atracao.Infantil;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.view.Aviso;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

// Tela do visitante para consultar as atrações. Quem conta entradas/saídas é o operador (tela Controle da atração).
public class VerificacaoAtracoesController {

    @FXML private Label lblVisitante, lblResultado;
    @FXML private TableView<LinhaAtracao> tabela;
    @FXML private TableColumn<LinhaAtracao, String> colNome, colRequisitos, colOcupacao, colFuraFila, colSituacao;

    private record LinhaAtracao(String nome, String tipo, String requisitos, String ocupacao,
                                String furaFila, String situacao) { }

    @FXML
    private void initialize() {
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().nome()));
        colRequisitos.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().requisitos()));
        colOcupacao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().ocupacao()));
        colFuraFila.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().furaFila()));
        colSituacao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().situacao()));
        colSituacao.setCellFactory(coluna -> new TableCell<LinhaAtracao, String>() {
            @Override
            protected void updateItem(String texto, boolean vazio) {
                super.updateItem(texto, vazio);
                getStyleClass().removeAll("sit-ok", "sit-alerta", "sit-bloqueio");
                if (vazio || texto == null) {
                    setText(null);
                    return;
                }
                setText(texto);
                if (texto.startsWith("Liberada") || texto.startsWith("Aberta")) {
                    getStyleClass().add("sit-ok");
                } else if (texto.startsWith("Lotada")) {
                    getStyleClass().add("sit-alerta");
                } else {
                    getStyleClass().add("sit-bloqueio");
                }
            }
        });
        tabela.setPlaceholder(new Label("Nenhuma atração cadastrada."));

        Visitante visitante = ContextoController.SESSAO.getVisitanteAtual();
        if (visitante == null) {
            lblVisitante.setText("Faça login para ver se você pode entrar em cada atração.");
        } else {
            lblVisitante.setText("Atrações para " + visitante.getNome());
        }

        List<LinhaAtracao> linhas = new ArrayList<>();
        for (Atracao atracao : RepositorioAtracaoController.listarTodas()) {
            linhas.add(new LinhaAtracao(
                    atracao.getNome() + " (" + atracao.getTipo() + ")",
                    atracao.getTipo(),
                    requisitos(atracao),
                    atracao.getOcupacaoAtual() + "/" + atracao.getCapacidadeMaxima(),
                    atracao.vagasFuraFilaPorHorario() + " vaga(s) por horário",
                    situacao(atracao, visitante)));
        }
        tabela.setItems(FXCollections.observableArrayList(linhas));
        Aviso.info(lblResultado, "Quem não tem reserva espera na fila comum da atração. "
                + "O fura-fila é um passe por horário: reserve em “Reservar fura-fila”.");
    }

    private String requisitos(Atracao atracao) {
        if (atracao instanceof Infantil infantil) {
            return "Até " + infantil.getAlturaMaxima() + " cm e " + infantil.getIdadeMaxima() + " anos";
        }
        return "A partir de " + atracao.getAlturaMinima() + " cm e " + atracao.getIdadeMinima() + " anos";
    }

    private String situacao(Atracao atracao, Visitante visitante) {
        if (!atracao.temOperador()) {
            return "Fechada (sem operador)";
        }
        if (visitante != null) {
            switch (atracao.verificarRestricao(visitante)) {
                case FORA_DOS_REQUISITOS: return "Fora dos seus requisitos";
                case SEM_RESPONSAVEL:     return "Menor: precisa de responsável";
                default:                  break;
            }
        }
        if (atracao.estaLotada()) {
            return "Lotada: aguarde na fila ou reserve o fura-fila";
        }
        return visitante == null ? "Aberta" : "Liberada para você";
    }

    @FXML
    private void onReservar() {
        try {
            ParkAdventure.proximaCena("view/fila-virtual-cadastro.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onVoltar() {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}