package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.controller.controles.CarregadorFeriados;
import com.ufrpe.parkadventure.controller.controles.ColunasRelatorio;
import com.ufrpe.parkadventure.repository.RelatorioGerencial;
import com.ufrpe.parkadventure.repository.RelatorioGerencial.RelatorioIngressos;
import com.ufrpe.parkadventure.repository.RelatorioGerencial.VendaDetalhada;
import com.ufrpe.parkadventure.view.Aviso;
import com.ufrpe.parkadventure.view.Formatos;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

// Aba "Ingresso": vendas de um mês, com os dias de feriado destacados (REQ14)
public class RelatorioIngressoController {
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    @FXML private ComboBox<Month> cbMes;
    @FXML private ComboBox<Integer> cbAno;
    @FXML private Label lblQuantidade, lblDiarias, lblPasses, lblReceita, lblReceitaFeriados, lblTicketMedio, lblStatus;

    @FXML private TableView<VendaDetalhada> tabela;
    @FXML private TableColumn<VendaDetalhada, String> colData, colVisitante, colTipo, colPagamento, colFeriado;
    @FXML private TableColumn<VendaDetalhada, Number> colValor;

    private final RelatorioGerencial relatorio = new RelatorioGerencial();

    @FXML
    private void initialize() {
        ColunasRelatorio.texto(colData, v -> Formatos.data(v.data()));
        ColunasRelatorio.texto(colVisitante, VendaDetalhada::visitante);
        ColunasRelatorio.texto(colTipo, v -> Formatos.tipoIngresso(v.tipo()));
        ColunasRelatorio.moeda(colValor, VendaDetalhada::valor);
        ColunasRelatorio.texto(colPagamento, v -> v.formaPagamento() == null || v.formaPagamento().isBlank()
                ? "—" : v.formaPagamento());
        ColunasRelatorio.texto(colFeriado, v -> v.ehFeriado() ? v.feriado() : "");
        tabela.setPlaceholder(new Label("Nenhum ingresso vendido neste mês."));

        // linhas de feriado ganham a classe CSS "linha-feriado" (veja relatorios.css)
        tabela.setRowFactory(t -> new TableRow<>() {
            @Override
            protected void updateItem(VendaDetalhada venda, boolean vazio) {
                super.updateItem(venda, vazio);
                getStyleClass().remove("linha-feriado");
                if (!vazio && venda != null && venda.ehFeriado()) {
                    getStyleClass().add("linha-feriado");
                }
            }
        });

        cbMes.setItems(FXCollections.observableArrayList(Month.values()));
        cbMes.setConverter(new StringConverter<>() {
            @Override public String toString(Month mes) {
                if (mes == null) return "";
                String nome = mes.getDisplayName(TextStyle.FULL, PT_BR);
                return Character.toUpperCase(nome.charAt(0)) + nome.substring(1);
            }
            @Override public Month fromString(String texto) { return null; }
        });

        int anoAtual = LocalDate.now().getYear();
        int primeiro = relatorio.primeiroAnoComVenda(anoAtual);
        cbAno.setItems(FXCollections.observableArrayList(IntStream.rangeClosed(primeiro, anoAtual + 1).boxed().toList()));

        cbMes.setValue(LocalDate.now().getMonth());
        cbAno.setValue(anoAtual);
        // só depois dos valores iniciais, para não recarregar à toa
        cbMes.valueProperty().addListener((obs, a, n) -> atualizar());
        cbAno.valueProperty().addListener((obs, a, n) -> atualizar());
    }

    public void atualizar() {
        if (cbMes.getValue() == null || cbAno.getValue() == null) {
            return;
        }
        YearMonth periodo = YearMonth.of(cbAno.getValue(), cbMes.getValue());

        // mostra já os números; os feriados chegam depois e só acrescentam o destaque
        preencher(relatorio.relatorioIngressos(periodo, Map.of()));
        Aviso.info(lblStatus, "Consultando feriados na BrasilAPI…");

        CarregadorFeriados.carregar(periodo.getYear(), feriados -> {
            if (periodo.equals(periodoSelecionado())) {       // o usuário pode ter trocado o mês enquanto isso
                preencher(relatorio.relatorioIngressos(periodo, feriados));
                Aviso.limpar(lblStatus);
            }
        }, erro -> {
            if (periodo.equals(periodoSelecionado())) {
                Aviso.erro(lblStatus, "Não foi possível consultar a BrasilAPI (sem internet?). "
                        + "Os valores estão corretos, mas os feriados não foram destacados.");
            }
        });
    }

    private YearMonth periodoSelecionado() {
        return YearMonth.of(cbAno.getValue(), cbMes.getValue());
    }

    private void preencher(RelatorioIngressos r) {
        tabela.setItems(FXCollections.observableArrayList(r.vendas()));
        lblQuantidade.setText(String.valueOf(r.quantidade()));
        lblDiarias.setText(String.valueOf(r.diarias()));
        lblPasses.setText(String.valueOf(r.passesAnuais()));
        lblReceita.setText(Formatos.moeda(r.receitaTotal()));
        lblReceitaFeriados.setText(Formatos.moeda(r.receitaFeriados()) + "  (" + r.vendasEmFeriados() + " vendas)");
        lblTicketMedio.setText(Formatos.moeda(r.ticketMedio()));
    }
}
