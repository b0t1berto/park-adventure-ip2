package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.controller.controles.ColunasRelatorio;
import com.ufrpe.parkadventure.repository.RelatorioGerencial;
import com.ufrpe.parkadventure.repository.RelatorioGerencial.LinhaAtracao;
import com.ufrpe.parkadventure.view.Aviso;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

// Aba "Atração": ranking das mais acessadas no período (REQ13) + situação atual, com exportação em CSV
public class RelatorioAtracaoController {

    @FXML private DatePicker dpDe, dpAte;
    @FXML private Label lblMaisAcessada, lblTotalUsadas, lblStatus;
    @FXML private BarChart<String, Number> grafico;

    @FXML private TableView<LinhaAtracao> tabela;
    @FXML private TableColumn<LinhaAtracao, String> colPosicao, colNome, colTipo, colOperador, colSituacao;
    @FXML private TableColumn<LinhaAtracao, Number> colCapacidade, colOcupacao, colOcupacaoPct,
            colReservas, colUsadas, colNoShow, colCanceladas;

    private final RelatorioGerencial relatorio = new RelatorioGerencial();
    private List<LinhaAtracao> linhasAtuais = List.of();

    @FXML
    private void initialize() {
        // a posição é o número da linha no ranking
        colPosicao.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean vazio) {
                super.updateItem(item, vazio);
                setText(vazio ? null : String.valueOf(getIndex() + 1));
            }
        });
        ColunasRelatorio.texto(colPosicao, l -> "");
        ColunasRelatorio.texto(colNome, LinhaAtracao::nome);
        ColunasRelatorio.texto(colTipo, LinhaAtracao::tipo);
        ColunasRelatorio.texto(colOperador, LinhaAtracao::operador);
        ColunasRelatorio.inteiro(colCapacidade, LinhaAtracao::capacidade);
        ColunasRelatorio.inteiro(colOcupacao, LinhaAtracao::ocupacao);
        ColunasRelatorio.percentual(colOcupacaoPct, LinhaAtracao::percentualOcupacao);
        ColunasRelatorio.inteiro(colReservas, LinhaAtracao::reservas);
        ColunasRelatorio.inteiro(colUsadas, LinhaAtracao::usadas);
        ColunasRelatorio.inteiro(colNoShow, LinhaAtracao::noShows);
        ColunasRelatorio.inteiro(colCanceladas, LinhaAtracao::canceladas);
        ColunasRelatorio.texto(colSituacao, LinhaAtracao::situacao);
        pintarSituacao();

        grafico.setAnimated(false);
        tabela.setPlaceholder(new Label("Nenhuma atração cadastrada."));

        // período padrão: o mês atual inteiro
        YearMonth mes = YearMonth.now();
        dpDe.setValue(mes.atDay(1));
        dpAte.setValue(mes.atEndOfMonth());
        dpDe.valueProperty().addListener((obs, a, n) -> atualizar());
        dpAte.valueProperty().addListener((obs, a, n) -> atualizar());
    }

    public void atualizar() {
        LocalDate de = dpDe.getValue();
        LocalDate ate = dpAte.getValue();
        if (de == null || ate == null) {
            Aviso.erro(lblStatus, "Escolha a data inicial e a final.");
            return;
        }
        if (de.isAfter(ate)) {
            Aviso.erro(lblStatus, "A data inicial não pode ser depois da data final.");
            return;
        }

        linhasAtuais = relatorio.desempenhoAtracoes(de, ate);
        tabela.setItems(FXCollections.observableArrayList(linhasAtuais));
        tabela.refresh();   // renumera a coluna de posição

        int totalUsadas = linhasAtuais.stream().mapToInt(LinhaAtracao::usadas).sum();
        lblTotalUsadas.setText(String.valueOf(totalUsadas));
        // sem nenhum acesso no período não existe "mais acessada"
        lblMaisAcessada.setText(totalUsadas == 0 ? "—" : linhasAtuais.get(0).nome());

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Acessos pelo fura-fila");
        for (LinhaAtracao l : linhasAtuais) {
            serie.getData().add(new XYChart.Data<>(l.nome(), l.usadas()));
        }
        grafico.getData().setAll(serie);

        Aviso.info(lblStatus, "Acessos = reservas de fura-fila usadas no período. "
                + "A entrada na fila comum não fica registrada; ela aparece só como ocupação atual.");
    }

    @FXML
    private void onExportarCsv() {
        if (linhasAtuais.isEmpty()) {
            Aviso.erro(lblStatus, "Não há dados para exportar.");
            return;
        }
        FileChooser escolha = new FileChooser();
        escolha.setTitle("Salvar relatório de atrações");
        escolha.setInitialFileName("relatorio-atracoes.csv");
        escolha.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivo CSV (*.csv)", "*.csv"));
        File arquivo = escolha.showSaveDialog(tabela.getScene().getWindow());
        if (arquivo == null) {
            return;   // cancelou
        }
        try {
            // BOM no começo para o Excel abrir os acentos corretamente
            String conteudo = "\uFEFF" + relatorio.montarCsvAtracoes(linhasAtuais);
            Files.writeString(arquivo.toPath(), conteudo, StandardCharsets.UTF_8);
            Aviso.sucesso(lblStatus, "Relatório salvo em: " + arquivo.getAbsolutePath());
        } catch (Exception e) {
            Aviso.erro(lblStatus, "Não foi possível salvar o arquivo: " + e.getMessage());
        }
    }

    // verde = aberta, amarelo = lotada, vermelho = sem operador (classes do estilo.css)
    private void pintarSituacao() {
        colSituacao.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean vazio) {
                super.updateItem(item, vazio);
                getStyleClass().removeAll("sit-ok", "sit-alerta", "sit-bloqueio");
                if (vazio || item == null) {
                    setText(null);
                    return;
                }
                setText(item);
                switch (item) {
                    case "Aberta" -> getStyleClass().add("sit-ok");
                    case "Lotada" -> getStyleClass().add("sit-alerta");
                    default -> getStyleClass().add("sit-bloqueio");
                }
            }
        });
    }
}
