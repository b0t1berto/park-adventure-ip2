package com.ufrpe.parkadventure.view;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.Optional;

public final class Aviso {
    private Aviso() { }

    public static void sucesso(Label label, String texto) { mostrar(label, texto, "ok"); }

    public static void erro(Label label, String texto) {
        mostrar(label, texto, "erro");
        Alert alert = new Alert(AlertType.ERROR);
        alert.initOwner(label.getScene().getWindow());
        alert.setTitle("ERRO");
        alert.setHeaderText("Atenção");
        alert.setContentText(texto);
        mostrar(alert);
    }

    public static void info(Label label, String texto) { mostrar(label, texto, null); }

    public static void limpar(Label label) { mostrar(label, "", null); }

    public static Optional<ButtonType> mostrar(Alert alert) {
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.setTitle(alert.getTitle() == null ? "" : alert.getTitle());
        Window owner = alert.getOwner() != null ? alert.getOwner() : janelaAtiva();
        if (owner != null) {
            stage.initOwner(owner);
        }
        stage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox();
        root.getStyleClass().addAll("alerta-parque", classeTipo(alert.getAlertType()));
        root.setPrefWidth(460);

        HBox cabecalho = new HBox(14);
        cabecalho.setAlignment(Pos.CENTER_LEFT);
        cabecalho.getStyleClass().add("alerta-cabecalho");
        double[] arrasto = new double[2];
        cabecalho.setOnMousePressed(event -> {
            arrasto[0] = event.getScreenX() - stage.getX();
            arrasto[1] = event.getScreenY() - stage.getY();
        });
        cabecalho.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - arrasto[0]);
            stage.setY(event.getScreenY() - arrasto[1]);
        });
        Node icone = alert.getDialogPane().getGraphic();
        if (icone != null) {
            alert.getDialogPane().setGraphic(null);
            icone.getStyleClass().add("alerta-icone");
            cabecalho.getChildren().add(icone);
        }

        String titulo = textoPresente(alert.getHeaderText()) ? alert.getHeaderText() : alert.getTitle();
        Label tituloLabel = new Label(titulo == null ? "" : titulo);
        tituloLabel.getStyleClass().add("alerta-titulo");
        tituloLabel.setWrapText(true);
        HBox.setHgrow(tituloLabel, Priority.ALWAYS);
        cabecalho.getChildren().add(tituloLabel);

        ButtonType[] resultado = {null};
        ButtonType cancelamento = alert.getButtonTypes().stream()
                .filter(tipo -> tipo.getButtonData().isCancelButton())
                .findFirst()
                .orElse(null);

        VBox conteudo = new VBox();
        conteudo.getStyleClass().add("alerta-conteudo");
        if (textoPresente(alert.getContentText())) {
            Label mensagem = new Label(alert.getContentText());
            mensagem.getStyleClass().add("alerta-mensagem");
            mensagem.setWrapText(true);
            mensagem.setMaxWidth(420);
            conteudo.getChildren().add(mensagem);
        }

        HBox acoes = new HBox(10);
        acoes.setAlignment(Pos.CENTER_RIGHT);
        acoes.getStyleClass().add("alerta-acoes");
        for (ButtonType tipo : alert.getButtonTypes()) {
            Button botao = new Button(tipo.getText());
            botao.getStyleClass().add(tipo.getButtonData().isCancelButton()
                    ? "alerta-botao-secundario" : "alerta-botao-primario");
            botao.setDefaultButton(tipo.getButtonData().isDefaultButton());
            botao.setCancelButton(tipo.getButtonData().isCancelButton());
            botao.setOnAction(event -> {
                resultado[0] = tipo;
                stage.close();
            });
            acoes.getChildren().add(botao);
        }
        root.getChildren().addAll(cabecalho, conteudo, acoes);

        Scene scene = new Scene(root, Color.TRANSPARENT);
        scene.getStylesheets().add(
                Aviso.class.getResource("/com/ufrpe/parkadventure/view/tema-parque.css").toExternalForm());
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                resultado[0] = cancelamento;
                stage.close();
            }
        });
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> resultado[0] = cancelamento);
        stage.sizeToScene();
        stage.setOnShown(event -> {
            centralizar(stage, owner);
            stage.toFront();
            stage.requestFocus();
        });
        Node conteudoDaJanela = owner instanceof Stage stageOwner && stageOwner.getScene() != null
                ? stageOwner.getScene().getRoot()
                : null;
        Effect efeitoOriginal = conteudoDaJanela != null ? conteudoDaJanela.getEffect() : null;
        if (conteudoDaJanela != null) {
            ColorAdjust escurecer = new ColorAdjust();
            escurecer.setBrightness(-0.3);
            escurecer.setSaturation(-0.12);
            escurecer.setInput(efeitoOriginal);
            conteudoDaJanela.setEffect(escurecer);
        }
        try {
            stage.showAndWait();
        } finally {
            if (conteudoDaJanela != null) {
                conteudoDaJanela.setEffect(efeitoOriginal);
            }
        }
        return Optional.ofNullable(resultado[0]);
    }

    private static Window janelaAtiva() {
        return Window.getWindows().stream()
                .filter(Window::isShowing)
                .filter(Window::isFocused)
                .findFirst()
                .orElse(null);
    }

    private static void centralizar(Stage stage, Window owner) {
        if (owner != null && owner.isShowing()) {
            stage.setX(owner.getX() + (owner.getWidth() - stage.getWidth()) / 2);
            stage.setY(owner.getY() + (owner.getHeight() - stage.getHeight()) / 2);
            return;
        }
        var bounds = Screen.getPrimary().getVisualBounds();
        stage.setX(bounds.getMinX() + (bounds.getWidth() - stage.getWidth()) / 2);
        stage.setY(bounds.getMinY() + (bounds.getHeight() - stage.getHeight()) / 2);
    }

    private static String classeTipo(AlertType tipo) {
        return switch (tipo) {
            case ERROR -> "alerta-erro";
            case WARNING -> "alerta-aviso";
            case INFORMATION -> "alerta-info";
            case CONFIRMATION -> "alerta-confirmacao";
            default -> "alerta-info";
        };
    }

    private static boolean textoPresente(String texto) {
        return texto != null && !texto.isBlank();
    }

    private static void mostrar(Label label, String texto, String classeCss) {
        label.getStyleClass().removeAll("ok", "erro");
        if (classeCss != null) {
            label.getStyleClass().add(classeCss);
        }
        label.setWrapText(true);
        label.setText(texto);
    }
}