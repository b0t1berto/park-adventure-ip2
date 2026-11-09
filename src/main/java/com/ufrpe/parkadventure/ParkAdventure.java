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
        navegar("view/menu.fxml");
        palco.setTitle("Park Adventure");
        palco.setResizable(true);
        palco.show();
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
        boolean primeiraTela = palcoPrimario.getScene() == null;
        palcoPrimario.setScene(criarCena(raiz));
        if (primeiraTela) {
            palcoPrimario.centerOnScreen();
        }
    }

    /**
     * Cria uma Scene com as dimensões da tela atual. Isso evita que a janela
     * volte ao tamanho preferido de cada FXML durante a navegação e preserva
     * o estado maximizado do Stage.
     */
    public static Scene criarCena(Parent raiz) {
        if (palcoPrimario != null && palcoPrimario.getScene() != null) {
            Scene atual = palcoPrimario.getScene();
            double largura = atual.getWidth() > 0 ? atual.getWidth() : 1000;
            double altura = atual.getHeight() > 0 ? atual.getHeight() : 700;
            return new Scene(raiz, largura, altura);
        }
        return new Scene(raiz, 1000, 700);
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
