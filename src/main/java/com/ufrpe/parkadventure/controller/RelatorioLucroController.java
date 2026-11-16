package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.controller.controles.ColunasRelatorio;
import com.ufrpe.parkadventure.repository.RelatorioGerencial;
import com.ufrpe.parkadventure.repository.RelatorioGerencial.LinhaLucro;
import com.ufrpe.parkadventure.repository.RelatorioGerencial.RelatorioLucro;
import com.ufrpe.parkadventure.view.Aviso;
import com.ufrpe.parkadventure.view.Formatos;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.stream.IntStream;

// Aba "Lucro": receita - custos, mês a mês. O sistema não guarda custos; eles são informados nos campos da tela.
public class RelatorioLucroController {
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    @FXML private ComboBox<Integer> cbAno;
    @FXML private TextField txtCustoOperador, txtCustoFixo;
    @FXML private Label lblReceita, lblCusto, lblLucro, lblMargem, lblStatus;
    @FXML private BarChart<String, Number> grafico;

    @FXML private TableView<LinhaLucro> tabela;
    @FXML private TableColumn<LinhaLucro, String> colMes;
    @FXML private TableColumn<LinhaLucro, Number> colIngressos, colReceita, colTurnos, colCusto, colLucro, colMargem;

    private final RelatorioGerencial relatorio = new RelatorioGerencial();

    @FXML
    private void initialize() {
        ColunasRelatorio.texto(colMes, l -> nomeDoMes(l.mes()));
        ColunasRelatorio.inteiro(colIngressos, LinhaLucro::ingressos);
        ColunasRelatorio.moeda(colReceita, LinhaLucro::receita);
        ColunasRelatorio.inteiro(colTurnos, LinhaLucro::turnos);
        ColunasRelatorio.moeda(colCusto, LinhaLucro::custo);
        ColunasRelatorio.moeda(colLucro, LinhaLucro::lucro);
        ColunasRelatorio.percentual(colMargem, LinhaLucro::margem);
        pintarLucro(colLucro);

        grafico.setAnimated(false);   // sem animação, para não "piscar" ao recalcular
        tabela.setPlaceholder(new Label("Sem dados para o ano escolhido."));

        int anoAtual = LocalDate.now().getYear();
        int primeiro = relatorio.primeiroAnoComVenda(anoAtual);
        cbAno.setItems(FXCollections.observableArrayList(IntStream.rangeClosed(primeiro, anoAtual + 1).boxed().toList()));
        cbAno.setValue(anoAtual);
        cbAno.valueProperty().addListener((obs, a, n) -> atualizar());

        txtCustoOperador.setText("0");
        txtCustoFixo.setText("0");
    }

    // Botão "Calcular" (ou Enter nos campos de custo)
    @FXML
    private void onCalcular() {
        atualizar();
    }

    public void atualizar() {
        if (cbAno.getValue() == null) {
            return;
        }
        double custoOperador;
        double custoFixo;
        try {
            custoOperador = lerValor(txtCustoOperador.getText());
            custoFixo = lerValor(txtCustoFixo.getText());
        } catch (IllegalArgumentException e) {
            Aviso.erro(lblStatus, "Digite um valor válido (ex.: 150 ou 1.500,50) nos campos de custo.");
            return;
        }

        RelatorioLucro r = relatorio.relatorioLucro(cbAno.getValue(), custoOperador, custoFixo, YearMonth.now());
        tabela.setItems(FXCollections.observableArrayList(r.meses()));

        lblReceita.setText(Formatos.moeda(r.receitaTotal()));
        lblCusto.setText(Formatos.moeda(r.custoTotal()));
        lblLucro.setText(Formatos.moeda(r.lucroTotal()));
        lblLucro.getStyleClass().removeAll("sit-ok", "sit-bloqueio");
        lblLucro.getStyleClass().add(r.lucroTotal() < 0 ? "sit-bloqueio" : "sit-ok");
        lblMargem.setText(String.format(PT_BR, "%.1f%%", r.margem()));

        montarGrafico(r);

        if (custoOperador == 0 && custoFixo == 0) {
            Aviso.info(lblStatus, "Custos zerados: o lucro é igual à receita. Informe os custos para um cálculo real.");
        } else {
            Aviso.limpar(lblStatus);
        }
    }

    private void montarGrafico(RelatorioLucro r) {
        XYChart.Series<String, Number> receita = new XYChart.Series<>();
        receita.setName("Receita");
        XYChart.Series<String, Number> lucro = new XYChart.Series<>();
        lucro.setName("Lucro");
        for (LinhaLucro l : r.meses()) {
            String mes = abreviado(l.mes());
            receita.getData().add(new XYChart.Data<>(mes, l.receita()));
            lucro.getData().add(new XYChart.Data<>(mes, l.lucro()));
        }
        grafico.getData().setAll(receita, lucro);
    }

    // Aceita "1500", "1500.50" e "1.500,50"; vazio vale 0; negativo é inválido
    private double lerValor(String texto) {
        String limpo = texto == null ? "" : texto.replace("R$", "").trim();
        if (limpo.isEmpty()) {
            return 0;
        }
        if (limpo.contains(",")) {
            limpo = limpo.replace(".", "").replace(",", ".");
        }
        double valor = Double.parseDouble(limpo);   // NumberFormatException é um IllegalArgumentException
        if (valor < 0 || Double.isNaN(valor) || Double.isInfinite(valor)) {
            throw new IllegalArgumentException("valor inválido");
        }
        return valor;
    }

    // coluna "Lucro": vermelho quando negativo, verde quando positivo (classes do estilo.css)
    private void pintarLucro(TableColumn<LinhaLucro, Number> coluna) {
        coluna.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean vazio) {
                super.updateItem(item, vazio);
                getStyleClass().removeAll("sit-ok", "sit-bloqueio");
                if (vazio || item == null) {
                    setText(null);
                    return;
                }
                setText(Formatos.moeda(item.doubleValue()));
                if (item.doubleValue() < 0) getStyleClass().add("sit-bloqueio");
                else if (item.doubleValue() > 0) getStyleClass().add("sit-ok");
            }
        });
    }

    private String nomeDoMes(YearMonth mes) {
        String nome = mes.getMonth().getDisplayName(TextStyle.FULL, PT_BR);
        return Character.toUpperCase(nome.charAt(0)) + nome.substring(1);
    }

    private String abreviado(YearMonth mes) {
        String nome = mes.getMonth().getDisplayName(TextStyle.SHORT, PT_BR);
        return Character.toUpperCase(nome.charAt(0)) + nome.substring(1);
    }
}
