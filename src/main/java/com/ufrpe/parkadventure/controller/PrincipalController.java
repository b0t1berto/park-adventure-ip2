package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.controller.controles.Navegador;
import com.ufrpe.parkadventure.controller.controles.Navegador.Tela;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;

import java.util.EnumMap;
import java.util.Map;

public class PrincipalController {
    @FXML private BorderPane moldura;
    @FXML private Button btnEscala, btnCalendario, btnIngresso, btnVerificacao, btnIngressosComprados, btnFilaVirtual;
    @FXML private ComboBox<Visitante> cbVisitante;
    private final Map<Tela, Button> botoes = new EnumMap<>(Tela.class);

    @FXML
    private void initialize() {
        botoes.put(Tela.ESCALA, btnEscala);
        botoes.put(Tela.CALENDARIO, btnCalendario);
        botoes.put(Tela.INGRESSO, btnIngresso);
        botoes.put(Tela.VERIFICACAO, btnVerificacao);
        botoes.put(Tela.INGRESSOS_COMPRADOS, btnIngressosComprados);
        botoes.put(Tela.FILA_VIRTUAL, btnFilaVirtual);

        configurarSeletorDeVisitante();
        Navegador.telaAtualProperty().addListener((observado, antiga, nova) -> destacar(nova));
        Navegador.iniciar(moldura, Tela.INGRESSO);
    }

    private void configurarSeletorDeVisitante() {
        cbVisitante.setItems(FXCollections.observableArrayList(RepositorioVisitanteController.listarTodos()));

        cbVisitante.setConverter(new StringConverter<>() {
            @Override public String toString(Visitante visitante) {
                return visitante == null ? "" : visitante.getNome();
            }
            @Override public Visitante fromString(String texto) {
                return null;
            }
        });

        cbVisitante.getSelectionModel().select(ContextoController.SESSAO.getVisitanteAtual());
        cbVisitante.valueProperty().addListener((observado, antigo, novo) -> {
            ContextoController.SESSAO.setVisitanteAtual(novo);
            Navegador.recarregar();
        });
    }

    private void destacar(Tela telaAtual) {
        botoes.values().forEach(botao -> botao.getStyleClass().remove("ativo"));
        botoes.get(telaAtual).getStyleClass().add("ativo");
    }

    @FXML private void onEscala()            { Navegador.ir(Tela.ESCALA); }
    @FXML private void onCalendario()        { Navegador.ir(Tela.CALENDARIO); }
    @FXML private void onIngresso()          { Navegador.ir(Tela.INGRESSO); }
    @FXML private void onVerificacao()       { Navegador.ir(Tela.VERIFICACAO); }
    @FXML private void onIngressosComprados(){ Navegador.ir(Tela.INGRESSOS_COMPRADOS); }
    @FXML private void onFilaVirtual()       { Navegador.ir(Tela.FILA_VIRTUAL); }
}