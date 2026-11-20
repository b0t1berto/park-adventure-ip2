package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.controller.controles.CarregadorFeriados;
import com.ufrpe.parkadventure.controller.controles.ColunasRelatorio;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.repository.RelatorioGerencial;
import com.ufrpe.parkadventure.repository.RelatorioGerencial.ResumoGeral;
import com.ufrpe.parkadventure.repository.RepositorioEscalaTrabalho;
import com.ufrpe.parkadventure.view.Aviso;
import com.ufrpe.parkadventure.view.Formatos;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Aba "Geral": números principais do parque + operadores escalados nos próximos feriados (REQ15)
public class RelatorioGeralController {
    private static final int QTD_FERIADOS = 6;

    @FXML private Label lblVisitantes, lblIngressos, lblReceita, lblIngressosHoje, lblReceitaHoje;
    @FXML private Label lblAtracoes, lblSemOperador, lblLotadas, lblOcupacao, lblReservasHoje, lblStatus;
    @FXML private ProgressBar pbOcupacao;

    @FXML private TableView<LinhaFeriado> tabelaFeriados;
    @FXML private TableColumn<LinhaFeriado, String> colFeriado, colDia, colOperadores;
    @FXML private TableColumn<LinhaFeriado, String> colData;

    public record LinhaFeriado(LocalDate data, String nome, String operadores) { }

    private final RelatorioGerencial relatorio = new RelatorioGerencial();

    @FXML
    private void initialize() {
        ColunasRelatorio.texto(colFeriado, LinhaFeriado::nome);
        ColunasRelatorio.texto(colData, l -> Formatos.data(l.data()));
        ColunasRelatorio.texto(colDia, l -> Formatos.diaSemana(l.data()));
        ColunasRelatorio.texto(colOperadores, LinhaFeriado::operadores);
        tabelaFeriados.setPlaceholder(new Label("Sem feriados para mostrar."));
    }

    // Chamado pelo RelatoriosController sempre que a aba é aberta
    public void atualizar() {
        LocalDate hoje = LocalDate.now();
        ResumoGeral r = relatorio.resumoGeral(hoje);

        lblVisitantes.setText(String.valueOf(r.visitantes()));
        lblIngressos.setText(String.valueOf(r.ingressosEmitidos()));
        lblReceita.setText(Formatos.moeda(r.receitaTotal()));
        lblIngressosHoje.setText(String.valueOf(r.ingressosHoje()));
        lblReceitaHoje.setText(Formatos.moeda(r.receitaHoje()));
        lblAtracoes.setText(String.valueOf(r.atracoes()));
        lblSemOperador.setText(String.valueOf(r.atracoesSemOperador()));
        lblLotadas.setText(String.valueOf(r.atracoesLotadas()));
        lblOcupacao.setText(r.ocupacaoAtual() + " / " + r.capacidadeTotal() + " pessoas");
        pbOcupacao.setProgress(r.fracaoOcupacao());
        lblReservasHoje.setText(String.valueOf(r.reservasAtivasHoje()));

        carregarFeriados(hoje);
    }

    private void carregarFeriados(LocalDate hoje) {
        Aviso.info(lblStatus, "Consultando feriados na BrasilAPI…");
        tabelaFeriados.setItems(FXCollections.observableArrayList());
        CarregadorFeriados.carregar(hoje.getYear(), feriadosDoAno -> {
            // no fim do ano os próximos feriados já são do ano seguinte (se a API responder)
            CarregadorFeriados.carregar(hoje.getYear() + 1,
                    proximoAno -> montarFeriados(hoje, feriadosDoAno, proximoAno),
                    erro -> montarFeriados(hoje, feriadosDoAno, Map.of()));
        }, erro -> Aviso.erro(lblStatus,
                "Não foi possível consultar a BrasilAPI (sem internet?). Os números acima continuam corretos."));
    }

    private List<LinhaFeriado> linhasFeriadosAtuais = List.of();

    private void montarFeriados(LocalDate hoje, Map<LocalDate, String> ano, Map<LocalDate, String> proximoAno) {
        Map<LocalDate, String> todos = new java.util.TreeMap<>(ano);
        todos.putAll(proximoAno);

        linhasFeriadosAtuais = new ArrayList<>();
        for (Map.Entry<LocalDate, String> f : todos.entrySet()) {
            if (f.getKey().isBefore(hoje)) continue;
            List<Operador> escalados = RepositorioEscalaTrabalho.getInstancia().operadoresDoDia(f.getKey());
            String quem = escalados.isEmpty() ? "Sem operador escalado"
                    : escalados.stream().map(Operador::getNome).collect(Collectors.joining(", "));
            linhasFeriadosAtuais.add(new LinhaFeriado(f.getKey(), f.getValue(), quem));
            if (linhasFeriadosAtuais.size() == QTD_FERIADOS) break;
        }
        tabelaFeriados.setItems(FXCollections.observableArrayList(linhasFeriadosAtuais));
        Aviso.limpar(lblStatus);
    }

    @FXML
    private void onExportarCsv() {
        if (linhasFeriadosAtuais.isEmpty()) {
            Aviso.erro(lblStatus, "Não há dados para exportar.");
            return;
        }
        FileChooser escolha = new FileChooser();
        escolha.setTitle("Salvar relatório geral — feriados");
        escolha.setInitialFileName("relatorio-geral-feriados.csv");
        escolha.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivo CSV (*.csv)", "*.csv"));
        File arquivo = escolha.showSaveDialog(tabelaFeriados.getScene().getWindow());
        if (arquivo == null) {
            return;   // cancelou
        }
        try {
            String conteudo = "\uFEFF" + relatorio.montarCsvGeralFeriados(linhasFeriadosAtuais.toArray());
            Files.writeString(arquivo.toPath(), conteudo, StandardCharsets.UTF_8);
            Aviso.sucesso(lblStatus, "Relatório salvo em: " + arquivo.getAbsolutePath());
        } catch (Exception e) {
            Aviso.erro(lblStatus, "Não foi possível salvar o arquivo: " + e.getMessage());
        }
    }
}
