package com.ufrpe.parkadventure;

import com.ufrpe.parkadventure.controller.RepositorioAtracaoController;
import com.ufrpe.parkadventure.controller.controles.DadosExemplo;
import com.ufrpe.parkadventure.repository.Persistencia;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

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
        // primeira execução: cria as atrações e os operadores do parque
        if (RepositorioAtracaoController.listarTodas().isEmpty()) {
            DadosExemplo.carregarAtracoesPadrao();
        }
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
        Scene novaCena = new Scene(raiz, 600, 400);
        palcoPrimario.setScene(novaCena);
        palcoPrimario.centerOnScreen();
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

    // =============================================
    // || SUBSTITUIR A TELA (sem entrar no histórico)
    // =============================================
    // Troca a tela atual por outra e devolve o controller da nova tela, para quem chamou poder
    // passar dados (ex.: setValor). A tela que sai NÃO vai para o histórico, então o "voltar"
    // da nova tela leva para a que veio antes dela. A janela ajusta ao tamanho preferido do FXML.
    public static <T> T substituirCena(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(ParkAdventure.class.getResource(fxml));
        Parent raiz = loader.load();
        fxmlAtual = fxml;
        palcoPrimario.setScene(new Scene(raiz));
        palcoPrimario.centerOnScreen();
        return loader.getController();
    }

    // Igual à anterior, para quando não precisa do controller
    public static void substituirCenaPadrao(String fxml) throws IOException {
        substituirCena(fxml);
    }

    // Volta para uma cena já carregada (ex.: o formulário de pagamento, com o que foi digitado)
    public static void restaurarCena(Scene cena, String fxml) {
        fxmlAtual = fxml;
        palcoPrimario.setScene(cena);
        palcoPrimario.centerOnScreen();
    }

    // Remove telas do histórico (ex.: depois de pagar, "voltar" não deve reabrir a compra)
    public static void descartarHistorico(String... fxmls) {
        historico.removeAll(List.of(fxmls));
    }
}
