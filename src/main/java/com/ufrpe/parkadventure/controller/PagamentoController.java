package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.Period;

public class PagamentoController {

    @FXML private Label lblItem, lblDataVisita, lblQuantidade, lblTotal;

    @FXML private TextField txtNome, txtCpf, txtEmail, txtTelefone;
    @FXML private DatePicker dpNascimento;
    @FXML private Spinner<Integer> spAltura;
    @FXML private Button btnFotoDocumento, btnVoltar, btnPagar;
    @FXML private Label lblArquivoDocumento;
    @FXML private ImageView ivDocumento;

    @FXML private TitledPane paneResponsavel;
    @FXML private TextField txtNomeResponsavel, txtCpfResponsavel, txtContatoEmergencia;
    @FXML private CheckBox chkAutorizacao;

    @FXML private TextField txtCep;
    @FXML private ComboBox<String> cbComoConheceu;
    @FXML private CheckBox chkPromoEmail, chkPromoWhatsapp;

    @FXML private ToggleGroup grupoPagamento;
    @FXML private RadioButton rbPix, rbCartao, rbBoleto;

    private File arquivoDocumento;
    private double total;

    @FXML
    private void initialize() {
        spAltura.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(50, 250, 150));

        cbComoConheceu.getItems().addAll(
                "Instagram", "Indicação de amigos", "Google",
                "TV ou rádio", "Já conhecia", "Outro");

        paneResponsavel.setVisible(false);
        paneResponsavel.setManaged(false);

        dpNascimento.valueProperty().addListener((obs, antigo, novo) -> {
            boolean menor = novo != null
                    && Period.between(novo, LocalDate.now()).getYears() < 18;
            paneResponsavel.setVisible(menor);
            paneResponsavel.setManaged(menor);
        });

        btnPagar.disableProperty().bind(
                grupoPagamento.selectedToggleProperty().isNull());
    }

    public void setResumo(String item, LocalDate data, int quantidade, double total) {
        this.total = total;
        lblItem.setText(item);
        lblDataVisita.setText(data.toString());
        lblQuantidade.setText(String.valueOf(quantidade));
        lblTotal.setText(String.format("R$ %.2f", total));
    }

    @FXML
    private void aoEscolherDocumento() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Foto do documento");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg"));

        Stage stage = (Stage) btnFotoDocumento.getScene().getWindow();
        File escolhido = fc.showOpenDialog(stage);

        if (escolhido != null) {
            arquivoDocumento = escolhido;
            lblArquivoDocumento.setText(escolhido.getName());
            ivDocumento.setImage(new Image(escolhido.toURI().toString()));
        }
    }

    @FXML
    private void aoPagar() {

        String erro = validar();
        if (erro != null) {
            new Alert(Alert.AlertType.WARNING, erro).showAndWait();
            return;
        }

        try {
            Stage stage = (Stage) btnPagar.getScene().getWindow();
            Toggle escolhido = grupoPagamento.getSelectedToggle();

            if (escolhido == rbPix) {
                FXMLLoader loader = new FXMLLoader(
                        ParkAdventure.class.getResource("view/pagamento-pix.fxml"));
                Scene scene = new Scene(loader.load());
                PagamentoPixController pix = loader.getController();
                pix.setValor(total);
                stage.setScene(scene);

            } else if (escolhido == rbCartao) {
                FXMLLoader loader = new FXMLLoader(
                        ParkAdventure.class.getResource("view/pagamento-cartao.fxml"));
                Scene scene = new Scene(loader.load());
                PagamentoCartaoController cartao = loader.getController();
                cartao.setValor(total);
                stage.setScene(scene);

            } else if (escolhido == rbBoleto) {
                FXMLLoader loader = new FXMLLoader(
                        ParkAdventure.class.getResource("view/pagamento-boleto.fxml"));
                Scene scene = new Scene(loader.load());
                PagamentoBoletoController boleto = loader.getController();
                boleto.setValor(total);
                stage.setScene(scene);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void aoVoltar() {
    }

    private String validar() {
        if (txtNome.getText().isBlank()) return "Informe o nome completo.";
        if (txtCpf.getText().replaceAll("\\D", "").length() != 11) return "CPF inválido.";
        if (dpNascimento.getValue() == null) return "Informe a data de nascimento.";
        if (!txtEmail.getText().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) return "E-mail inválido.";
        if (txtTelefone.getText().replaceAll("\\D", "").length() < 10) return "Telefone inválido.";
        if (arquivoDocumento == null) return "Anexe a foto do documento.";

        if (paneResponsavel.isVisible()) {
            if (txtNomeResponsavel.getText().isBlank()) return "Informe o nome do responsável.";
            if (txtCpfResponsavel.getText().replaceAll("\\D", "").length() != 11) return "CPF do responsável inválido.";
            if (txtContatoEmergencia.getText().isBlank()) return "Informe o contato de emergência.";
            if (!chkAutorizacao.isSelected()) return "É necessária a autorização do responsável.";
        }
        return null;
    }
}