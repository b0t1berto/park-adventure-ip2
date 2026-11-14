package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.pessoa.Responsavel;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;

public class PagamentoController {

    private Scene cenaAnterior;

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

        preencherComDadosDaConta();
    }

    // A conta (usuário e senha) já identifica quem compra. Nome e CPF são informados na PRIMEIRA compra; depois
    // disso o formulário já vem preenchido e eles NÃO podem mais ser alterados (campos bloqueados).
    private void preencherComDadosDaConta() {
        Visitante conta = ContextoController.SESSAO.getVisitanteAtual();
        if (conta == null) {
            return;
        }
        if (conta.getNomeCompleto() != null && !conta.getNomeCompleto().isBlank()) {
            txtNome.setText(conta.getNomeCompleto());
            txtNome.setEditable(false);
        }
        if (conta.getCpf() != null && !conta.getCpf().isBlank()) {
            txtCpf.setText(conta.getCpf());
            txtCpf.setEditable(false);
        }
        if (conta.getEmail() != null) txtEmail.setText(conta.getEmail());
        if (conta.getTelefone() != null) txtTelefone.setText(conta.getTelefone());

        // conta recém-criada tem altura 0 e nascimento 01/01/2000 só como marcadores: aí a pessoa preenche
        if (conta.getAltura() > 0) {
            spAltura.getValueFactory().setValue(Math.max(50, Math.min(250, conta.getAltura())));
            dpNascimento.setValue(conta.getDataNascimento());
        }
        Responsavel resp = conta.getResponsavel();
        if (resp != null) {
            if (resp.getNome() != null) txtNomeResponsavel.setText(resp.getNome());
            if (resp.getCpf() != null) txtCpfResponsavel.setText(resp.getCpf());
            if (resp.getContatoEmergencia() != null) txtContatoEmergencia.setText(resp.getContatoEmergencia());
        }
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

        boolean menor = paneResponsavel.isVisible();
        PedidoAtual.definirComprador(
                txtNome.getText(), txtCpf.getText(), dpNascimento.getValue(), spAltura.getValue(),
                txtEmail.getText(), txtTelefone.getText(),
                menor ? txtNomeResponsavel.getText() : null,
                menor ? txtCpfResponsavel.getText() : null,
                menor ? txtContatoEmergencia.getText() : null);

        try {
            Scene formulario = btnPagar.getScene();   // o "Voltar" das próximas telas devolve este formulário como está
            Toggle escolhido = grupoPagamento.getSelectedToggle();

            if (escolhido == rbPix) {
                PagamentoPixController pix = ParkAdventure.substituirCena("view/pagamento-pix.fxml");
                pix.setCenaAnterior(formulario);
                pix.setValor(total);

            } else if (escolhido == rbCartao) {
                PagamentoCartaoController cartao = ParkAdventure.substituirCena("view/pagamento-cartao.fxml");
                cartao.setCenaAnterior(formulario);
                cartao.setValor(total);

            } else if (escolhido == rbBoleto) {
                PagamentoBoletoController boleto = ParkAdventure.substituirCena("view/pagamento-boleto.fxml");
                boleto.setCenaAnterior(formulario);
                boleto.setValor(total);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void aoVoltar() {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String validar() {
        if (txtNome.getText().isBlank()) return "Informe o nome completo.";
        if (txtCpf.getText().replaceAll("\\D", "").length() != 11) return "CPF inválido.";
        if (dpNascimento.getValue() == null) return "Informe a data de nascimento.";
        if (dpNascimento.getValue().isAfter(LocalDate.now())) return "A data de nascimento não pode ser no futuro.";
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