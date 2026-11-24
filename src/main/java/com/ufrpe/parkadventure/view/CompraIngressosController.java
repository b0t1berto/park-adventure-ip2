package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.app.Contexto;
import com.ufrpe.parkadventure.app.PedidoAtual;
import com.ufrpe.parkadventure.model.ingresso.TipoIngresso;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.time.LocalDate;

public class CompraIngressosController {

    @FXML private Button btnInstantaneo;
    @FXML private Button btnProgramado;
    @FXML private Button btnPasseAnual;
    @FXML private Label lblVagas;

    @FXML
    private void initialize() {
        double precoDiaria = Contexto.SERVICO_EMISSAO_INGRESSO.calcularValor(TipoIngresso.DIARIA, false);
        double precoAnual = Contexto.SERVICO_EMISSAO_INGRESSO.calcularValor(TipoIngresso.PASSE_ANUAL, false);
        String moedaDiaria = Formatos.moeda(precoDiaria);
        String moedaAnual = Formatos.moeda(precoAnual);
        btnInstantaneo.setText("Ingresso instantâneo\n" + moedaDiaria);
        btnProgramado.setText("Ingresso programado\n" + moedaDiaria);
        btnPasseAnual.setText("Passe Anual — 12 entradas (1 por mês)\n" + moedaAnual);
        atualizarAvisoDeVagas();
    }

    private void atualizarAvisoDeVagas() {
        LocalDate hoje = LocalDate.now();
        int vagas = Contexto.SERVICO_CAPACIDADE.vagasRestantes(hoje);
        int capacidade = Contexto.SERVICO_CAPACIDADE.capacidadeMaximaDiaria();
        if (vagas == 0) {
            lblVagas.setText("⚠ Ingressos esgotados para hoje — tente o Passe Anual ou escolha uma data futura.");
            lblVagas.getStyleClass().add("esgotado");
        } else if (vagas <= capacidade * 0.1) {
            // menos de 10% das vagas restantes
            lblVagas.setText("⚠ Restam apenas " + vagas + " vagas para hoje — garanta o seu!");
            lblVagas.getStyleClass().add("critico");
        } else {
            lblVagas.setText("✓ " + vagas + " vagas disponíveis para hoje.");
        }
    }

    @FXML
    private void onIngressoProgramado(ActionEvent event) throws IOException {
        try {
            PedidoAtual.setDataVisita(null);   // volta para "hoje" (null = hoje)
            PedidoAtual.setTipoIngresso(TipoIngresso.DIARIA);
            ParkAdventure.proximaCena("view/escolha-data.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void onIngressoInstantaneo(ActionEvent event) throws IOException {
        // capacidade do dia atingida: não vende ingresso para hoje
        LocalDate hoje = LocalDate.now();
        if (Contexto.SERVICO_CAPACIDADE.estaEsgotado(hoje)) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setHeaderText("Ingressos esgotados para hoje");
            alerta.setContentText("Infelizmente não restam vagas para hoje.\n\nVocê pode:\n• Escolher uma data futura (Ingresso programado)\n• Comprar um Passe Anual para entrar em outro dia\n\nDesculpe o inconveniente!");
            alerta.initOwner(((Node) event.getSource()).getScene().getWindow());
            Aviso.mostrar(alerta);
            return;
        }
        try {
            // ingresso instantâneo é sempre para hoje (a data de uma tentativa "programada" anterior não vale)
            PedidoAtual.setDataVisita(null);
            PedidoAtual.setTipoIngresso(TipoIngresso.DIARIA);
            ParkAdventure.proximaCena("view/redirecionamento-pagamento-ingresso.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onPasseAnual(ActionEvent event) throws IOException {
        // Verifica se o visitante já comprou um passe anual neste mês
        Visitante visitante = Contexto.SESSAO.getVisitanteAtual();
        if (visitante != null && !Contexto.SERVICO_EMISSAO_INGRESSO.podeComprarPasseAnual(visitante)) {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.initOwner(((Node) event.getSource()).getScene().getWindow());
            alerta.setHeaderText("Você já possui o Passe Anual");
            alerta.setContentText("Você já tem um passe anual ativo.\n\nUse o botão 'Usar passe' no menu para escolher o dia de cada mês.");
            Aviso.mostrar(alerta);
            return;
        }
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.initOwner(((Node) event.getSource()).getScene().getWindow());
        alerta.setHeaderText("Passe Anual");
        alerta.setContentText("O passe anual inclui 12 entradas, 1 por mês. Em 'Usar passe' você escolhe, mês a mês, o dia da visita. Deseja continuar?");
        Aviso.mostrar(alerta).ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    PedidoAtual.setDataVisita(null);
                    PedidoAtual.setTipoIngresso(TipoIngresso.PASSE_ANUAL);
                    ParkAdventure.proximaCena("view/pagamento.fxml");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML 
    void onVoltar(ActionEvent event) {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
