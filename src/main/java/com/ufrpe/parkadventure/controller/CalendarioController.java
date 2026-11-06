package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.controller.controles.Tarefas;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.view.Formatos;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class CalendarioController {

    @FXML private Label lblMes, lblStatusApi, lblDetalhe;
    @FXML private GridPane grade;

    private YearMonth mesExibido = YearMonth.now();
    private LocalDate dataSelecionada = LocalDate.now();

    @FXML
    private void initialize() {
        configurarGrade();
        montarGrade();                    // desenha já com o que existir no cache
        mostrarDetalhe(dataSelecionada);
        carregarFeriadosDoMes();          // busca o que faltar na internet e redesenha
    }

    // 7 colunas de largura igual, cabeçalho (dias da semana) e  6 linhas de dias
    private void configurarGrade() {
        grade.getColumnConstraints().clear();
        grade.getRowConstraints().clear();
        for (int i = 0; i < 7; i++) {
            ColumnConstraints coluna = new ColumnConstraints();
            coluna.setPercentWidth(100.0 / 7);
            grade.getColumnConstraints().add(coluna);
        }
        RowConstraints cabecalho = new RowConstraints();
        cabecalho.setMinHeight(24);
        grade.getRowConstraints().add(cabecalho);
        for (int i = 0; i < 6; i++) {
            RowConstraints semana = new RowConstraints();
            semana.setMinHeight(64);
            semana.setVgrow(Priority.ALWAYS);   // as semanas dividem o espaço que sobrar
            grade.getRowConstraints().add(semana);
        }
    }

    private LocalDate primeiroDiaDaGrade() {
        LocalDate primeiro = mesExibido.atDay(1);
        int deslocamento = primeiro.getDayOfWeek().getValue() % 7;
        return primeiro.minusDays(deslocamento);
    }

    private void montarGrade() {
        grade.getChildren().clear();
        lblMes.setText(Formatos.mesAno(mesExibido));

        DayOfWeek[] ordem = {DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY};
        for (int coluna = 0; coluna < 7; coluna++) {
            Label titulo = new Label(Formatos.nomeCurto(ordem[coluna]));
            titulo.getStyleClass().add("cab-semana");
            GridPane.setHalignment(titulo, HPos.CENTER);
            grade.add(titulo, coluna, 0);
        }

        LocalDate dia = primeiroDiaDaGrade();
        for (int i = 0; i < 42; i++) {
            grade.add(criarCelula(dia.plusDays(i)), i % 7, 1 + i / 7);
        }
    }

    private VBox criarCelula(LocalDate dia) {
        VBox celula = new VBox(2);
        celula.getStyleClass().add("dia");

        Label numero = new Label(String.valueOf(dia.getDayOfMonth()));
        numero.getStyleClass().add("dia-numero");
        celula.getChildren().add(numero);

        if (!YearMonth.from(dia).equals(mesExibido)) {
            celula.getStyleClass().add("dia-outro-mes");
        }
        if (dia.equals(LocalDate.now())) {
            celula.getStyleClass().add("dia-hoje");
        }
        if (dia.equals(dataSelecionada)) {
            celula.getStyleClass().add("dia-selecionado");
        }

        Optional<String> feriado = ContextoController.FERIADOS.nomeDoFeriado(dia);
        if (feriado.isPresent()) {
            celula.getStyleClass().add("dia-feriado");
            Label nome = new Label(feriado.get());
            nome.getStyleClass().add("dia-texto");
            nome.setMaxWidth(Double.MAX_VALUE);
            celula.getChildren().add(nome);
            Tooltip.install(celula, new Tooltip(feriado.get()));
        }

        int escalados = ContextoController.ESCALA.operadoresDoDia(dia).size();
        if (escalados > 0) {
            celula.getStyleClass().add("dia-escala");
            Label escala = new Label("Escala: " + escalados + (escalados == 1 ? " operador" : " operadores"));
            escala.getStyleClass().add("dia-texto-escala");
            celula.getChildren().add(escala);
        }

        celula.setOnMouseClicked(evento -> {
            dataSelecionada = dia;
            mostrarDetalhe(dia);
            montarGrade();
        });
        return celula;
    }

    private void mostrarDetalhe(LocalDate dia) {
        StringBuilder texto = new StringBuilder(Formatos.diaSemana(dia) + ", " + Formatos.data(dia));

        ContextoController.FERIADOS.nomeDoFeriado(dia)
                .ifPresent(nome -> texto.append("\nFeriado nacional: ").append(nome));

        List<Operador> escalados = ContextoController.ESCALA.operadoresDoDia(dia);
        if (escalados.isEmpty()) {
            texto.append("\nNenhum operador escalado.");
        } else {
            texto.append("\nOperadores escalados:");
            for (Operador operador : escalados) {
                String atracao = ContextoController.ATRACOES.buscarOperador(operador);
                texto.append("\n  • ").append(operador.getNome())
                        .append(atracao == null ? "" : " — " + atracao);
            }
        }
        lblDetalhe.setText(texto.toString());
    }

    private void carregarFeriadosDoMes() {
        LocalDate inicio = primeiroDiaDaGrade();
        Set<Integer> anos = new TreeSet<>(List.of(inicio.getYear(), inicio.plusDays(41).getYear()));
        Set<Integer> faltando = anos.stream()
                .filter(ano -> !ContextoController.FERIADOS.anoCarregado(ano))
                .collect(Collectors.toSet());
        if (faltando.isEmpty()) {
            lblStatusApi.setText("");
            return;
        }

        lblStatusApi.setText("Consultando feriados na BrasilAPI…");
        YearMonth mesDaConsulta = mesExibido;
        Task<Void> tarefa = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (int ano : faltando) {
                    ContextoController.FERIADOS.carregarAno(ano);
                }
                return null;
            }
        };
        tarefa.setOnSucceeded(evento -> {
            lblStatusApi.setText("");
            if (mesDaConsulta.equals(mesExibido)) {
                montarGrade();
                mostrarDetalhe(dataSelecionada);
            }
        });
        tarefa.setOnFailed(evento -> lblStatusApi.setText(
                "Não foi possível consultar a BrasilAPI (sem internet?). Os feriados não serão exibidos."));
        Tarefas.executar(tarefa);
    }

    @FXML
    private void onMesAnterior() {
        mesExibido = mesExibido.minusMonths(1);
        montarGrade();
        carregarFeriadosDoMes();
    }

    @FXML
    private void onMesSeguinte() {
        mesExibido = mesExibido.plusMonths(1);
        montarGrade();
        carregarFeriadosDoMes();
    }

    @FXML
    private void onHoje() {
        mesExibido = YearMonth.now();
        dataSelecionada = LocalDate.now();
        montarGrade();
        mostrarDetalhe(dataSelecionada);
        carregarFeriadosDoMes();
    }
}
