package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.app.Contexto;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Window;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Tela de configurações do Park Adventure.
 * Permite ajustar a aparência e consultar informações sobre o sistema.
 */
public class ConfiguracoesController {

    // ---- Aparência ----
    @FXML private Label lblTamanhoFonte;

    // ---- Sobre ----
    @FXML private Label lblTotalAtracoes;
    @FXML private Label lblTotalVisitantes;
    @FXML private Label lblTotalIngressos;

    // ---- Resultado ----
    @FXML private Label lblResultado;

    // Preferências salvas em memória
    private static final String PREFS_FILE = "dados/prefs.txt";

    private IntegerProperty tamanhoFonte = new SimpleIntegerProperty(13);

    @FXML
    private void initialize() {
        carregarPreferencias();
        atualizarInterface();
    }

    // =============================================
    // || Aparência
    // =============================================

    @FXML
    private void onAumentarFonte() {
        if (tamanhoFonte.get() < 24) {
            tamanhoFonte.set(tamanhoFonte.get() + 1);
            aplicarTamanhoFonte();
            salvarPreferencias();
            Aviso.sucesso(lblResultado, "Tamanho da fonte ajustado para " + tamanhoFonte.get() + " px.");
        }
    }

    @FXML
    private void onDiminuirFonte() {
        if (tamanhoFonte.get() > 8) {
            tamanhoFonte.set(tamanhoFonte.get() - 1);
            aplicarTamanhoFonte();
            salvarPreferencias();
            Aviso.sucesso(lblResultado, "Tamanho da fonte ajustado para " + tamanhoFonte.get() + " px.");
        }
    }

    private void aplicarTamanhoFonte() {
        lblTamanhoFonte.setText(tamanhoFonte.get() + " px");
        // Aplica o tamanho da fonte na raiz da cena
        Window janela = lblTamanhoFonte.getScene().getWindow();
        if (janela != null) {
            janela.getScene().getRoot().setStyle("-fx-font-size: " + tamanhoFonte.get() + "px;");
        }
    }

    // =============================================
    // || Preferências
    // =============================================

    private void carregarPreferencias() {
        try {
            Path prefs = Path.of(PREFS_FILE);
            if (!Files.exists(prefs)) {
                return;
            }
            for (String linha : Files.readAllLines(prefs, StandardCharsets.UTF_8)) {
                String[] partes = linha.split("=", 2);
                if (partes.length != 2) continue;
                String chave = partes[0].trim();
                String valor = partes[1].trim();
                if ("tamanhoFonte".equals(chave)) {
                    tamanhoFonte.set(Integer.parseInt(valor));
                }
            }
        } catch (IOException | NumberFormatException e) {
            // Ignora erros e usa os valores padrão
        }
    }

    private void salvarPreferencias() {
        try {
            Path prefs = Path.of(PREFS_FILE);
            if (!Files.exists(prefs.getParent())) {
                Files.createDirectories(prefs.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(prefs, StandardCharsets.UTF_8)) {
                writer.write("tamanhoFonte=" + tamanhoFonte.get());
                writer.newLine();
            }
        } catch (IOException e) {
            // Silencioso: preferências não críticas
        }
    }

    private void atualizarInterface() {
        lblTamanhoFonte.setText(tamanhoFonte.get() + " px");
        atualizarEstatisticas();
    }

    private void atualizarEstatisticas() {
        lblTotalAtracoes.setText(String.valueOf(Contexto.SERVICO_ATRACAO.listarTodas().size()));
        lblTotalVisitantes.setText(String.valueOf(Contexto.SERVICO_VISITANTE.listarTodos().size()));
        lblTotalIngressos.setText(String.valueOf(Contexto.SERVICO_INGRESSO.listarTodos().size()));
    }

    // =============================================
    // || Navegação
    // =============================================

    @FXML
    private void onVoltar() {
        salvarPreferencias();
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
