package com.ufrpe.parkadventure.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.atracao.FilaVirtual;
import com.ufrpe.parkadventure.model.atracao.Reserva;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.repository.Persistencia;
import com.ufrpe.parkadventure.view.Aviso;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Duration;

// Tela do operador: ele conta as pessoas (+1 / -1) e libera quem tem fura-fila na entrada de prioridade.
public class ControleAtracaoController {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter HORA_SEGUNDOS = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML private Label lblAtracao, lblOcupacao, lblStatus, lblAtualizado;
    @FXML private TextField txtOcupacao;
    @FXML private Button btnEntrou, btnSaiu, btnAjustar, btnLiberar;
    @FXML private TableView<LinhaReserva> tabela;
    @FXML private TableColumn<LinhaReserva, String> colHorario, colVisitante, colSituacao;

    private record LinhaReserva(Reserva reserva, String horario, String nomeVisitante, String situacao) { }

    private Atracao atracao;
    private Timeline atualizador;
    private boolean foraDoTurno;   // o turno acabou (ou ainda não começou): os botões ficam travados

    @FXML
    private void initialize() {
        colHorario.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().horario()));
        colVisitante.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().nomeVisitante()));
        colSituacao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().situacao()));
        tabela.setPlaceholder(new Label("Nenhuma reserva de fura-fila para hoje."));

        Operador operador = ContextoController.SESSAO.getOperadorAtual();
        atracao = (operador == null) ? null : RepositorioAtracaoController.buscarAtracaoPorOperador(operador);
        if (atracao == null) {
            lblAtracao.setText("Nenhuma atração vinculada a este operador.");
            lblOcupacao.setText("-");
            Aviso.erro(lblStatus, "Peça ao administrador para vincular você a uma atração.");
            btnEntrou.setDisable(true);
            btnSaiu.setDisable(true);
            btnAjustar.setDisable(true);
            btnLiberar.setDisable(true);
            txtOcupacao.setDisable(true);
            return;
        }
        lblAtracao.setText(atracao.getNome() + "  •  operador: " + operador.getNome());
        atualizar();

        // atualiza a cada 5 segundos e para sozinho quando a tela sai da janela
        atualizador = new Timeline(new KeyFrame(Duration.seconds(5), evento -> {
            if (tabela.getScene() == null || tabela.getScene().getWindow() == null) {
                atualizador.stop();
            } else {
                atualizar();
            }
        }));
        atualizador.setCycleCount(Timeline.INDEFINITE);
        atualizador.play();
    }

    // ---------------------------------------------------------------- contador

    @FXML
    private void onEntrou() {
        if (atracao.entrar()) {
            Aviso.sucesso(lblStatus, "Entrada registrada (+1).");
        } else {
            Aviso.erro(lblStatus, "A atração está lotada. Espere alguém sair antes de liberar a próxima pessoa.");
        }
        atualizar();
    }

    @FXML
    private void onSaiu() {
        if (atracao.getOcupacaoAtual() == 0) {
            Aviso.erro(lblStatus, "Não há ninguém registrado dentro da atração.");
        } else {
            ContextoController.CONTROLE_ACESSO.finalizarUso(atracao);
            Aviso.sucesso(lblStatus, "Saída registrada (-1).");
        }
        atualizar();
    }

    @FXML
    private void onAjustar() {
        String texto = txtOcupacao.getText() == null ? "" : txtOcupacao.getText().trim();
        int valor;
        try {
            valor = Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            Aviso.erro(lblStatus, "Digite um número inteiro para a ocupação.");
            return;
        }
        if (!atracao.ajustarOcupacao(valor)) {
            Aviso.erro(lblStatus, "A ocupação deve estar entre 0 e " + atracao.getCapacidadeMaxima() + ".");
            return;
        }
        txtOcupacao.setText("");
        Aviso.sucesso(lblStatus, "Ocupação ajustada para " + valor + ".");
        atualizar();
    }

    // ---------------------------------------------------------------- fura-fila

    @FXML
    private void onLiberar() {
        LinhaReserva linha = tabela.getSelectionModel().getSelectedItem();
        if (linha == null) {
            Aviso.erro(lblStatus, "Selecione na tabela quem chegou à entrada de prioridade.");
            return;
        }
        ResultadoAcesso resultado = ContextoController.CONTROLE_ACESSO
                .liberarFuraFila(linha.reserva().getVisitante(), atracao, LocalDateTime.now());
        String nome = linha.nomeVisitante();
        if (resultado == ResultadoAcesso.AUTORIZADO) {
            Persistencia.salvar();   // a reserva passou para "usada"
        }
        switch (resultado) {
            case AUTORIZADO -> Aviso.sucesso(lblStatus, nome + " liberado(a) (+1 registrado).");
            case ATRACAO_LOTADA -> Aviso.erro(lblStatus,
                    "Atração lotada. " + nome + " espera na entrada de prioridade; a reserva continua valendo até o fim da janela.");
            case SEM_RESERVA_NO_HORARIO -> Aviso.erro(lblStatus,
                    nome + " está fora da janela do horário reservado (ou já usou/cancelou).");
            case INGRESSO_EXPIRADO -> Aviso.erro(lblStatus, "O ingresso de " + nome + " não vale hoje.");
            case FORA_DOS_REQUISITOS -> Aviso.erro(lblStatus, nome + " não atinge os requisitos de altura/idade.");
            case SEM_RESPONSAVEL -> Aviso.erro(lblStatus, nome + " é menor de idade e precisa de responsável.");
            case SEM_OPERADOR -> Aviso.erro(lblStatus, "A atração está sem operador vinculado.");
            default -> Aviso.erro(lblStatus, "Não foi possível liberar " + nome + ".");
        }
        atualizar();
    }

    @FXML
    private void onAtualizar() {
        atualizar();
    }

    @FXML
    private void onVoltar() {
        try {
            if (atualizador != null) {
                atualizador.stop();
            }
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------------- atualização da tela

    // Trava a tela quando o operador está fora do próprio turno (e destrava quando o turno dele começa).
    // Só mexe na mensagem quando a situação muda, para não apagar o resultado da última ação.
    private void aplicarTurno(LocalDateTime agora) {
        String motivo = TurnoDoOperador.motivoSemAcesso(ContextoController.SESSAO.getOperadorAtual(), agora);
        boolean fora = motivo != null;
        if (fora == foraDoTurno && !(fora && lblStatus.getText().isEmpty())) {
            return;
        }
        foraDoTurno = fora;
        btnEntrou.setDisable(fora);
        btnSaiu.setDisable(fora);
        btnAjustar.setDisable(fora);
        btnLiberar.setDisable(fora);
        txtOcupacao.setDisable(fora);
        if (fora) {
            Aviso.erro(lblStatus, motivo);
        } else {
            Aviso.limpar(lblStatus);
        }
    }

    private void atualizar() {
        LocalDateTime agora = LocalDateTime.now();
        aplicarTurno(agora);
        atracao.getFilaVirtual().expirarNoShows(agora);

        lblOcupacao.setText(atracao.getOcupacaoAtual() + " / " + atracao.getCapacidadeMaxima()
                + (atracao.estaLotada() ? "  (lotada)" : ""));

        LinhaReserva selecionada = tabela.getSelectionModel().getSelectedItem();
        List<LinhaReserva> linhas = new ArrayList<>();
        LinhaReserva reselecionar = null;
        for (Reserva r : atracao.getFilaVirtual().reservasDoDia(LocalDate.now())) {
            LinhaReserva linha = new LinhaReserva(r, r.getHorario().format(HORA),
                    r.getVisitante().getNome(), situacao(r, agora));
            linhas.add(linha);
            if (selecionada != null && selecionada.reserva() == r) {
                reselecionar = linha;
            }
        }
        tabela.setItems(FXCollections.observableArrayList(linhas));
        if (reselecionar != null) {
            tabela.getSelectionModel().select(reselecionar);
        }
        lblAtualizado.setText("Atualizado às " + agora.format(HORA_SEGUNDOS) + " (a cada 5 segundos)");
    }

    private String situacao(Reserva r, LocalDateTime agora) {
        return switch (r.getEstado()) {
            case USADA -> "Entrou";
            case NO_SHOW -> "Não compareceu";
            case CANCELADA -> "Cancelada";
            case RESERVADA -> {
                LocalDateTime inicio = LocalDateTime.of(r.getData(), r.getHorario());
                LocalDateTime fim = inicio.plus(FilaVirtual.TOLERANCIA);
                if (agora.isBefore(inicio)) {
                    yield "Aguardando o horário";
                }
                yield "NA JANELA — pode liberar até " + fim.toLocalTime().format(HORA);
            }
        };
    }
}
