package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.app.Contexto;
import com.ufrpe.parkadventure.app.PedidoAtual;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.ingresso.TipoIngresso;
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
    @FXML private Label lblDetalhePasse, lblDetalhePasseLabel;

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

        preencherResumo();
        preencherComDadosDaConta();
    }

    private void preencherResumo() {
        TipoIngresso tipo = PedidoAtual.getTipoIngresso();
        if (tipo == TipoIngresso.PASSE_ANUAL) {
            double preco = Contexto.SERVICO_EMISSAO_INGRESSO.calcularValor(TipoIngresso.PASSE_ANUAL, false);
            setResumo("Passe Anual", PedidoAtual.getDataVisita(), 1, preco);
            lblDetalhePasseLabel.setVisible(true);
            lblDetalhePasse.setVisible(true);
            lblDetalhePasse.setText("12 entradas (1 por mês) • Validade: 1 ano • Acesso a todas as atrações");
            lblDataVisita.setText("Não requer data");
        } else {
            lblDetalhePasseLabel.setVisible(false);
            lblDetalhePasse.setVisible(false);
        }
    }

    // A conta (usuário e senha) já identifica quem compra. Nome e CPF são informados na PRIMEIRA compra; depois
    // disso o formulário já vem preenchido e eles NÃO podem mais ser alterados (campos bloqueados).
    private void preencherComDadosDaConta() {
        Visitante conta = Contexto.SESSAO.getVisitanteAtual();
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
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Verifique os dados");
            alert.setContentText(erro);
            alert.initOwner(btnPagar.getScene().getWindow());
            Aviso.mostrar(alert);
            return;
        }

        // confere a capacidade do dia da visita ANTES de ir para o pagamento
        LocalDate diaVisita = PedidoAtual.getDataVisita();
        if (Contexto.SERVICO_CAPACIDADE.estaEsgotado(diaVisita)) {
            Alert esgotado = new Alert(Alert.AlertType.WARNING);
            esgotado.setHeaderText("Ingressos esgotados para hoje");
            esgotado.setContentText("Infelizmente não restam vagas para hoje.\n\nVocê pode:\n• Escolher uma data futura (Ingresso programado)\n• Comprar um Passe Anual para entrar em outro dia\n\nDesculpe o inconveniente!");
            esgotado.initOwner(btnPagar.getScene().getWindow());
            Aviso.mostrar(esgotado);
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
        if (txtNome.getText().isBlank()) return "Precisamos do seu nome completo para emitir o ingresso.";
        if (txtCpf.getText().replaceAll("\\D", "").length() != 11) return "O CPF precisa ter 11 dígitos. Verifique e tente novamente.";
        if (dpNascimento.getValue() == null) return "Informe sua data de nascimento para continuar.";
        if (dpNascimento.getValue().isAfter(LocalDate.now())) return "A data de nascimento não pode ser no futuro.";
        if (!txtEmail.getText().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) return "O e-mail informado parece inválido. Verifique e tente novamente.";
        if (txtTelefone.getText().replaceAll("\\D", "").length() < 10) return "Informe um telefone válido com DDD.";
        if (arquivoDocumento == null) return "Anexe uma foto do seu documento com foto para finalizar a compra.";

        if (paneResponsavel.isVisible()) {
            if (txtNomeResponsavel.getText().isBlank()) return "Informe o nome do responsável para liberar a entrada do menor.";
            if (txtCpfResponsavel.getText().replaceAll("\\D", "").length() != 11) return "O CPF do responsável precisa ter 11 dígitos.";
            if (txtContatoEmergencia.getText().isBlank()) return "Informe um contato de emergência do responsável.";
            if (!chkAutorizacao.isSelected()) return "O responsável precisa autorizar a entrada do menor. Marque a caixa de autorização.";
        }
        return null;
    }
}