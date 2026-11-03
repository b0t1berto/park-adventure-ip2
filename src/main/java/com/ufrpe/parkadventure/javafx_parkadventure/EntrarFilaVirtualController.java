package com.ufrpe.parkadventure.javafx_parkadventure;

import com.ufrpe.parkadventure.javafx_parkadventure.controller.Contexto;
import com.ufrpe.parkadventure.javafx_parkadventure.controller.ResultadoAcesso;
import com.ufrpe.parkadventure.javafx_parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.javafx_parkadventure.model.atracao.FilaVirtual;
import com.ufrpe.parkadventure.javafx_parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.javafx_parkadventure.model.ingresso.TipoIngresso;
import com.ufrpe.parkadventure.javafx_parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.javafx_parkadventure.view.Aviso;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EntrarFilaVirtualController {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private Label lblVisitante, lblVagas, lblResultado;
    @FXML private TableView<LinhaAtracao> tabela;
    @FXML private TableColumn<LinhaAtracao, String> colAtracao, colTipo, colRequisitos, colSituacao, colFila;
    @FXML private ComboBox<String> cbHorario;
    @FXML private Button btnVoltar, btnEntrar, btnContinuar;

    private record LinhaAtracao(Atracao atracao, String tipo, String requisitos,
                                String situacao, String fila) { }

    private Visitante visitante;
    private LocalDate dataVisita = LocalDate.now().plusDays(1);
    private Ingresso ingressoDaVisita;   // só para validar o cadastro; não é gravado na lista

    @FXML
    private void initialize() {
        colAtracao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().atracao().getNome()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().tipo()));
        colRequisitos.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().requisitos()));
        colSituacao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().situacao()));
        colFila.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().fila()));
        tabela.setPlaceholder(new Label("Nenhuma atração cadastrada."));

        btnEntrar.disableProperty().bind(
                tabela.getSelectionModel().selectedItemProperty().isNull()
                        .or(cbHorario.valueProperty().isNull()));

        tabela.getSelectionModel().selectedItemProperty().addListener(
                (obs, antiga, nova) -> carregarHorarios());
        cbHorario.valueProperty().addListener((obs, antigo, novo) -> atualizarVagas());

        visitante = Contexto.SESSAO.getVisitanteAtual();
        if (visitante == null) {
            lblVisitante.setText("Nenhum visitante selecionado.");
            Aviso.erro(lblResultado, "Escolha um visitante antes de entrar na fila virtual.");
            tabela.setDisable(true);
            return;
        }
        atualizarTitulo();
        atualizarTabela();
    }

    public void setDataVisita(LocalDate dataVisita) {
        this.dataVisita = dataVisita;
        ingressoDaVisita = null;
        if (visitante != null) {
            atualizarTitulo();
        }
    }

    private void atualizarTitulo() {
        lblVisitante.setText("Visitante: " + visitante.getNome() + "  •  " + visitante.getIdade()
                + " anos  •  " + visitante.getAltura() + " cm  •  visita em " + dataVisita.format(DATA));
    }

    private void atualizarTabela() {
        Atracao selecionada = atracaoSelecionada();
        List<LinhaAtracao> linhas = new ArrayList<>();
        for (Atracao atracao : Contexto.ATRACOES.listarTodas()) {
            linhas.add(new LinhaAtracao(
                    atracao,
                    atracao.getTipo(),
                    atracao.getAlturaMinima() + " cm  •  " + atracao.getIdadeMinima() + " anos",
                    situacao(atracao),
                    String.valueOf(atracao.getFilaVirtual().tamanhoTotal())));
        }
        tabela.setItems(FXCollections.observableArrayList(linhas));
        if (selecionada != null) {
            for (LinhaAtracao linha : linhas) {
                if (linha.atracao() == selecionada) {
                    tabela.getSelectionModel().select(linha);
                }
            }
        }
    }

    private String situacao(Atracao atracao) {
        if (!atracao.temOperador()) {
            return "Fechada (sem operador)";
        }
        if (atracao.getFilaVirtual().contem(visitante)) {
            return "Você já está na fila (" + atracao.getFilaVirtual().horarioDe(visitante) + ")";
        }
        return switch (atracao.verificarRestricao(visitante)) {
            case FORA_DOS_REQUISITOS -> "Fora dos requisitos";
            case SEM_RESPONSAVEL -> "Precisa de responsável";
            case NENHUM -> "Disponível";
        };
    }

    private Atracao atracaoSelecionada() {
        LinhaAtracao linha = tabela.getSelectionModel().getSelectedItem();
        return linha == null ? null : linha.atracao();
    }

    private void carregarHorarios() {
        String escolhidoAntes = cbHorario.getValue();
        Atracao atracao = atracaoSelecionada();
        if (atracao == null) {
            cbHorario.getItems().clear();
            return;
        }
        List<String> horarios = atracao.getFilaVirtual().getHorarios();
        cbHorario.getItems().setAll(horarios);
        if (escolhidoAntes != null && horarios.contains(escolhidoAntes)) {
            cbHorario.setValue(escolhidoAntes);
        }
        atualizarVagas();
    }

    private void atualizarVagas() {
        Atracao atracao = atracaoSelecionada();
        String horario = cbHorario.getValue();
        if (atracao == null || horario == null) {
            lblVagas.setText("");
            return;
        }
        FilaVirtual fila = atracao.getFilaVirtual();
        lblVagas.setText("Fila das " + horario + ": " + fila.tamanho(horario)
                + " de " + fila.getCapacidadeMaximaPorHora() + " vagas ocupadas");
    }

    private Ingresso ingressoParaVisita() {
        for (Ingresso ingresso : Contexto.INGRESSOS.listarPorVisitante(visitante)) {
            if (ingresso.valido(dataVisita)) {
                return ingresso;
            }
        }
        if (ingressoDaVisita == null) {
            ingressoDaVisita = new Ingresso(visitante, TipoIngresso.DIARIA, dataVisita, dataVisita,
                    Contexto.CONTROLE_INGRESSO.calcularValor(TipoIngresso.DIARIA, false));
        }
        return ingressoDaVisita;
    }

    @FXML
    private void onEntrarNaFila() {
        Atracao atracao = atracaoSelecionada();
        String horario = cbHorario.getValue();
        if (atracao == null || horario == null) {
            Aviso.erro(lblResultado, "Escolha uma atração e um horário.");
            return;
        }
        ResultadoAcesso resultado = Contexto.CONTROLE_ACESSO
                .entrarNaFilaVirtual(visitante, ingressoParaVisita(), atracao, horario);

        if (resultado == ResultadoAcesso.NA_FILA) {
            Aviso.sucesso(lblResultado, mensagem(resultado, atracao));
        } else {
            Aviso.erro(lblResultado, mensagem(resultado, atracao));
        }
        if (resultado == ResultadoAcesso.NA_FILA || resultado == ResultadoAcesso.JA_NA_FILA) {
            btnContinuar.setDisable(false);
        }
        atualizarTabela();
        atualizarVagas();
    }

    private String mensagem(ResultadoAcesso resultado, Atracao atracao) {
        String nome = visitante.getNome();
        return switch (resultado) {
            case NA_FILA -> nome + " entrou na fila virtual de " + atracao.getNome() + " às "
                    + atracao.getFilaVirtual().horarioDe(visitante) + " (posição "
                    + atracao.getFilaVirtual().posicaoGeral(visitante) + "º).";
            case JA_NA_FILA -> nome + " já está na fila virtual de " + atracao.getNome() + ".";
            case FILA_CHEIA -> "A fila de " + atracao.getNome() + " está cheia nesse horário. Escolha outro.";
            case HORARIO_INVALIDO -> "Horário inválido. Use de 11:00 a 18:00, de 30 em 30 minutos.";
            case FORA_DOS_REQUISITOS -> nome + " não atinge os requisitos de altura/idade de " + atracao.getNome() + ".";
            case SEM_RESPONSAVEL -> nome + " é menor de idade e precisa de um responsável.";
            case SEM_OPERADOR -> atracao.getNome() + " está fechada: não há operador vinculado.";
            case INGRESSO_DE_OUTRO_VISITANTE -> "O ingresso não pertence a " + nome + ".";
            case INGRESSO_EXPIRADO -> "O ingresso de " + nome + " não vale para o dia " + dataVisita.format(DATA) + ".";
            case AUTORIZADO, ATRACAO_LOTADA -> "Não foi possível entrar na fila de " + atracao.getNome() + ".";
        };
    }

    @FXML
    private void onContinuar() {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class
                    .getResource("redirecionamento-pagamento-ingresso.fxml"));
            Scene scene = new Scene(loader.load());
            RedirecionamentoPagamentoController redir = loader.getController();
            redir.setDataVisita(dataVisita);
            ((Stage) btnContinuar.getScene().getWindow()).setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onVoltar() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource("escolha-data.fxml"));
            ((Stage) btnVoltar.getScene().getWindow()).setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}