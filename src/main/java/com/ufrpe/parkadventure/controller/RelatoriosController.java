package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.app.Contexto;
import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.view.Formatos;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Tela com as abas de relatório. Cada aba é um FXML próprio (fx:include) com seu controller;
// aqui só se decide qual aba atualizar e se faz o "Voltar".
public class RelatoriosController {

    @FXML private TabPane tabPane;
    @FXML private Tab tabGeral, tabDia, tabIngresso, tabLucro, tabAtracao, tabListaIngressos;
    @FXML private TextField txtBuscaIngresso;
    @FXML private DatePicker dpDataEmissao;
    @FXML private Label lblTotalIngressos, lblQtdData;
    @FXML private TableView<Ingresso> tabelaIngressos;
    @FXML private TableColumn<Ingresso, String> colIngUsuario, colIngTipo, colIngCompra,
            colIngValidade, colIngValor, colIngPagamento;

    // Injetados pelo fx:include: fx:id="geral" -> geralController, e assim por diante
    @FXML private RelatorioGeralController geralController;
    @FXML private RelatorioDiaController diaController;
    @FXML private RelatorioIngressoController ingressoController;
    @FXML private RelatorioLucroController lucroController;
    @FXML private RelatorioAtracaoController atracaoController;

    @FXML
    private void initialize() {
        // cada aba carrega os dados quando é aberta (os números sempre saem atualizados)
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, antiga, nova) -> atualizar(nova));
        configurarListaIngressos();
        atualizar(tabPane.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void onAtualizar() {
        atualizar(tabPane.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void onVoltar() {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void atualizar(Tab aba) {
        if (aba == tabGeral) geralController.atualizar();
        else if (aba == tabDia) diaController.atualizar();
        else if (aba == tabIngresso) ingressoController.atualizar();
        else if (aba == tabLucro) lucroController.atualizar();
        else if (aba == tabAtracao) atracaoController.atualizar();
        else if (aba == tabListaIngressos) atualizarIngressos();
    }

    private void configurarListaIngressos() {
        colIngUsuario.setCellValueFactory(c -> new SimpleStringProperty(
                Contexto.SERVICO_PERFIL_VISITANTE.usuarioOuNome(c.getValue().getVisitante())));
        colIngTipo.setCellValueFactory(c -> new SimpleStringProperty(
                Formatos.tipoIngresso(c.getValue().getTipoIngresso())));
        colIngCompra.setCellValueFactory(c -> new SimpleStringProperty(
                Formatos.data(c.getValue().getDataCompra())));
        colIngValidade.setCellValueFactory(c -> new SimpleStringProperty(
                Formatos.data(c.getValue().getDataValidade())));
        colIngValor.setCellValueFactory(c -> new SimpleStringProperty(
                Formatos.moeda(c.getValue().getValor())));
        colIngPagamento.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFormaPagamento() == null ? "-" : c.getValue().getFormaPagamento()));
        tabelaIngressos.setPlaceholder(new Label("Nenhum ingresso encontrado."));
        txtBuscaIngresso.textProperty().addListener((obs, antigo, novo) -> atualizarIngressos());
        atualizarIngressos();
    }

    private void atualizarIngressos() {
        List<Ingresso> ingressos = new ArrayList<>(
                Contexto.SERVICO_INGRESSO.listarPorTrecho(txtBuscaIngresso.getText()));
        ingressos.sort(Comparator.comparing(Ingresso::getDataCompra).reversed());
        tabelaIngressos.setItems(FXCollections.observableArrayList(ingressos));
        lblTotalIngressos.setText(ingressos.size() + " ingresso(s)");
    }

    @FXML
    private void onConsultarData() {
        LocalDate data = dpDataEmissao.getValue();
        if (data == null) {
            lblQtdData.setText("Escolha uma data.");
            return;
        }
        lblQtdData.setText(Contexto.SERVICO_INGRESSO.consultarQuantidadePorData(data)
                + " ingresso(s) emitido(s) em " + Formatos.data(data));
    }
}
