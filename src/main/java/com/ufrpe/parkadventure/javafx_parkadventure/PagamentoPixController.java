package com.ufrpe.parkadventure.javafx_parkadventure;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.UUID;

public class PagamentoPixController {

    @FXML private Label lblValor, lblTempo, lblStatus;
    @FXML private ImageView ivQrCode;
    @FXML private TextField txtCodigoPix;
    @FXML private Button btnCopiar, btnVoltar, btnJaPaguei;

    private Timeline contagem;
    private int segundosRestantes = 300; // 5 minutos
    private double total;

    @FXML
    private void initialize() {
        txtCodigoPix.setText("00020126-PARKADVENTURE-" + UUID.randomUUID());
        atualizarTempo();

        contagem = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            segundosRestantes--;
            atualizarTempo();
            if (segundosRestantes <= 0) {
                contagem.stop();
                lblStatus.setText("O código expirou. Volte e gere um novo pagamento.");
                btnJaPaguei.setDisable(true);
                btnCopiar.setDisable(true);
            }
        }));
        contagem.setCycleCount(Timeline.INDEFINITE);
        contagem.play();
    }

    public void setValor(double total) {
        this.total = total;
        lblValor.setText(String.format("R$ %.2f", total));
    }

    private void abrirConfirmacao(Stage stage, String forma, String situacao) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource("pagamento-confirmado.fxml"));
            Scene scene = new Scene(loader.load());
            PagamentoConfirmadoController conf = loader.getController();
            conf.setDados(forma, total, situacao);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void atualizarTempo() {
        int min = Math.max(segundosRestantes, 0) / 60;
        int seg = Math.max(segundosRestantes, 0) % 60;
        lblTempo.setText(String.format("Expira em %02d:%02d", min, seg));
    }

    @FXML
    private void aoCopiar() {
        ClipboardContent conteudo = new ClipboardContent();
        conteudo.putString(txtCodigoPix.getText());
        Clipboard.getSystemClipboard().setContent(conteudo);
        lblStatus.setText("Código copiado!");
    }

    @FXML
    private void aoConfirmar() {
        contagem.stop();
        abrirConfirmacao((Stage) btnJaPaguei.getScene().getWindow(),
                "Pix", "Pagamento aprovado");
    }

    @FXML
    private void aoVoltar() {
        contagem.stop();
        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource("pagamento.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnVoltar.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}