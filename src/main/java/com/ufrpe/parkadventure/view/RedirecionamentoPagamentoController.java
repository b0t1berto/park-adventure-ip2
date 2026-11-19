package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.app.Contexto;
import com.ufrpe.parkadventure.app.PedidoAtual;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.view.controles.Tarefas;
import com.ufrpe.parkadventure.model.ingresso.TipoIngresso;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

// Tela de passagem: enquanto mostra a mensagem, descobre o preço do ingresso (a diária sobe em feriado
// nacional) e só depois abre o pagamento. Antes o valor era fixo em R$ 120,00 para qualquer data.
public class RedirecionamentoPagamentoController {

    @FXML
    private Label lblMensagem;

    private boolean pausaTerminou;
    private boolean precoPronto;
    private boolean avancou;
    private boolean feriado;
    private Optional<String> nomeFeriado = Optional.empty();
    private double preco = Contexto.SERVICO_EMISSAO_INGRESSO.calcularValor(TipoIngresso.DIARIA, false);

    @FXML
    public void initialize() {
        LocalDate data = PedidoAtual.getDataVisita();

        PauseTransition pausa = new PauseTransition(Duration.seconds(2));
        pausa.setOnFinished(e -> {
            pausaTerminou = true;
            tentarAvancar();
        });
        pausa.play();

        // a consulta à BrasilAPI é lenta: roda em segundo plano (a janela não congela)
        Task<Boolean> consulta = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                Contexto.FERIADOS.carregarAno(data.getYear());
                return Contexto.FERIADOS.nomeDoFeriado(data).isPresent();
            }
        };
        consulta.setOnSucceeded(e -> {
            feriado = Boolean.TRUE.equals(consulta.getValue());
            nomeFeriado = Contexto.FERIADOS.nomeDoFeriado(data);
            preco = Contexto.SERVICO_EMISSAO_INGRESSO.calcularValor(TipoIngresso.DIARIA, feriado);
            precoPronto = true;
            tentarAvancar();
        });
        consulta.setOnFailed(e -> {
            // sem internet: cobra o preço normal (mesmo comportamento de IngressoController.ehFeriado)
            precoPronto = true;
            tentarAvancar();
        });
        Tarefas.executar(consulta);
    }

    private void tentarAvancar() {
        if (avancou || !pausaTerminou || !precoPronto) {
            return;
        }
        avancou = true;

        // alerta de feriado antes de prosseguir para o pagamento
        if (feriado) {
            Stage stage = (Stage) lblMensagem.getScene().getWindow();
            String motivo = nomeFeriado.orElse("feriado nacional");
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.initOwner(stage);
            alerta.setHeaderText("Preço especial em feriado");
            alerta.setContentText(
                "A data escolhida coincide com " + motivo + ". O ingresso terá um acréscimo de "
                + (int) Contexto.SERVICO_EMISSAO_INGRESSO.getAcrescimoFeriadoPercentual() + "% no valor.");
            alerta.showAndWait();
        }

        try {
            // a tela de redirecionamento é só de passagem: não entra no histórico
            PagamentoController pagamento = ParkAdventure.substituirCena("view/pagamento.fxml");
            pagamento.setResumo(feriado ? "Ingresso day use (feriado)" : "Ingresso day use",
                    PedidoAtual.getDataVisita(), 1, preco);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
