package com.ufrpe.parkadventure;

import com.ufrpe.parkadventure.repository.Persistencia;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class ParkAdventure extends Application {
    // =============================================
    // || ATRIBUTOS
    // =============================================
    private static Stage palcoPrimario;
    private static final Deque<String> historico = new ArrayDeque<>();
    private static String fxmlAtual;


    // =============================================
    // || MÉTODO START/INICIALIZADOR
    // =============================================
    @Override
    public void start(Stage palco) throws IOException {
        palcoPrimario = palco;
        Persistencia.carregar();
        palco.setTitle("Park Adventure");
        palco.setResizable(true);
        navegar("view/menu.fxml");
        palco.show();
        palco.setMaximized(true);
    }

    @Override
    public void stop() {
        Persistencia.salvar();
    }

    // =============================================
    // || MÉTODO NAVEGAR
    // =============================================
    private static void navegar(String primeiraTelaFxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(ParkAdventure.class.getResource(primeiraTelaFxml));
        Parent raiz = loader.load();
        fxmlAtual = primeiraTelaFxml;
        palcoPrimario.setScene(criarCena(raiz));
    }


    /** Cria uma cena padronizada, reaplicando os estilos em toda navegação. */
    public static Scene criarCena(Parent raiz) {
        Scene cena = new Scene(raiz);
        adicionarEstilo(cena, "/com/ufrpe/parkadventure/view/estilo.css");
        adicionarEstilo(cena, "/com/ufrpe/parkadventure/view/tema-parque.css");
        adicionarEstilo(cena, "/com/ufrpe/parkadventure/view/fila-virtual.css");
        return cena;
    }

    /** Exibe uma tela carregada por navegadores auxiliares sem perder o tema. */
    public static void exibirConteudo(Parent raiz) {
        if (palcoPrimario == null) {
            throw new IllegalStateException("A janela principal ainda não foi inicializada.");
        }
        palcoPrimario.setScene(criarCena(raiz));
    }

    private static void adicionarEstilo(Scene cena, String caminho) {
        var recurso = ParkAdventure.class.getResource(caminho);
        if (recurso != null) {
            String url = recurso.toExternalForm();
            if (!cena.getStylesheets().contains(url)) cena.getStylesheets().add(url);
        }
    }

    // =============================================
    // || MÉTODO AVANÇAR TELA
    // =============================================
    public static void proximaCena(String novaTelaFxml) throws IOException {
        if (fxmlAtual != null) {
            historico.push(fxmlAtual);
        }
        navegar(novaTelaFxml);
    }

    // =============================================
    // || MÉTODO VOLTAR TELA
    // =============================================
    public static void voltar() throws IOException {
        if (!historico.isEmpty()) {
            String telaAnteriorFxml = historico.pop();
            navegar(telaAnteriorFxml);
        }
    }
}
