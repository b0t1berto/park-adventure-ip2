package com.ufrpe.parkadventure.javafx_parkadventure.controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

public final class Navegador {
    private Navegador() { }

    public enum Tela {
        ESCALA("escala-trabalho.fxml"),
        CALENDARIO("calendario.fxml"),
        INGRESSO("tela-ingresso.fxml"),
        VERIFICACAO("verificacao-atracoes.fxml"),
        INGRESSOS_COMPRADOS("ingressos-comprados.fxml"),
        FILA_VIRTUAL("fila-virtual.fxml");

        private final String arquivoFxml;

        Tela(String arquivoFxml) { this.arquivoFxml = arquivoFxml; }

        public String getArquivoFxml() { return arquivoFxml; }
    }

    private static final String PASTA_FXML = "/com/ufrpe/parkadventure/javafx_parkadventure/";
    private static BorderPane moldura;
   private static final ObjectProperty<Tela> telaAtual = new SimpleObjectProperty<>();
    public static ObjectProperty<Tela> telaAtualProperty() { return telaAtual; }

    public static void iniciar(BorderPane moldura, Tela telaInicial) {
        Navegador.moldura = moldura;
        ir(telaInicial);
    }

    public static void ir(Tela tela) {
        URL arquivo = Navegador.class.getResource(PASTA_FXML + tela.getArquivoFxml());
        if (arquivo == null) {
            throw new IllegalStateException("Arquivo FXML não encontrado: " + tela.getArquivoFxml());
        }
        try {
            FXMLLoader loader = new FXMLLoader(arquivo);
            Parent conteudo = loader.load(); 
            moldura.setCenter(conteudo);
            telaAtual.set(tela);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível abrir a tela " + tela, e);
        }
    }

    public static void recarregar() {
        if (moldura != null && telaAtual.get() != null) {
            ir(telaAtual.get());
        }
    }
}