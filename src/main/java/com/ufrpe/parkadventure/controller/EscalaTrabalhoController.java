package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.controller.controles.Tarefas;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.repository.EscalaTrabalho.Turno;
import com.ufrpe.parkadventure.view.Aviso;
import com.ufrpe.parkadventure.view.Formatos;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class EscalaTrabalhoController {
    @FXML private Label lblProximoFeriado, lblStatus;
    @FXML private TableView<LinhaEscala> tabela;
    @FXML private TableColumn<LinhaEscala, String> colData, colDia, colOperador, colAtracao, colObservacao;
    @FXML private DatePicker dpData;
    @FXML private ComboBox<Operador> cbOperador;

    private record LinhaEscala(Turno turno, String data, String dia, String operador,
                               String atracao, String observacao) { }
    @FXML
    private void initialize() {
        colData.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().data()));
        colDia.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().dia()));
        colOperador.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().operador()));
        colAtracao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().atracao()));
        colObservacao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().observacao()));

        tabela.setPlaceholder(new Label("Nenhum operador escalado ainda."));

        lblProximoFeriado.visibleProperty().bind(lblProximoFeriado.textProperty().isNotEmpty());
        lblProximoFeriado.managedProperty().bind(lblProximoFeriado.visibleProperty());

        cbOperador.setItems(FXCollections.observableArrayList(operadoresDisponiveis()));
        cbOperador.setConverter(new StringConverter<>() {
            @Override public String toString(Operador operador) {
                return operador == null ? "" : operador.getNome();
            }
            @Override public Operador fromString(String texto) {
                return null;
            }
        });

        tabela.setPlaceholder(new Label("Nenhuma escala cadastrada."));

        dpData.setValue(LocalDate.now());
        dpData.setDayCellFactory(seletor -> new DateCell() {
            @Override
            public void updateItem(LocalDate dia, boolean vazio) {
                super.updateItem(dia, vazio);
                setDisable(!vazio && dia.isBefore(LocalDate.now()));
            }
        });

        lblProximoFeriado.setText("Consultando feriados na BrasilAPI…");
        atualizarTabela();
        carregarFeriadosEmSegundoPlano();
    }

    private List<Operador> operadoresDisponiveis() {
        return RepositorioAtracaoController.listarTodas().stream()
                .map(Atracao::getOperador)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private void atualizarTabela() {
        List<LinhaEscala> linhas = new ArrayList<>();
        for (Turno turno : RepositorioEscalaDeTrabalhoController.listarOrdenado()) {
            String atracao = RepositorioAtracaoController.buscarOperador(turno.operador());
            String observacao = ContextoController.FERIADOS.nomeDoFeriado(turno.data())
                    .map(nome -> "Feriado: " + nome)
                    .orElse("");
            linhas.add(new LinhaEscala(
                    turno,
                    Formatos.data(turno.data()),
                    Formatos.diaSemana(turno.data()),
                    turno.operador().getNome(),
                    atracao == null ? "—" : atracao,
                    observacao));
        }
        tabela.setItems(FXCollections.observableArrayList(linhas));
        atualizarProximoFeriado();
    }

    private void atualizarProximoFeriado() {
        Optional<Map.Entry<LocalDate, String>> proximo = ContextoController.FERIADOS.proximoFeriado(LocalDate.now());
        if (proximo.isEmpty()) {
            return;
        }
        LocalDate data = proximo.get().getKey();
        List<Operador> escalados = RepositorioEscalaDeTrabalhoController.operadoresDoDia(data);
        String quem = escalados.isEmpty()
                ? "nenhum operador escalado"
                : escalados.stream().map(Operador::getNome).collect(Collectors.joining(", "));
        lblProximoFeriado.setText("Próximo feriado: " + proximo.get().getValue()
                + " (" + Formatos.data(data) + ") — " + quem);
    }

    private void carregarFeriadosEmSegundoPlano() {
        int ano = LocalDate.now().getYear();
        Task<Void> tarefa = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ContextoController.FERIADOS.carregarAno(ano);
                try {
                    ContextoController.FERIADOS.carregarAno(ano + 1);
                } catch (Exception ignorada) {

                }
                return null;
            }
        };
        tarefa.setOnSucceeded(evento -> atualizarTabela());
        tarefa.setOnFailed(evento -> lblProximoFeriado.setText(
                "Não foi possível consultar a BrasilAPI (sem internet?). A escala funciona normalmente, sem os feriados."));
        Tarefas.executar(tarefa);
    }

    @FXML
    private void onEscalar() {
        LocalDate data = dpData.getValue();
        Operador operador = cbOperador.getValue();

        if (data == null || operador == null) {
            Aviso.erro(lblStatus, "Escolha a data e o operador.");
            return;
        }
        if (data.isBefore(LocalDate.now())) {
            Aviso.erro(lblStatus, "Não é possível escalar para uma data que já passou.");
            return;
        }
        if (!RepositorioEscalaDeTrabalhoController.escalar(data, operador)) {
            Aviso.erro(lblStatus, operador.getNome() + " já está escalado em " + Formatos.data(data) + ".");
            return;
        }
        Aviso.sucesso(lblStatus, operador.getNome() + " escalado em " + Formatos.data(data) + ".");
        atualizarTabela();
    }

    @FXML
    private void onRemover() {
        LinhaEscala selecionada = tabela.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            Aviso.erro(lblStatus, "Selecione uma linha da tabela para remover.");
            return;
        }
        RepositorioEscalaDeTrabalhoController.remover(selecionada.turno());
        Aviso.sucesso(lblStatus, "Escala de " + selecionada.operador() + " em " + selecionada.data() + " removida.");
        atualizarTabela();
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
