package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.ingresso.TipoIngresso;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class RedirecionamentoPagamentoController {

    private static final long TEMPO_MINIMO_MS = 1500;   // a tela de passagem aparece pelo menos esse tempo

    @FXML
    private Label lblMensagem;

    @FXML
    public void initialize() {
        LocalDate data = PedidoAtual.getDataVisita();

        // A consulta à BrasilAPI pode demorar: roda fora da tela para a janela não travar.
        Task<Optional<String>> consulta = new Task<>() {
            @Override
            protected Optional<String> call() throws InterruptedException {
                long inicio = System.currentTimeMillis();
                try {
                    ContextoController.FERIADOS.carregarAno(data.getYear());
                } catch (Exception e) {
                    // API fora do ar: vale o preço normal (mesma regra do IngressoController.ehFeriado)
                }
                long resto = TEMPO_MINIMO_MS - (System.currentTimeMillis() - inicio);
                if (resto > 0) {
                    Thread.sleep(resto);
                }
                return ContextoController.FERIADOS.nomeDoFeriado(data);
            }
        };
        consulta.setOnSucceeded(e -> abrirPagamento(data, consulta.getValue()));
        consulta.setOnFailed(e -> abrirPagamento(data, Optional.empty()));

        Thread fio = new Thread(consulta, "consulta-feriado");
        fio.setDaemon(true);
        fio.start();
    }

    private void abrirPagamento(LocalDate data, Optional<String> feriado) {
        try {
            IngressoController controle = ContextoController.CONTROLE_INGRESSO;
            double valor = controle.calcularValor(TipoIngresso.DIARIA, feriado.isPresent());
            String item = "Ingresso day use";
            if (feriado.isPresent()) {
                item += String.format(" (feriado: %s, +%.0f%%)", feriado.get(), controle.getAcrescimoFeriadoPercentual());
            }
            // a tela de redirecionamento é só de passagem: não entra no histórico
            PagamentoController pagamento = ParkAdventure.substituirCena("view/pagamento.fxml");
            pagamento.setResumo(item, data, 1, valor);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
