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
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;

public class ParkAdventure extends Application {
    // =============================================
    // || ATRIBUTOS
    // =============================================
    private static final double LARGURA_PADRAO = 900;
    private static final double ALTURA_PADRAO = 640;

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
        // o botão "Stop" de algumas IDEs encerra a JVM sem chamar stop(): o hook ainda grava os dados
        Runtime.getRuntime().addShutdownHook(new Thread(Persistencia::salvar, "salvar-ao-encerrar"));
        // primeira execução: cria as atrações e os operadores do parque
        if (RepositorioAtracaoController.listarTodas().isEmpty()) {
            DadosExemplo.carregarAtracoesPadrao();
        }
        exibir("view/menu.fxml");
        palco.setTitle("Park Adventure");
        palco.setResizable(true);
        palco.show();
    }

    @Override
    public void stop() {
        Persistencia.salvar();
    }

    // =============================================
    // || NAVEGAÇÃO
    // =============================================

    // Avança para outra tela. A tela atual vai para o histórico (o "Voltar" volta para ela).
    public static void proximaCena(String novaTelaFxml) throws IOException {
        String anterior = fxmlAtual;
        exibir(novaTelaFxml);              // se falhar, o histórico não é alterado
        if (anterior != null) {
            historico.push(anterior);
        }
    }

    // Volta para a tela anterior do histórico
    public static void voltar() throws IOException {
        if (!historico.isEmpty()) {
            exibir(historico.peek());
            historico.pop();               // só tira do histórico depois de abrir com sucesso
        }
    }

    // Troca a tela atual por outra SEM guardar a atual no histórico (telas "de passagem").
    // Devolve o controller da nova tela para quem chamou poder configurá-lo.
    public static <T> T substituirCena(String novaTelaFxml) throws IOException {
        return exibir(novaTelaFxml);
    }

    // Igual a substituirCena, para quem não precisa do controller
    public static void substituirCenaPadrao(String novaTelaFxml) throws IOException {
        exibir(novaTelaFxml);
    }

    // Recoloca uma cena que já estava pronta (ex.: o formulário de pagamento com tudo o que foi digitado)
    public static void restaurarCena(Scene cena, String fxml) throws IOException {
        if (cena == null) {
            throw new IOException("Não há tela para restaurar: " + fxml);
        }
        palcoPrimario.setScene(cena);
        fxmlAtual = fxml;
    }

    // Tira do histórico as telas informadas (ex.: depois de pagar, "Voltar" não deve reabrir a compra)
    public static void descartarHistorico(String... telasFxml) {
        for (String tela : telasFxml) {
            historico.removeIf(item -> item.equals(tela));
        }
    }

    // =============================================
    // || CARREGAMENTO DE TELAS
    // =============================================
    private static <T> T exibir(String fxml) throws IOException {
        URL arquivo = ParkAdventure.class.getResource(fxml);
        if (arquivo == null) {
            throw new IOException("Tela não encontrada: " + fxml);
        }
        FXMLLoader loader = new FXMLLoader(arquivo);
        Parent raiz = loader.load();

        // A janela mantém o tamanho que o usuário deu (antes, cada tela nova voltava para 600x400).
        // Se a tela nova precisar de mais espaço que o atual, a janela cresce.
        Scene atual = palcoPrimario.getScene();
        double preferidaLargura = raiz.prefWidth(-1);
        double preferidaAltura = raiz.prefHeight(-1);
        double largura = (atual != null) ? atual.getWidth() : LARGURA_PADRAO;
        double altura = (atual != null) ? atual.getHeight() : ALTURA_PADRAO;
        if (preferidaLargura > largura) largura = preferidaLargura;
        if (preferidaAltura > altura) altura = preferidaAltura;

        palcoPrimario.setScene(new Scene(raiz, largura, altura));
        if (atual == null) {
            palcoPrimario.centerOnScreen();     // só na primeira tela
        }
        fxmlAtual = fxml;
        return loader.getController();
    }
}