package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.view.Formatos;
import com.ufrpe.parkadventure.controller.controles.Navegador;
import com.ufrpe.parkadventure.controller.controles.Navegador.Tela;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IngressosCompradosController {

    @FXML private Label lblVisitante, lblResumo;
    @FXML private TableView<LinhaIngresso> tabela;
    @FXML private TableColumn<LinhaIngresso, String> colTipo, colData, colValidade, colValor, colSituacao;

    private record LinhaIngresso(String tipo, String data, String validade, String valor, String situacao) { }

    @FXML
    private void initialize() {
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().tipo()));
        colData.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().data()));
        colValidade.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().validade()));
        colValor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().valor()));
        colSituacao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().situacao()));
        tabela.setPlaceholder(new Label("Nenhum ingresso comprado ainda."));

        Visitante visitante = ContextoController.SESSAO.getVisitanteAtual();
        if (visitante == null) {
            lblVisitante.setText("Escolha um visitante na caixa do topo da janela.");
            lblResumo.setText("");
            return;
        }
        lblVisitante.setText("Ingressos de " + visitante.getNome());

        List<Ingresso> ingressos = new ArrayList<>(ContextoController.INGRESSOS.listarPorVisitante(visitante));
        Collections.reverse(ingressos);

        LocalDate hoje = LocalDate.now();
        double total = 0;
        List<LinhaIngresso> linhas = new ArrayList<>();
        for (Ingresso ingresso : ingressos) {
            total += ingresso.getValor();
            linhas.add(new LinhaIngresso(
                    Formatos.tipoIngresso(ingresso.getTipoIngresso()),
                    Formatos.data(ingresso.getDataCompra()),
                    Formatos.data(ingresso.getDataValidade()),
                    Formatos.moeda(ingresso.getValor()),
                    ingresso.valido(hoje) ? "Válido" : "Expirado"));
        }
        tabela.setItems(FXCollections.observableArrayList(linhas));
        lblResumo.setText(ingressos.size() + (ingressos.size() == 1 ? " ingresso" : " ingressos")
                + "  •  total gasto: " + Formatos.moeda(total));
    }

    @FXML
    private void onVerFilaVirtual() {
        Navegador.ir(Tela.FILA_VIRTUAL);
    }

    @FXML
    private void onComprarIngresso() {
        Navegador.ir(Tela.INGRESSO);
    }
}
