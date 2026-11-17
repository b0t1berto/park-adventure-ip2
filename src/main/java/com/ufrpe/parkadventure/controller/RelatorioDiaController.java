package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.app.Contexto;
import com.ufrpe.parkadventure.controller.controles.CarregadorFeriados;
import com.ufrpe.parkadventure.controller.controles.ColunasRelatorio;
import com.ufrpe.parkadventure.repository.RelatorioGerencial;
import com.ufrpe.parkadventure.repository.RelatorioGerencial.LinhaDia;
import com.ufrpe.parkadventure.repository.RelatorioGerencial.RelatorioDia;
import com.ufrpe.parkadventure.view.Aviso;
import com.ufrpe.parkadventure.view.Formatos;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;

// Aba "Dia": vendas do dia, visitas previstas, capacidade máxima do parque (soma das atrações x 5)
// e quantas vagas ainda restam. Com a capacidade atingida o dia aparece como "Esgotado".
public class RelatorioDiaController {

    @FXML private DatePicker dpDia;
    @FXML private Label lblDiaSemana, lblFeriado, lblVendidos, lblReceita, lblVisitas, lblCapacidade,
            lblVagas, lblReservas, lblOperadores, lblOcupacao, lblSituacao;
    @FXML private ProgressBar pbOcupacao;

    @FXML private TableView<LinhaDia> tabela;
    @FXML private TableColumn<LinhaDia, String> colVisitante, colTipo, colRelacao, colCompra, colVisita, colPagamento;
    @FXML private TableColumn<LinhaDia, Number> colValor;

    private final RelatorioGerencial relatorio = new RelatorioGerencial();

    @FXML
    private void initialize() {
        ColunasRelatorio.texto(colVisitante, LinhaDia::visitante);
        ColunasRelatorio.texto(colTipo, l -> Formatos.tipoIngresso(l.tipo()));
        ColunasRelatorio.texto(colRelacao, LinhaDia::relacao);
        ColunasRelatorio.texto(colCompra, l -> Formatos.data(l.compra()));
        ColunasRelatorio.texto(colVisita, l -> Formatos.data(l.validade()));
        ColunasRelatorio.moeda(colValor, LinhaDia::valor);
        ColunasRelatorio.texto(colPagamento, l -> l.formaPagamento() == null || l.formaPagamento().isBlank()
                ? "—" : l.formaPagamento());
        tabela.setPlaceholder(new Label("Nenhum ingresso comprado ou com visita neste dia."));

        dpDia.setValue(LocalDate.now());
        dpDia.valueProperty().addListener((obs, antiga, nova) -> atualizar());
    }

    // Chamado pelo RelatoriosController sempre que a aba é aberta (e pelo botão "Atualizar")
    public void atualizar() {
        LocalDate dia = dpDia.getValue();
        if (dia == null) {
            Aviso.erro(lblSituacao, "Escolha uma data.");
            return;
        }

        RelatorioDia r = relatorio.relatorioDia(dia, Contexto.SERVICO_CAPACIDADE.capacidadeMaximaDiaria());

        lblDiaSemana.setText(Formatos.diaSemana(dia) + ", " + Formatos.data(dia));
        lblVendidos.setText(String.valueOf(r.vendidosNoDia()));
        lblReceita.setText(Formatos.moeda(r.receitaNoDia()));
        lblVisitas.setText(String.valueOf(r.visitasPrevistas()));
        lblCapacidade.setText(String.valueOf(r.capacidadeMaxima()));
        lblVagas.setText(String.valueOf(r.vagasRestantes()));
        lblReservas.setText(String.valueOf(r.reservasFuraFila()));
        lblOperadores.setText(String.valueOf(r.operadoresEscalados()));
        lblOcupacao.setText(r.visitasPrevistas() + " / " + r.capacidadeMaxima() + " visitantes");
        pbOcupacao.setProgress(r.fracaoOcupacao());
        tabela.setItems(FXCollections.observableArrayList(r.ingressos()));

        if (r.esgotado()) {
            Aviso.erro(lblSituacao, "Esgotado: capacidade máxima atingida, vendas encerradas para este dia.");
        } else {
            Aviso.sucesso(lblSituacao, "Vendas abertas: " + r.vagasRestantes() + " vaga(s) restante(s).");
        }

        carregarFeriado(dia);
    }

    private void carregarFeriado(LocalDate dia) {
        lblFeriado.setText("");
        CarregadorFeriados.carregar(dia.getYear(), feriados -> {
            if (dia.equals(dpDia.getValue())) {       // o usuário pode ter trocado a data enquanto isso
                String nome = feriados.get(dia);
                lblFeriado.setText(nome == null ? "" : "Feriado nacional: " + nome);
            }
        }, erro -> { /* sem internet: o relatório sai igual, só sem o nome do feriado */ });
    }

    @FXML
    private void onHoje() {
        dpDia.setValue(LocalDate.now());
    }

    @FXML
    private void onDiaAnterior() {
        if (dpDia.getValue() != null) dpDia.setValue(dpDia.getValue().minusDays(1));
    }

    @FXML
    private void onProximoDia() {
        if (dpDia.getValue() != null) dpDia.setValue(dpDia.getValue().plusDays(1));
    }
}
