package com.ufrpe.parkadventure.view;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public final class DialogoParque {
    private DialogoParque() { }

    public static void mostrar(Alert.AlertType tipo, Scene cena, String titulo, String mensagem) {
        mostrar(tipo, cena, titulo, mensagem, resposta -> { });
    }

    public static void mostrar(Alert.AlertType tipo, Scene cena, String titulo, String mensagem,
                               Consumer<ButtonType> aoFechar) {
        if (cena == null) {
            throw new IllegalArgumentException("A cena é obrigatória para mostrar um aviso.");
        }

        Parent tela = cena.getRoot();
        StackPane camada = new StackPane();
        camada.getStyleClass().add("aviso-camada");
        camada.getStylesheets().addAll(tela.getStylesheets());
        camada.getChildren().add(tela);

        Label cabecalho = new Label(titulo == null || titulo.isBlank() ? tituloPadrao(tipo) : titulo);
        cabecalho.getStyleClass().add("aviso-titulo");
        cabecalho.setWrapText(true);

        Label detalhe = new Label(mensagem == null ? "" : mensagem);
        detalhe.getStyleClass().add("aviso-mensagem");
        detalhe.setWrapText(true);
        detalhe.setMaxWidth(440);

        VBox cartao = new VBox(16, cabecalho, detalhe);
        cartao.getStyleClass().addAll("aviso-cartao", classeTipo(tipo));
        cartao.setAlignment(Pos.CENTER_LEFT);
        cartao.setPadding(new Insets(24));
        cartao.setPrefWidth(500);
        cartao.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        cartao.setMaxSize(500, Region.USE_PREF_SIZE);

        Button principal = new Button(tipo == Alert.AlertType.CONFIRMATION ? "Confirmar" : "OK");
        principal.getStyleClass().add("botao-primario");
        principal.setDefaultButton(true);
        HBox botoes = new HBox(10);
        botoes.setAlignment(Pos.CENTER_RIGHT);
        if (tipo == Alert.AlertType.CONFIRMATION) {
            Button cancelar = new Button("Cancelar");
            cancelar.getStyleClass().add("botao-secundario");
            cancelar.setCancelButton(true);
            botoes.getChildren().add(cancelar);
            cancelar.setOnAction(event -> fechar(cena, camada, tela, aoFechar, ButtonType.CANCEL));
        }
        botoes.getChildren().add(principal);
        cartao.getChildren().add(botoes);

        StackPane fundo = new StackPane(cartao);
        fundo.getStyleClass().add("aviso-fundo");
        fundo.setOnMouseClicked(event -> event.consume());
        camada.getChildren().add(fundo);
        cena.setRoot(camada);

        principal.setOnAction(event -> fechar(cena, camada, tela, aoFechar, ButtonType.OK));
        camada.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                fechar(cena, camada, tela, aoFechar, ButtonType.CANCEL);
                event.consume();
            }
        });
        principal.requestFocus();
    }

    public static void mostrarErro(Scene cena, String titulo, String mensagem) {
        mostrar(Alert.AlertType.ERROR, cena, titulo, mensagem);
    }

    private static void fechar(Scene cena, StackPane camada, Parent tela,
                               Consumer<ButtonType> aoFechar, ButtonType resultado) {
        camada.getChildren().remove(tela);
        cena.setRoot(tela);
        aoFechar.accept(resultado);
    }

    private static String tituloPadrao(Alert.AlertType tipo) {
        return switch (tipo) {
            case CONFIRMATION -> "Confirmação";
            case ERROR -> "Ocorreu um erro";
            case WARNING -> "Atenção";
            case INFORMATION -> "Informação";
            case NONE -> "Park Adventure";
        };
    }

    private static String classeTipo(Alert.AlertType tipo) {
        return switch (tipo) {
            case CONFIRMATION -> "aviso-confirmacao";
            case ERROR -> "aviso-erro";
            case WARNING -> "aviso-atencao";
            case INFORMATION, NONE -> "aviso-informacao";
        };
    }
}
