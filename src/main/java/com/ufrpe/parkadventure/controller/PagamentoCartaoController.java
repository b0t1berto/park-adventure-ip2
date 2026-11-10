package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;

public class PagamentoCartaoController {

    @FXML private Label lblValor, lblStatus;
    @FXML private TextField txtNumeroCartao, txtNomeCartao, txtValidade;
    @FXML private PasswordField pfCvv;
    @FXML private ComboBox<String> cbParcelas;
    @FXML private Button btnVoltar, btnPagar;

    private double total;
    private Scene cenaAnterior;   // formulário de pagamento, para o "Voltar" não perder o que foi digitado

    public void setCenaAnterior(Scene cenaAnterior) {
        this.cenaAnterior = cenaAnterior;
    }

    @FXML
    private void initialize() {
        txtNumeroCartao.textProperty().addListener((obs, antigo, novo) -> {
            String digitos = novo.replaceAll("\\D", "");
            if (digitos.length() > 16) digitos = digitos.substring(0, 16);
            String formatado = digitos.replaceAll("(.{4})(?=.)", "$1 ");
            if (!formatado.equals(novo)) {
                txtNumeroCartao.setText(formatado);
                txtNumeroCartao.positionCaret(formatado.length());
            }
        });

        txtValidade.textProperty().addListener((obs, antigo, novo) -> {
            String d = novo.replaceAll("\\D", "");
            if (d.length() > 4) d = d.substring(0, 4);
            String formatado = d.length() > 2 ? d.substring(0, 2) + "/" + d.substring(2) : d;
            if (!formatado.equals(novo)) {
                txtValidade.setText(formatado);
                txtValidade.positionCaret(formatado.length());
            }
        });

        pfCvv.textProperty().addListener((obs, antigo, novo) -> {
            String d = novo.replaceAll("\\D", "");
            if (d.length() > 4) d = d.substring(0, 4);
            if (!d.equals(novo)) pfCvv.setText(d);
        });
    }

    public void setValor(double total) {
        this.total = total;
        lblValor.setText(String.format("R$ %.2f", total));

        cbParcelas.getItems().clear();
        for (int i = 1; i <= 6; i++) {
            cbParcelas.getItems().add(String.format("%dx de R$ %.2f sem juros", i, total / i));
        }
        cbParcelas.getSelectionModel().selectFirst();
    }

    private void abrirConfirmacao(String forma, String situacao) {
        try {
            PagamentoConfirmadoController conf = ParkAdventure.substituirCena("view/pagamento-confirmado.fxml");
            conf.setDados(forma, total, situacao);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void aoPagar() {
        String erro = validar();
        if (erro != null) {
            lblStatus.setText(erro);
            return;
        }

        abrirConfirmacao("Cartão de crédito", "Pagamento aprovado");
    }

    @FXML
    private void aoVoltar() {
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

    private String validar() {
        if (txtNumeroCartao.getText().replaceAll("\\D", "").length() != 16)
            return "Número do cartão inválido.";

        if (txtNomeCartao.getText().isBlank())
            return "Informe o nome impresso no cartão.";

        if (!validadeOk())
            return "Validade inválida ou cartão vencido.";

        int tamanhoCvv = pfCvv.getText().length();
        if (tamanhoCvv < 3 || tamanhoCvv > 4)
            return "CVV inválido.";

        if (cbParcelas.getValue() == null)
            return "Escolha o número de parcelas.";

        return null;
    }

    private boolean validadeOk() {
        String d = txtValidade.getText().replaceAll("\\D", "");
        if (d.length() != 4) return false;
        try {
            int mes = Integer.parseInt(d.substring(0, 2));
            int ano = 2000 + Integer.parseInt(d.substring(2));
            return !YearMonth.of(ano, mes).isBefore(YearMonth.now());
        } catch (Exception e) {
            return false;
        }
    }
}