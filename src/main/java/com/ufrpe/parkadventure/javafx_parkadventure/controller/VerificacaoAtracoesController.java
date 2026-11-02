package com.ufrpe.parkadventure.javafx_parkadventure;

import com.ufrpe.parkadventure.javafx_parkadventure.controller.Contexto;
import com.ufrpe.parkadventure.javafx_parkadventure.controller.Navegador;
import com.ufrpe.parkadventure.javafx_parkadventure.controller.Navegador.Tela;
import com.ufrpe.parkadventure.javafx_parkadventure.controller.ResultadoAcesso;
import com.ufrpe.parkadventure.javafx_parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.javafx_parkadventure.model.atracao.Infantil;
import com.ufrpe.parkadventure.javafx_parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.javafx_parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.javafx_parkadventure.view.Aviso;
import com.ufrpe.parkadventure.javafx_parkadventure.view.Formatos;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VerificacaoAtracoesController {

    @FXML private Label lblVisitante, lblResultado;
    @FXML private TableView<LinhaAtracao> tabela;
    @FXML private TableColumn<LinhaAtracao, String> colNome, colTipo, colRequisitos, colOcupacao,
            colFila, colOperador, colSituacao;
    @FXML private ComboBox<String> cbHorario;
    private record LinhaAtracao(Atracao atracao, String nome, String tipo, String requisitos,
                                String ocupacao, String fila, String operador, String situacao) { }

    private Visitante visitante;
    private Ingresso ingresso;

    @FXML
    private void initialize() {
        visitante = Contexto.SESSAO.getVisitanteAtual();
        ingresso = (visitante == null) ? null : Contexto.INGRESSOS.buscarUltimo(visitante);
        lblVisitante.setText(descreverVisitante());

        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().nome()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().tipo()));
        colRequisitos.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().requisitos()));
        colOcupacao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().ocupacao()));
        colFila.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().fila()));
        colOperador.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().operador()));
        colSituacao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().situacao()));
        colSituacao.setCellFactory(coluna -> new TableCell<>() {
            @Override
            protected void updateItem(String texto, boolean vazia) {
                super.updateItem(texto, vazia);
                getStyleClass().removeAll("sit-ok", "sit-alerta", "sit-bloqueio");
                if (vazia || texto == null) {
                    setText(null);
                    return;
                }
                setText(texto);
                if (texto.startsWith("Liberado")) {
                    getStyleClass().add("sit-ok");
                } else if (texto.startsWith("Lotada")) {
                    getStyleClass().add("sit-alerta");
                } else {
                    getStyleClass().add("sit-bloqueio");
                }
            }
        });

        tabela.setPlaceholder(new Label("Nenhuma atração cadastrada."));
        tabela.getSelectionModel().selectedItemProperty()
                .addListener((observado, antiga, nova) -> carregarHorarios());

        atualizarTabela();
    }

    private String descreverVisitante() {
        if (visitante == null) {
            return "Escolha um visitante na caixa do topo da janela.";
        }
        String situacaoIngresso = (ingresso == null)
                ? "sem ingresso (compre na Tela ingresso)"
                : Formatos.tipoIngresso(ingresso.getTipoIngresso()) + " válida até "
                + Formatos.data(ingresso.getDataValidade());
        return "Visitante: " + visitante.getNome() + "  •  " + visitante.getIdade() + " anos  •  "
                + visitante.getAltura() + " cm  •  Ingresso: " + situacaoIngresso;
    }

    private void atualizarTabela() {
        Atracao anterior = atracaoSelecionada();

        List<LinhaAtracao> linhas = new ArrayList<>();
        for (Atracao a : Contexto.ATRACOES.listarTodas()) {
            linhas.add(new LinhaAtracao(
                    a,
                    a.getNome(),
                    a.getTipo(),
                    descreverRequisitos(a),
                    a.getOcupacaoAtual() + "/" + a.getCapacidadeMaxima(),
                    a.getFilaVirtual().tamanhoTotal() + " esperando",
                    a.temOperador() ? a.getOperador().getNome() : "— sem operador —",
                    diagnosticar(a)));
        }
        tabela.setItems(FXCollections.observableArrayList(linhas));

        if (anterior != null) {
            tabela.getItems().stream()
                    .filter(linha -> linha.atracao() == anterior)
                    .findFirst()
                    .ifPresent(linha -> tabela.getSelectionModel().select(linha));
        }
    }

    private String descreverRequisitos(Atracao atracao) {
        if (atracao instanceof Infantil infantil) {
            return "Até " + infantil.getAlturaMaxima() + " cm e " + infantil.getIdadeMaxima() + " anos";
        }
        return "Mín. " + atracao.getAlturaMinima() + " cm e " + atracao.getIdadeMinima() + " anos";
    }

    private String diagnosticar(Atracao atracao) {
        if (visitante == null) {
            return "Escolha um visitante";
        }
        if (!atracao.temOperador()) {
            return "Fechada (sem operador)";
        }
        if (ingresso == null) {
            return "Sem ingresso";
        }
        if (!ingresso.valido(LocalDate.now())) {
            return "Ingresso expirado";
        }
        return switch (atracao.verificarRestricao(visitante)) {
            case FORA_DOS_REQUISITOS -> "Fora dos requisitos";
            case SEM_RESPONSAVEL -> "Precisa de responsável";
            case NENHUM -> atracao.estaLotada() ? "Lotada — use a fila virtual" : "Liberado";
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
    }

   private Atracao exigirVisitanteIngressoEAtracao() {
        if (visitante == null) {
            Aviso.erro(lblResultado, "Escolha um visitante na caixa do topo da janela.");
            return null;
        }
        if (ingresso == null) {
            Aviso.erro(lblResultado, visitante.getNome() + " ainda não tem ingresso. Compre um na Tela ingresso.");
            return null;
        }
        Atracao atracao = atracaoSelecionada();
        if (atracao == null) {
            Aviso.erro(lblResultado, "Selecione uma atração na tabela.");
        }
        return atracao;
    }

    @FXML
    private void onEntrar() {
        Atracao atracao = exigirVisitanteIngressoEAtracao();
        if (atracao == null) {
            return;
        }
        ResultadoAcesso resultado = Contexto.CONTROLE_ACESSO.registrarAcesso(visitante, ingresso, atracao);
        mostrar(resultado, atracao);
        atualizarTabela();
    }

    @FXML
    private void onEntrarNaFila() {
        Atracao atracao = exigirVisitanteIngressoEAtracao();
        if (atracao == null) {
            return;
        }
        String horario = cbHorario.getValue();
        if (horario == null) {
            Aviso.erro(lblResultado, "Escolha o horário da fila virtual.");
            return;
        }
        ResultadoAcesso resultado = Contexto.CONTROLE_ACESSO.entrarNaFilaVirtual(visitante, ingresso, atracao, horario);
        mostrar(resultado, atracao);
        atualizarTabela();
    }

    @FXML
    private void onSimularSaida() {
        Atracao atracao = atracaoSelecionada();
        if (atracao == null) {
            Aviso.erro(lblResultado, "Selecione uma atração na tabela.");
            return;
        }
        if (atracao.getOcupacaoAtual() == 0) {
            Aviso.erro(lblResultado, "Ninguém está usando " + atracao.getNome() + " agora.");
            return;
        }
        Visitante chamado = Contexto.CONTROLE_ACESSO.finalizarUso(atracao);
        Aviso.sucesso(lblResultado, chamado == null
                ? "Vaga liberada em " + atracao.getNome() + "."
                : "Vaga liberada em " + atracao.getNome() + ". " + chamado.getNome() + " saiu da fila e entrou!");
        atualizarTabela();
    }

    @FXML
    private void onVerFila() {
        Navegador.ir(Tela.FILA_VIRTUAL);
    }

    private void mostrar(ResultadoAcesso resultado, Atracao atracao) {
        String mensagem = mensagem(resultado, atracao);
        boolean deuCerto = resultado == ResultadoAcesso.AUTORIZADO || resultado == ResultadoAcesso.NA_FILA;
        if (deuCerto) {
            Aviso.sucesso(lblResultado, mensagem);
        } else {
            Aviso.erro(lblResultado, mensagem);
        }
    }

   private String mensagem(ResultadoAcesso resultado, Atracao atracao) {
        String nome = visitante.getNome();
        return switch (resultado) {
            case AUTORIZADO -> "Acesso liberado! Aproveite " + atracao.getNome() + ", " + nome + ".";
            case ATRACAO_LOTADA -> atracao.getNome() + " está lotada. Escolha um horário e entre na fila virtual.";
            case NA_FILA -> nome + " entrou na fila virtual de " + atracao.getNome() + " às "
                    + atracao.getFilaVirtual().horarioDe(visitante) + " (posição "
                    + atracao.getFilaVirtual().posicaoGeral(visitante) + "º).";
            case FILA_CHEIA -> "A fila de " + atracao.getNome() + " está cheia nesse horário.";
            case HORARIO_INVALIDO -> "Horário inválido. Use de 11:00 a 18:00, de 30 em 30 minutos.";
            case INGRESSO_DE_OUTRO_VISITANTE -> "O ingresso apresentado não pertence a " + nome + ".";
            case INGRESSO_EXPIRADO -> "O ingresso de " + nome + " está fora da validade.";
            case FORA_DOS_REQUISITOS -> nome + " não atinge os requisitos de altura/idade de " + atracao.getNome() + ".";
            case SEM_RESPONSAVEL -> nome + " é menor de idade e precisa de um responsável.";
            case SEM_OPERADOR -> atracao.getNome() + " está fechada: não há operador vinculado.";
            case JA_NA_FILA -> nome + " já está na fila virtual de " + atracao.getNome() + ".";
        };
    }
}