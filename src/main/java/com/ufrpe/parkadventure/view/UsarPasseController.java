package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.app.Contexto;
import com.ufrpe.parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.repository.Persistencia;
import com.ufrpe.parkadventure.service.ServicoPasseAnual;
import com.ufrpe.parkadventure.service.ServicoPasseAnual.MesPasse;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.util.List;

// "Usar passe": o passe anual dá 1 entrada por mês; aqui o visitante escolhe em que dia de cada mês vai ao parque
public class UsarPasseController {

    @FXML private Label lblVisitante, lblResumo, lblStatus;
    @FXML private TableView<MesPasse> tabela;
    @FXML private TableColumn<MesPasse, String> colMes, colDia, colSituacao;
    @FXML private DatePicker dpData;
    @FXML private Button btUsar, btCancelar;

    private Visitante visitante;
    private Ingresso passe;

    @FXML
    private void initialize() {
        colMes.setCellValueFactory(c -> new SimpleStringProperty(Formatos.mesAno(c.getValue().mes())));
        colDia.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().dia() == null ? "—"
                : Formatos.data(c.getValue().dia()) + " (" + Formatos.diaSemana(c.getValue().dia()) + ")"));
        colSituacao.setCellValueFactory(c -> new SimpleStringProperty(rotulo(c.getValue().situacao())));
        tabela.setPlaceholder(new Label("Você não tem um passe anual ativo."));

        visitante = Contexto.SESSAO.getVisitanteAtual();
        if (visitante == null) {
            lblVisitante.setText("Faça login para usar o passe.");
            bloquear();
            return;
        }
        passe = Contexto.SERVICO_PASSE_ANUAL.passeAtivo(visitante);
        if (passe == null) {
            lblVisitante.setText("Passe anual de " + visitante.getNome());
            lblResumo.setText("Você ainda não tem um passe anual ativo. Compre um em “Comprar Ingressos”.");
            bloquear();
            return;
        }

        // só dá para escolher dias que o passe aceita (mês ainda livre, dentro da validade, com vaga no parque)
        dpData.setEditable(false);
        dpData.setDayCellFactory(seletor -> new DateCell() {
            @Override
            public void updateItem(LocalDate dia, boolean vazio) {
                super.updateItem(dia, vazio);
                setDisable(!vazio && dia != null && Contexto.SERVICO_PASSE_ANUAL.motivoRecusa(passe, dia) != null);
            }
        });
        btUsar.disableProperty().bind(dpData.valueProperty().isNull());
        btCancelar.disableProperty().bind(tabela.getSelectionModel().selectedItemProperty().isNull());

        atualizar();
    }

    private void bloquear() {
        dpData.setDisable(true);
        btUsar.setDisable(true);
        btCancelar.setDisable(true);
    }

    private String rotulo(ServicoPasseAnual.SituacaoMes situacao) {
        return switch (situacao) {
            case DISPONIVEL -> "Disponível: escolha o dia";
            case AGENDADO -> "Dia escolhido";
            case USADO -> "Utilizado";
            case PERDIDO -> "Não utilizado (mês encerrado)";
        };
    }

    private void atualizar() {
        lblVisitante.setText("Passe anual de " + visitante.getNome());
        List<MesPasse> meses = Contexto.SERVICO_PASSE_ANUAL.meses(passe);
        tabela.setItems(FXCollections.observableArrayList(meses));
        long escolhidos = meses.stream().filter(m -> m.dia() != null).count();
        lblResumo.setText("Válido até " + Formatos.data(passe.getDataValidade()) + "  •  "
                + escolhidos + " de " + ServicoPasseAnual.MESES_DO_PASSE + " entradas com dia escolhido");
        dpData.setValue(null);
    }

    @FXML
    private void onUsar() {
        LocalDate dia = dpData.getValue();
        String motivo = Contexto.SERVICO_PASSE_ANUAL.motivoRecusa(passe, dia);
        if (motivo != null) {
            Aviso.erro(lblStatus, motivo);
            return;
        }
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.initOwner(lblStatus.getScene().getWindow());
        confirmacao.setHeaderText("Usar o passe em " + Formatos.data(dia) + "?");
        confirmacao.setContentText("Será a sua entrada de " + Formatos.mesAno(java.time.YearMonth.from(dia))
                + ". Você ainda pode trocar o dia enquanto ele não chegar.");
        if (Aviso.mostrar(confirmacao).orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        motivo = Contexto.SERVICO_PASSE_ANUAL.usar(passe, dia);
        if (motivo != null) {
            Aviso.erro(lblStatus, motivo);
            return;
        }
        Persistencia.salvar();
        atualizar();
        Aviso.sucesso(lblStatus, "Passe agendado para " + Formatos.data(dia) + " (" + Formatos.diaSemana(dia)
                + "). Nesse dia você já pode reservar o fura-fila.");
    }

    @FXML
    private void onCancelar() {
        MesPasse linha = tabela.getSelectionModel().getSelectedItem();
        if (linha == null || linha.dia() == null) {
            Aviso.erro(lblStatus, "Selecione um mês que já tenha um dia escolhido.");
            return;
        }
        String motivo = Contexto.SERVICO_PASSE_ANUAL.cancelar(passe, linha.dia());
        if (motivo != null) {
            Aviso.erro(lblStatus, motivo);
            return;
        }
        Persistencia.salvar();
        atualizar();
        Aviso.sucesso(lblStatus, "Dia " + Formatos.data(linha.dia())
                + " liberado. Escolha outro dia para " + Formatos.mesAno(linha.mes()) + ".");
    }

    @FXML
    private void onVoltar() {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
