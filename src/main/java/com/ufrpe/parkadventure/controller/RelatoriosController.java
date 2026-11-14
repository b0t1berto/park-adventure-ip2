package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.controller.controles.Tarefas;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.repository.Relatorio;
import com.ufrpe.parkadventure.repository.Relatorio.LinhaVenda;
import com.ufrpe.parkadventure.repository.Relatorio.ResumoReceita;
import com.ufrpe.parkadventure.view.Aviso;
import com.ufrpe.parkadventure.view.Formatos;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

// Tela do administrador: visão geral do parque (atrações, visitantes, receita) e receita de ingressos por mês
public class RelatoriosController {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private final Relatorio relatorio = new Relatorio();

    // ---- visão geral
    @FXML private Label lblVisitantes, lblIngressos, lblReceitaTotal, lblAtracoes, lblSemOperador, lblResultado;
    @FXML private TableView<Atracao> tabelaOcupacao;
    @FXML private TableColumn<Atracao, String> colAtracao, colOcupacao, colFila, colSituacao;

    // ---- receita por mês
    @FXML private ComboBox<Month> cbMes;
    @FXML private TextField txtAno;
    @FXML private Button btnGerar;
    @FXML private Label lblQtdMes, lblReceitaMes, lblReceitaFeriados, lblStatusReceita;
    @FXML private TableView<LinhaVenda> tabelaVendas;
    @FXML private TableColumn<LinhaVenda, String> colData, colValor, colFeriado;

    // feriados só existem se a consulta à BrasilAPI funcionou
    private record ResultadoReceita(ResumoReceita resumo, boolean comFeriados) { }

    @FXML
    private void initialize() {
        configurarVisaoGeral();
        configurarReceita();
    }

