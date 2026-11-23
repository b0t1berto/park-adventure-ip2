package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.app.Contexto;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.repository.RepositorioEscalaTrabalho.Turno;
import com.ufrpe.parkadventure.view.controles.Tarefas;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Escala do operador logado: ele só vê os PRÓPRIOS turnos (a escala geral é do administrador)
public class MinhaEscalaController {

    @FXML private Label lblOperador, lblProximo;
    @FXML private TableView<LinhaEscala> tabela;
    @FXML private TableColumn<LinhaEscala, String> colData, colDia, colPeriodo, colAtracao, colObservacao;

    private record LinhaEscala(String data, String dia, String periodo, String atracao, String observacao) { }

    private Operador operador;

    @FXML
    private void initialize() {
        colData.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().data()));
        colDia.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().dia()));
        colPeriodo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().periodo()));
        colAtracao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().atracao()));
        colObservacao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().observacao()));
        tabela.setPlaceholder(new Label("Você não tem turnos escalados."));

        operador = Contexto.SESSAO.getOperadorAtual();
        if (operador == null) {
            lblOperador.setText("Nenhum operador logado.");
            lblProximo.setText("");
            return;
        }
        lblOperador.setText("Escala de " + operador.getNome());
        atualizarTabela();
        carregarFeriadosEmSegundoPlano();
    }

    private void atualizarTabela() {
        Atracao daAtracao = Contexto.SERVICO_ATRACAO.buscarAtracaoPorOperador(operador);
        String nomeDaAtracao = daAtracao == null ? "—" : daAtracao.getNome();
        LocalDate hoje = LocalDate.now();

        List<LinhaEscala> linhas = new ArrayList<>();
        Turno proximo = null;
        for (Turno turno : Contexto.SERVICO_ESCALA.listarOrdenado()) {
            // só os turnos DESTE operador, de hoje em diante
            if (turno.operador() != operador || turno.data().isBefore(hoje)) {
                continue;
            }
            if (proximo == null) {
                proximo = turno;
            }
            String observacao = Contexto.FERIADOS.nomeDoFeriado(turno.data())
                    .map(nome -> "Feriado: " + nome)
                    .orElse("");
            linhas.add(new LinhaEscala(Formatos.data(turno.data()), Formatos.diaSemana(turno.data()),
                    turno.periodo().toString(), nomeDaAtracao, observacao));
        }
        tabela.setItems(FXCollections.observableArrayList(linhas));

        if (proximo == null) {
            lblProximo.setText("Você não tem turnos escalados nos próximos dias.");
        } else if (proximo.data().equals(hoje)) {
            lblProximo.setText("Hoje você trabalha no turno da " + proximo.periodo() + ".");
        } else {
            lblProximo.setText("Seu próximo turno: " + Formatos.data(proximo.data()) + " ("
                    + Formatos.diaSemana(proximo.data()) + "), " + proximo.periodo() + ".");
        }
    }

    // Os feriados vêm da internet: carrega em segundo plano e, se der certo, redesenha a tabela
    private void carregarFeriadosEmSegundoPlano() {
        int ano = LocalDate.now().getYear();
        Task<Void> tarefa = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Contexto.FERIADOS.carregarAno(ano);
                try {
                    Contexto.FERIADOS.carregarAno(ano + 1);
                } catch (Exception ignorada) {
                    // sem os feriados do ano seguinte a escala continua normal
                }
                return null;
            }
        };
        tarefa.setOnSucceeded(evento -> atualizarTabela());
        Tarefas.executar(tarefa);
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
