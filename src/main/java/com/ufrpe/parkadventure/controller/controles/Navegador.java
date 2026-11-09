package com.ufrpe.parkadventure.controller.controles;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import com.ufrpe.parkadventure.ParkAdventure;

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

    private static final String PASTA_FXML = "/com/ufrpe/parkadventure/view/";
    private static BorderPane moldura;
   private static final ObjectProperty<Tela> telaAtual = new SimpleObjectProperty<>();
    public static ObjectProperty<Tela> telaAtualProperty() { return telaAtual; }

    public static void iniciar(BorderPane moldura, Tela telaInicial) {
        Navegador.moldura = moldura;
        ir(telaInicial);
    }

    public static void ir(Tela tela) {
        if (tela == null) throw new IllegalArgumentException("A tela de destino não foi informada.");
        URL arquivo = Navegador.class.getResource(PASTA_FXML + tela.getArquivoFxml());
        if (arquivo == null) {
            throw new IllegalStateException("Arquivo FXML não encontrado: " + tela.getArquivoFxml());
        }
        try {
            FXMLLoader loader = new FXMLLoader(arquivo);
            Parent conteudo = loader.load();
            if (moldura != null) {
                moldura.setCenter(conteudo);
            } else {
                // Algumas telas também são abertas fora do painel administrativo.
                ParkAdventure.exibirConteudo(conteudo);
            }
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