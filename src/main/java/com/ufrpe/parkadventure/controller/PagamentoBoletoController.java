package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class PagamentoBoletoController {

    @FXML private Label lblValor, lblVencimento, lblStatus;
    @FXML private TextField txtLinhaDigitavel;
    @FXML private Button btnCopiar, btnVoltar, btnConcluir;

    private double total;

    @FXML
    private void initialize() {
        LocalDate vencimento = LocalDate.now().plusDays(3);
        lblVencimento.setText(
                vencimento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        txtLinhaDigitavel.setText(gerarLinhaFicticia());
        lblStatus.setText("Boleto gerado. Aguardando pagamento.");
    }

    public void setValor(double total) {
        this.total = total;
        lblValor.setText(String.format("R$ %.2f", total));
    }

    private void abrirConfirmacao(Stage stage, String forma, String situacao) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ParkAdventure.class.getResource("view/pagamento-confirmado.fxml"));
            Scene scene = ParkAdventure.criarCena(loader.load());
            PagamentoConfirmadoController conf = loader.getController();
            conf.setDados(forma, total, situacao);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String gerarLinhaFicticia() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        int[] grupos = {5, 5, 5, 6, 5, 6, 1, 14};
        for (int i = 0; i < grupos.length; i++) {
            for (int j = 0; j < grupos[i]; j++) sb.append(r.nextInt(10));
            if (i < grupos.length - 1) sb.append(' ');
        }
        return sb.toString();
    }

    @FXML
    private void aoCopiar() {
        ClipboardContent conteudo = new ClipboardContent();
        conteudo.putString(txtLinhaDigitavel.getText());
        Clipboard.getSystemClipboard().setContent(conteudo);
        lblStatus.setText("Linha digitável copiada!");
    }

    @FXML
    private void aoConcluir() {
        abrirConfirmacao((Stage) btnConcluir.getScene().getWindow(),
                "Boleto", "Pedido registrado. O ingresso é liberado após a compensação (até 3 dias úteis).");
    }

    @FXML
    private void aoVoltar() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ParkAdventure.class.getResource("view/pagamento.fxml"));
            Scene scene = ParkAdventure.criarCena(loader.load());
            Stage stage = (Stage) btnVoltar.getScene().getWindow();
            PagamentoController p = loader.getController();
            p.setResumo("Ingresso day use", LocalDate.now(), 1, total);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}