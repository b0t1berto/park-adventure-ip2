package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.Duration;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

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
    private Scene cenaAnterior;   // formulário de pagamento, para o "Voltar" não perder o que foi digitado

    public void setCenaAnterior(Scene cenaAnterior) {
        this.cenaAnterior = cenaAnterior;
    }

    @FXML
    private void initialize() {
        txtCodigoPix.setText("00020126-PARKADVENTURE-" + UUID.randomUUID());
        gerarQrCode(txtCodigoPix.getText());
        atualizarTempo();

        contagem = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            // a tela saiu da janela (ex.: foi trocada por outra): para de contar
            if (lblTempo.getScene() == null || lblTempo.getScene().getWindow() == null) {
                contagem.stop();
                return;
            }
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

    private void abrirConfirmacao(String forma, String situacao) {
        try {
            PagamentoConfirmadoController conf = ParkAdventure.substituirCena("view/pagamento-confirmado.fxml");
            conf.setDados(forma, total, situacao);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void atualizarTempo() {
        int min = Math.max(segundosRestantes, 0) / 60;
        int seg = Math.max(segundosRestantes, 0) % 60;
        lblTempo.setText(String.format("Expira em %02d:%02d", min, seg));
    }

    private void gerarQrCode(String texto) {
        try {
            BitMatrix matriz = new QRCodeWriter().encode(texto, BarcodeFormat.QR_CODE, 400, 400);
            ByteArrayOutputStream saida = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matriz, "PNG", saida);
            ivQrCode.setImage(new Image(new ByteArrayInputStream(saida.toByteArray())));
        } catch (WriterException | IOException e) {
            lblStatus.setText("Não foi possível gerar o QR Code.");
            e.printStackTrace();
        }
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
        abrirConfirmacao("Pix", "Pagamento aprovado");
    }

    @FXML
    private void aoVoltar() {
        contagem.stop();
        try {
            if (cenaAnterior != null) {
                ParkAdventure.restaurarCena(cenaAnterior, "view/pagamento.fxml");   // o formulário volta como estava
            } else {
                ParkAdventure.voltar();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}