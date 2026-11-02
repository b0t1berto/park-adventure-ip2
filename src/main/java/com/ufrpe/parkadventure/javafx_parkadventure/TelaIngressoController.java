package com.ufrpe.parkadventure.javafx_parkadventure;

import com.ufrpe.parkadventure.javafx_parkadventure.controller.Contexto;
import com.ufrpe.parkadventure.javafx_parkadventure.controller.Navegador;
import com.ufrpe.parkadventure.javafx_parkadventure.controller.Navegador.Tela;
import com.ufrpe.parkadventure.javafx_parkadventure.controller.Tarefas;
import com.ufrpe.parkadventure.javafx_parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.javafx_parkadventure.model.ingresso.TipoIngresso;
import com.ufrpe.parkadventure.javafx_parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.javafx_parkadventure.view.Aviso;
import com.ufrpe.parkadventure.javafx_parkadventure.view.Formatos;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.Optional;

public class TelaIngressoController {
    @FXML private Label lblVisitante, lblAvisoFeriado, lblValor, lblValidade, lblEmitidos, lblStatus;
    @FXML private ComboBox<TipoIngresso> cbTipo;
    @FXML private DatePicker dpData;
    @FXML private Button btnEmitir;

    private Visitante visitante;
    private boolean feriadoNaData;  
    private int consultaAtual;   

    @FXML
    private void initialize() {
        visitante = Contexto.SESSAO.getVisitanteAtual();
        lblAvisoFeriado.managedProperty().bind(lblAvisoFeriado.visibleProperty());
        lblAvisoFeriado.setVisible(false);

        if (visitante == null) {
            lblVisitante.setText("Escolha um visitante na caixa do topo da janela.");
            cbTipo.setDisable(true);
            dpData.setDisable(true);
            btnEmitir.setDisable(true);
            return;
        }
        lblVisitante.setText("Visitante: " + visitante.getNome() + "  •  " + visitante.getIdade()
                + " anos  •  " + visitante.getAltura() + " cm");

        cbTipo.getItems().setAll(TipoIngresso.values());
        cbTipo.setConverter(new StringConverter<>() {
            @Override public String toString(TipoIngresso tipo) {
                return tipo == null ? "" : Formatos.tipoIngresso(tipo);
            }
            @Override public TipoIngresso fromString(String texto) {
                return null;
            }
        });
        cbTipo.setValue(TipoIngresso.DIARIA);
        dpData.setValue(LocalDate.now());
        dpData.setDayCellFactory(seletor -> new DateCell() {
            @Override
            public void updateItem(LocalDate dia, boolean vazio) {
                super.updateItem(dia, vazio);
                setDisable(!vazio && dia.isBefore(LocalDate.now()));
            }
        });

        cbTipo.valueProperty().addListener((observado, antigo, novo) -> atualizarResumo());
        dpData.valueProperty().addListener((observado, antigo, novo) -> atualizarResumo());
        atualizarResumo();
    }

    private void atualizarResumo() {
        TipoIngresso tipo = cbTipo.getValue();
        LocalDate data = dpData.getValue();
        consultaAtual++;   
        feriadoNaData = false;
        lblAvisoFeriado.setVisible(false);
        Aviso.limpar(lblStatus);

        if (tipo == null || data == null) {
            btnEmitir.setDisable(true);
            return;
        }
        mostrarPreco(tipo, data);
        lblEmitidos.setText("Ingressos já emitidos em " + Formatos.data(data) + ": "
                + Contexto.INGRESSOS.consultarQuantidadePorData(data));

        if (tipo != TipoIngresso.DIARIA) { 
            btnEmitir.setDisable(false);
            return;
        }
        consultarFeriado(data);
    }

    private void consultarFeriado(LocalDate data) {
        int ano = data.getYear();
        if (Contexto.FERIADOS.anoCarregado(ano)) {
            aplicarFeriado(data);
            btnEmitir.setDisable(false);
            return;
        }

        btnEmitir.setDisable(true);
        Aviso.info(lblStatus, "Consultando feriados na BrasilAPI…");
        int minhaConsulta = consultaAtual;

        Task<Void> tarefa = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Contexto.FERIADOS.carregarAno(ano);
                return null;
            }
        };
        tarefa.setOnSucceeded(evento -> {
            if (minhaConsulta != consultaAtual) {
                return;
            }
            Aviso.limpar(lblStatus);
            aplicarFeriado(data);
            btnEmitir.setDisable(false);
        });
        tarefa.setOnFailed(evento -> {
            if (minhaConsulta != consultaAtual) {
                return;
            }
            Aviso.erro(lblStatus, "Não foi possível consultar os feriados. O valor será cobrado sem acréscimo.");
            btnEmitir.setDisable(false);
        });
        Tarefas.executar(tarefa);
    }
    private void aplicarFeriado(LocalDate data) {
        Optional<String> nome = Contexto.FERIADOS.nomeDoFeriado(data);
        feriadoNaData = nome.isPresent();
        if (feriadoNaData) {
            double percentual = Contexto.CONTROLE_INGRESSO.getAcrescimoFeriadoPercentual();
            lblAvisoFeriado.setText("Atenção: " + Formatos.data(data) + " é feriado nacional (" + nome.get()
                    + "). A diária tem acréscimo de " + (int) percentual + "%.");
            lblAvisoFeriado.setVisible(true);
        }
        mostrarPreco(TipoIngresso.DIARIA, data);
    }

    private void mostrarPreco(TipoIngresso tipo, LocalDate data) {
        double valor = Contexto.CONTROLE_INGRESSO.calcularValor(tipo, tipo == TipoIngresso.DIARIA && feriadoNaData);
        lblValor.setText(Formatos.moeda(valor));
        lblValidade.setText(tipo == TipoIngresso.PASSE_ANUAL
                ? "Válido de " + Formatos.data(data) + " até " + Formatos.data(data.plusYears(1))
                : "Válido somente em " + Formatos.data(data));
    }

    @FXML
    private void onEmitir() {
        TipoIngresso tipo = cbTipo.getValue();
        LocalDate data = dpData.getValue();
        if (visitante == null || tipo == null || data == null) {
            return;
        }
        boolean cobrarFeriado = tipo == TipoIngresso.DIARIA && feriadoNaData;
        Ingresso ingresso = Contexto.CONTROLE_INGRESSO.emitirIngresso(visitante, tipo, data, cobrarFeriado);

        Aviso.sucesso(lblStatus, "Ingresso " + Formatos.tipoIngresso(tipo).toLowerCase() + " emitido para "
                + visitante.getNome() + " por " + Formatos.moeda(ingresso.getValor())
                + ", válido até " + Formatos.data(ingresso.getDataValidade()) + ".");
        lblEmitidos.setText("Ingressos já emitidos em " + Formatos.data(data) + ": "
                + Contexto.INGRESSOS.consultarQuantidadePorData(data));
    }

    @FXML
    private void onVerIngressosComprados() {
        Navegador.ir(Tela.INGRESSOS_COMPRADOS);
    }
}