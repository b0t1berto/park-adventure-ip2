package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.controller.controles.DadosExemplo;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.view.Aviso;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class CadastroFilaVirtualController {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private Label lblVisitante, lblDataVisita, lblDetalhe, lblResultado, lblPosicao;
    @FXML private ComboBox<Atracao> cbAtracao;
    @FXML private ComboBox<String> cbHorario;
    @FXML private Button btnEntrar, btnVoltar;

    private Visitante visitante;
    private Ingresso ingresso;

    @FXML
    private void initialize() {
        if (RepositorioAtracaoController.listarTodas().isEmpty()) {
            DadosExemplo.carregar();
        }

        ingresso = PedidoAtual.getUltimoIngresso();
        if (ingresso != null) {
            visitante = ingresso.getVisitante();
        } else {
            visitante = ContextoController.SESSAO.getVisitanteAtual();
            ingresso = (visitante == null) ? null : RepositorioIngressoController.buscarUltimo(visitante.getNome());
        }

        cbAtracao.setItems(FXCollections.observableArrayList(RepositorioAtracaoController.listarTodas()));
        cbAtracao.setConverter(new StringConverter<>() {
            @Override public String toString(Atracao a) { return a == null ? "" : a.getNome(); }
            @Override public Atracao fromString(String s) { return null; }
        });
        cbAtracao.valueProperty().addListener((obs, antiga, nova) -> aoEscolherAtracao(nova));

        btnEntrar.disableProperty().bind(cbAtracao.valueProperty().isNull()
                .or(cbHorario.valueProperty().isNull()));

        if (visitante == null || ingresso == null) {
            lblVisitante.setText("Nenhum ingresso encontrado");
            Aviso.erro(lblResultado, "Compre um ingresso para entrar na fila virtual.");
            cbAtracao.setDisable(true);
            cbHorario.setDisable(true);
            btnEntrar.disableProperty().unbind();
            btnEntrar.setDisable(true);
            return;
        }
        lblVisitante.setText(visitante.getNome());
        lblDataVisita.setText("Visita em " + ingresso.getDataValidade().format(DATA));
    }

    private void aoEscolherAtracao(Atracao atracao) {
        Aviso.limpar(lblResultado);
        lblPosicao.setText("");
        cbHorario.getSelectionModel().clearSelection();
        if (atracao == null) {
            cbHorario.getItems().clear();
            lblDetalhe.setText("");
            return;
        }
        cbHorario.setItems(FXCollections.observableArrayList(atracao.getFilaVirtual().getHorarios()));
        lblDetalhe.setText("Altura mínima: " + atracao.getAlturaMinima() + " cm  •  Idade mínima: "
                + atracao.getIdadeMinima() + " anos  •  Pessoas na fila: "
                + atracao.getFilaVirtual().tamanhoTotal());
    }

    @FXML
    private void aoEntrarNaFila() {
        Atracao atracao = cbAtracao.getValue();
        String horario = cbHorario.getValue();
        if (atracao == null || horario == null) {
            Aviso.erro(lblResultado, "Escolha a atração e o horário.");
            return;
        }
        ResultadoAcesso resultado = ContextoController.CONTROLE_ACESSO
                .entrarNaFilaVirtual(visitante, ingresso, atracao, horario);

        boolean deuCerto = resultado == ResultadoAcesso.NA_FILA;
        if (deuCerto) {
            Aviso.sucesso(lblResultado, mensagem(resultado, atracao));
            lblPosicao.setText(atracao.getFilaVirtual().posicaoGeral(visitante) + "º");
            lblDetalhe.setText("Pessoas na fila: " + atracao.getFilaVirtual().tamanhoTotal());
        } else {
            Aviso.erro(lblResultado, mensagem(resultado, atracao));
            lblPosicao.setText("");
        }
    }

    private String mensagem(ResultadoAcesso resultado, Atracao atracao) {
        String nome = visitante.getNome();
        return switch (resultado) {
            case NA_FILA -> nome + " entrou na fila virtual de " + atracao.getNome() + " às "
                    + atracao.getFilaVirtual().horarioDe(visitante) + ".";
            case FILA_CHEIA -> "A fila de " + atracao.getNome() + " está cheia nesse horário. Escolha outro.";
            case HORARIO_INVALIDO -> "Horário inválido. Use de 11:00 a 18:00, de 30 em 30 minutos.";
            case INGRESSO_DE_OUTRO_VISITANTE -> "O ingresso não pertence a " + nome + ".";
            case INGRESSO_EXPIRADO -> "O ingresso de " + nome + " está fora da validade.";
            case FORA_DOS_REQUISITOS -> nome + " não atinge os requisitos de altura/idade de " + atracao.getNome() + ".";
            case SEM_RESPONSAVEL -> nome + " é menor de idade e precisa de um responsável.";
            case SEM_OPERADOR -> atracao.getNome() + " está fechada: não há operador vinculado.";
            case JA_NA_FILA -> nome + " já está na fila virtual de " + atracao.getNome() + ".";
            default -> "Não foi possível entrar na fila.";
        };
    }

    @FXML
    private void aoVoltar() {
        try {
            FXMLLoader loader = new FXMLLoader(ParkAdventure.class.getResource("view/menu.fxml"));
            ((Stage) btnVoltar.getScene().getWindow()).setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
