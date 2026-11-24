package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.app.Contexto;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

// Tela do administrador: lista, busca, edita e remove visitantes
public class VisitantesController {

    @FXML private TextField txtBusca, txtNomeCompleto, txtCpf, txtAltura, txtEmail, txtTelefone,
            txtNomeResponsavel, txtCpfResponsavel, txtContatoEmergencia, txtUsuarioLogin;
    @FXML private PasswordField txtNovaSenha;
    @FXML private DatePicker dpNascimento;
    @FXML private Label lblResultado;
    @FXML private TableView<Visitante> tabela;
    @FXML private TableColumn<Visitante, String> colUsuario, colNome, colCpf, colAltura, colIngressos, colAcesso;

    private PerfilFormulario formulario;

    @FXML
    private void initialize() {
        formulario = new PerfilFormulario(txtNomeCompleto, txtCpf, dpNascimento, txtAltura, txtEmail, txtTelefone,
                txtNomeResponsavel, txtCpfResponsavel, txtContatoEmergencia);

        colUsuario.setCellValueFactory(c -> new SimpleStringProperty(rotuloUsuario(c.getValue())));
        colNome.setCellValueFactory(c -> new SimpleStringProperty(textoOuTraco(c.getValue().getNomeCompleto())));
        colCpf.setCellValueFactory(c -> new SimpleStringProperty(textoOuTraco(c.getValue().getCpf())));
        // conta nova: altura 0 é só marcador
        colAltura.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getAltura() > 0 ? c.getValue().getAltura() + " cm" : "-"));
        colIngressos.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(Contexto.SERVICO_INGRESSO.listarPorVisitante(c.getValue().getNome()).size())));
        colAcesso.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().temCredenciais() ? "Ok" : "Sem login/senha"));
        tabela.setPlaceholder(new Label("Nenhum visitante encontrado."));

        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
            if (novo != null) {
                formulario.preencher(novo);
                txtUsuarioLogin.setText(novo.getUsuario() == null ? "" : novo.getUsuario());
                txtNovaSenha.clear();
                Aviso.limpar(lblResultado);
            }
        });
        txtBusca.textProperty().addListener((obs, antigo, novo) -> atualizarTabela());
        atualizarTabela();
    }

    @FXML
    private void onSalvar() {
        Visitante selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            Aviso.erro(lblResultado, "Selecione um visitante na tabela.");
            return;
        }
        String erro = formulario.salvarEm(selecionado);
        if (erro != null) {
            Aviso.erro(lblResultado, erro);
            return;
        }
        atualizarTabela();
        tabela.getSelectionModel().select(selecionado);
        Aviso.sucesso(lblResultado, "Dados de " + rotuloUsuario(selecionado) + " salvos.");
    }

    @FXML
    private void onSalvarAcesso() {
        Visitante selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            Aviso.erro(lblResultado, "Selecione um visitante na tabela.");
            return;
        }
        String erro = Contexto.SERVICO_PERFIL_VISITANTE.atualizarAcesso(selecionado, txtUsuarioLogin.getText(), txtNovaSenha.getText());
        if (erro != null) {
            Aviso.erro(lblResultado, erro);
            return;
        }
        txtNovaSenha.clear();
        atualizarTabela();
        tabela.getSelectionModel().select(selecionado);
        Aviso.sucesso(lblResultado, "Usuário e senha de " + rotuloUsuario(selecionado) + " salvos.");
    }

    @FXML
    private void onRemover() {
        Visitante selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            Aviso.erro(lblResultado, "Selecione um visitante na tabela.");
            return;
        }
        int ingressos = Contexto.SERVICO_INGRESSO.listarPorVisitante(selecionado.getNome()).size();
        Alert pergunta = new Alert(AlertType.CONFIRMATION);
        pergunta.setTitle("CONFIRMAÇÃO");
        pergunta.setHeaderText("Remover \"" + rotuloUsuario(selecionado) + "\"?");
        pergunta.setContentText("Os ingressos dele (" + ingressos + ") e as reservas de fura-fila também serão apagados. "
                + "Isso não pode ser desfeito.");
        if (Aviso.mostrar(pergunta).filter(botao -> botao == ButtonType.OK).isEmpty()) {
            return;
        }
        String nome = rotuloUsuario(selecionado);
        if (Contexto.SERVICO_PERFIL_VISITANTE.removerComDependencias(selecionado)) {
            formulario.limpar();
            txtUsuarioLogin.clear();
            txtNovaSenha.clear();
            atualizarTabela();
            Aviso.sucesso(lblResultado, "Visitante \"" + nome + "\" removido.");
        } else {
            Aviso.erro(lblResultado, "Não foi possível remover o visitante.");
        }
    }

    @FXML
    private void onVoltar() {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void atualizarTabela() {
        tabela.setItems(FXCollections.observableArrayList(Contexto.SERVICO_PERFIL_VISITANTE.buscarPorTrecho(txtBusca.getText())));
    }

    private String rotuloUsuario(Visitante v) {
        boolean semLogin = v.getUsuario() == null || v.getUsuario().isBlank();
        return Contexto.SERVICO_PERFIL_VISITANTE.usuarioOuNome(v) + (semLogin ? " (sem login)" : "");
    }

    private String textoOuTraco(String texto) {
        return texto == null || texto.isBlank() ? "-" : texto;
    }
}
