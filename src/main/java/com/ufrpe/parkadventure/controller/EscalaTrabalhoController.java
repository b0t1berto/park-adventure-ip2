package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.controller.controles.Tarefas;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.model.pessoa.PeriodoTrabalho;
import com.ufrpe.parkadventure.repository.RepositorioEscalaTrabalho.Turno;
import com.ufrpe.parkadventure.repository.Persistencia;
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
    @FXML private TableColumn<LinhaEscala, String> colData, colDia, colPeriodo, colOperador, colAtracao, colObservacao;
    @FXML private DatePicker dpData;
    @FXML private ComboBox<Operador> cbOperador;
    @FXML private ComboBox<PeriodoTrabalho> cbPeriodo;

    private record LinhaEscala(Turno turno, String data, String dia, String periodo, String nomeOperador,
                               String nomeAtracao, String observacao) { }
    @FXML
    private void initialize() {
        colData.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().data()));
        colDia.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().dia()));
        colPeriodo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().periodo()));
        colOperador.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().nomeOperador()));
        colAtracao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().nomeAtracao()));
        colObservacao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().observacao()));

        tabela.setPlaceholder(new Label("Nenhum operador escalado ainda."));

        lblProximoFeriado.visibleProperty().bind(lblProximoFeriado.textProperty().isNotEmpty());
        lblProximoFeriado.managedProperty().bind(lblProximoFeriado.visibleProperty());

        cbOperador.setItems(FXCollections.observableArrayList(operadoresDisponiveis()));
        cbPeriodo.setItems(FXCollections.observableArrayList(PeriodoTrabalho.values()));
        cbPeriodo.setValue(PeriodoTrabalho.MANHA);
        cbOperador.setConverter(new StringConverter<>() {
            @Override public String toString(Operador operador) {
                if (operador == null) {
                    return "";
                }
                Atracao daAtracao = RepositorioAtracaoController.buscarAtracaoPorOperador(operador);
                return operador.getNome() + (daAtracao == null ? "" : " (" + daAtracao.getNome() + ")");
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
        return RepositorioAtracaoController.todosOsOperadores();
    }

    private void atualizarTabela() {
        List<LinhaEscala> linhas = new ArrayList<>();
        for (Turno turno : RepositorioEscalaDeTrabalhoController.listarOrdenado()) {
            if (turno.data().isBefore(LocalDate.now())) {
                continue;   // a tabela mostra de hoje em diante
            }
            Atracao daAtracao = RepositorioAtracaoController.buscarAtracaoPorOperador(turno.operador());
            String nomeDaAtracao = (daAtracao == null) ? null : daAtracao.getNome();
            String observacao = ContextoController.FERIADOS.nomeDoFeriado(turno.data())
                    .map(nome -> "Feriado: " + nome)
                    .orElse("");
            linhas.add(new LinhaEscala(
                    turno,
                    Formatos.data(turno.data()),
                    Formatos.diaSemana(turno.data()),
                    turno.periodo().toString(),
                    turno.operador().getNome(),
                    nomeDaAtracao == null ? "—" : nomeDaAtracao,
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

        PeriodoTrabalho periodo = cbPeriodo.getValue();

        if (data == null || operador == null || periodo == null) {
            Aviso.erro(lblStatus, "Escolha a data, o turno e o operador.");
            return;
        }
        if (data.isBefore(LocalDate.now())) {
            Aviso.erro(lblStatus, "Não é possível escalar para uma data que já passou.");
            return;
        }
        String motivo = RepositorioEscalaDeTrabalhoController.motivoRecusa(data, operador, periodo);
        if (motivo != null) {
            Aviso.erro(lblStatus, motivo);
            return;
        }
        RepositorioEscalaDeTrabalhoController.escalar(data, operador, periodo);
        Aviso.sucesso(lblStatus, operador.getNome() + " escalado em " + Formatos.data(data) + " — " + periodo + ".");
        Persistencia.salvar();   // a escala agora é gravada no arquivo de dados
        atualizarTabela();
    }

    // Preenche os turnos vazios dos próximos dias (cada atração precisa de um operador em cada turno)
    @FXML
    private void onGerar() {
        GeradorEscala.Resultado resultado = GeradorEscala.gerarProximosDias();
        Persistencia.salvar();
        atualizarTabela();
        if (resultado.turnosSemOperador() > 0) {
            Aviso.erro(lblStatus, resultado.turnosCriados() + " turno(s) criado(s), mas " + resultado.turnosSemOperador()
                    + " ficaram sem operador: cadastre mais operadores (mínimo de "
                    + GeradorEscala.MIN_OPERADORES_POR_ATRACAO + " por atração).");
        } else {
            Aviso.sucesso(lblStatus, resultado.turnosCriados() + " turno(s) criado(s). Todas as atrações têm operador em todos os turnos dos próximos "
                    + GeradorEscala.DIAS_A_FRENTE + " dias.");
        }
    }

    @FXML
    private void onRemover() {
        LinhaEscala selecionada = tabela.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            Aviso.erro(lblStatus, "Selecione uma linha da tabela para remover.");
            return;
        }
        RepositorioEscalaDeTrabalhoController.remover(selecionada.turno());
        Persistencia.salvar();
        Aviso.sucesso(lblStatus, "Escala de " + selecionada.nomeOperador() + " em " + selecionada.data() + " removida.");
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