    // =============================================
    // || VISÃO GERAL
    // =============================================
    private void configurarVisaoGeral() {
        colAtracao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colOcupacao.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getOcupacaoAtual() + "/" + c.getValue().getCapacidadeMaxima()));
        colFila.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().getFilaVirtual().tamanhoTotal())));
        colSituacao.setCellValueFactory(c -> new SimpleStringProperty(situacao(c.getValue())));
        tabelaOcupacao.setPlaceholder(new Label("Nenhuma atração cadastrada."));

        List<Atracao> atracoes = new ArrayList<>(RepositorioAtracaoController.listarTodas());
        atracoes.sort(Comparator.comparingInt(Atracao::getOcupacaoAtual).reversed());

        double receita = 0;
        for (Ingresso ingresso : RepositorioIngressoController.listarTodos()) {
            receita += ingresso.getValor();
        }
        int semOperador = 0;
        for (Atracao atracao : atracoes) {
            if (!atracao.temOperador()) {
                semOperador++;
            }
        }

        lblVisitantes.setText(String.valueOf(RepositorioVisitanteController.listarTodos().size()));
        lblIngressos.setText(String.valueOf(RepositorioIngressoController.listarTodos().size()));
        lblReceitaTotal.setText(Formatos.moeda(receita));
        lblAtracoes.setText(String.valueOf(atracoes.size()));
        lblSemOperador.setText(String.valueOf(semOperador));
        tabelaOcupacao.setItems(FXCollections.observableArrayList(atracoes));
    }

    private String situacao(Atracao atracao) {
        if (!atracao.temOperador()) {
            return "Fechada (sem operador)";
        }
        return atracao.estaLotada() ? "Lotada" : "Aberta";
    }

    @FXML
    private void onExportarCsv() {
        String arquivo = "atracoes.csv";
        try {
            relatorio.salvarCsvAtracoes(RepositorioAtracaoController.listarTodas(), arquivo);
            Aviso.sucesso(lblResultado, "Arquivo gerado em: " + Path.of(arquivo).toAbsolutePath());
        } catch (IOException e) {
            Aviso.erro(lblResultado, "Não foi possível gerar o CSV: " + e.getMessage());
        }
    }

    // =============================================
    // || RECEITA POR MÊS
    // =============================================
    private void configurarReceita() {
        cbMes.setItems(FXCollections.observableArrayList(Month.values()));
        cbMes.setConverter(new StringConverter<>() {
            @Override public String toString(Month mes) {
                if (mes == null) return "";
                String nome = mes.getDisplayName(TextStyle.FULL, PT_BR);
                return Character.toUpperCase(nome.charAt(0)) + nome.substring(1);
            }
            @Override public Month fromString(String texto) {
                return null;
            }
        });
        LocalDate hoje = LocalDate.now();
        cbMes.setValue(hoje.getMonth());
        txtAno.setText(String.valueOf(hoje.getYear()));

        colData.setCellValueFactory(c -> new SimpleStringProperty(Formatos.data(c.getValue().data())));
        colValor.setCellValueFactory(c -> new SimpleStringProperty(Formatos.moeda(c.getValue().valor())));
        colFeriado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().feriado() ? "Sim" : "-"));
        tabelaVendas.setPlaceholder(new Label("Escolha o mês e clique em \"Gerar relatório\"."));
    }

    @FXML
    private void onGerarReceita() {
        Month mes = cbMes.getValue();
        int ano;
        try {
            ano = Integer.parseInt(txtAno.getText().trim());
        } catch (NumberFormatException e) {
            ano = -1;
        }
        if (mes == null || ano < 2000 || ano > 2100) {
            Aviso.erro(lblStatusReceita, "Escolha o mês e informe um ano válido (por exemplo, " + LocalDate.now().getYear() + ").");
            return;
        }

        final int anoEscolhido = ano;
        final List<Ingresso> vendas = new ArrayList<>(RepositorioIngressoController.listarTodos());
        btnGerar.setDisable(true);
        Aviso.info(lblStatusReceita, "Consultando os feriados e somando as vendas...");

        Task<ResultadoReceita> tarefa = new Task<>() {
            @Override
            protected ResultadoReceita call() {
                try {
                    return new ResultadoReceita(relatorio.gerarRelatorioIngressos(vendas, mes, anoEscolhido), true);
                } catch (IOException | InterruptedException e) {
                    // sem internet: o relatório sai mesmo assim, só sem o destaque dos feriados
                    return new ResultadoReceita(somarSemFeriados(vendas, mes, anoEscolhido), false);
                }
            }
        };
        tarefa.setOnSucceeded(evento -> {
            mostrarReceita(tarefa.getValue(), mes, anoEscolhido);
            btnGerar.setDisable(false);
        });
        tarefa.setOnFailed(evento -> {
            Aviso.erro(lblStatusReceita, "Não foi possível gerar o relatório.");
            btnGerar.setDisable(false);
        });
        Tarefas.executar(tarefa);
    }

    private ResumoReceita somarSemFeriados(List<Ingresso> vendas, Month mes, int ano) {
        List<LinhaVenda> linhas = new ArrayList<>();
        double total = 0;
        for (Ingresso venda : vendas) {
            LocalDate data = venda.getDataCompra();
            if (data.getMonth() == mes && data.getYear() == ano) {
                total += venda.getValor();
                linhas.add(new LinhaVenda(data, venda.getValor(), false));
            }
        }
        return new ResumoReceita(linhas, total, 0);
    }

    private void mostrarReceita(ResultadoReceita resultado, Month mes, int ano) {
        ResumoReceita resumo = resultado.resumo();
        List<LinhaVenda> linhas = new ArrayList<>(resumo.vendas());
        linhas.sort(Comparator.comparing(LinhaVenda::data));

        lblQtdMes.setText(String.valueOf(linhas.size()));
        lblReceitaMes.setText(Formatos.moeda(resumo.receitaTotal()));
        lblReceitaFeriados.setText(resultado.comFeriados() ? Formatos.moeda(resumo.receitaFeriados()) : "indisponível");
        tabelaVendas.setItems(FXCollections.observableArrayList(linhas));
        tabelaVendas.setPlaceholder(new Label("Nenhuma venda neste mês."));

        String periodo = Formatos.mesAno(YearMonth.of(ano, mes));
        if (resultado.comFeriados()) {
            Aviso.sucesso(lblStatusReceita, "Relatório de " + periodo + " gerado.");
        } else {
            Aviso.info(lblStatusReceita, "Relatório de " + periodo + " gerado sem os feriados: "
                    + "não foi possível consultar a BrasilAPI (confira a internet).");
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
