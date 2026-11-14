package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.repository.Persistencia;
import com.ufrpe.parkadventure.view.Aviso;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

// Tela do administrador: cadastra, altera e remove os operadores das atrações (nome, usuário e senha).
// Cada atração tem vários operadores, que se revezam nos turnos da escala (ver GeradorEscala).
public class OperadoresController {

    private record Linha(Atracao atracao, Operador operador) { }

    @FXML private TableView<Linha> tabela;
    @FXML private TableColumn<Linha, String> colAtracao, colNome, colUsuario, colCpf, colTurnos;
    @FXML private ComboBox<Atracao> cbAtracao;
    @FXML private TextField txtNome, txtCpf, txtUsuario;
    @FXML private PasswordField txtSenha;
    @FXML private Label lblStatus;

    // operador que está sendo editado; null = o formulário cadastra um operador novo
    private Operador emEdicao;

    @FXML
    private void initialize() {
        colAtracao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().atracao().getNome()));
        colNome.setCellValueFactory(c -> new SimpleStringProperty(texto(c.getValue().operador().getNome())));
        colUsuario.setCellValueFactory(c -> new SimpleStringProperty(texto(c.getValue().operador().getUsuario())));
        colCpf.setCellValueFactory(c -> new SimpleStringProperty(texto(c.getValue().operador().getCpf())));
        colTurnos.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(RepositorioEscalaDeTrabalhoController.contarTurnos(c.getValue().operador()))));
        tabela.setPlaceholder(new Label("Nenhuma atração cadastrada."));

        cbAtracao.setConverter(new StringConverter<>() {
            @Override public String toString(Atracao a) { return a == null ? "" : a.getNome(); }
            @Override public Atracao fromString(String s) { return null; }
        });
        cbAtracao.setItems(FXCollections.observableArrayList(RepositorioAtracaoController.listarTodas()));

        // escolher uma linha edita aquele operador
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antiga, nova) -> {
            if (nova != null) {
                editar(nova);
            }
        });
        atualizarTabela();
    }

    private void atualizarTabela() {
        List<Linha> linhas = new ArrayList<>();
        for (Atracao atracao : RepositorioAtracaoController.listarTodas()) {
            for (Operador operador : atracao.getOperadores()) {
                linhas.add(new Linha(atracao, operador));
            }
        }
        tabela.setItems(FXCollections.observableArrayList(linhas));
    }

    private void editar(Linha linha) {
        emEdicao = linha.operador();
        cbAtracao.setValue(linha.atracao());
        cbAtracao.setDisable(true);   // um operador não muda de atração; para isso, remova e cadastre de novo
        txtNome.setText(texto(emEdicao.getNome()));
        txtCpf.setText(texto(emEdicao.getCpf()));
        txtUsuario.setText(texto(emEdicao.getUsuario()));
        txtSenha.setText("");
    }

    @FXML
    private void onNovo() {
        emEdicao = null;
        tabela.getSelectionModel().clearSelection();
        cbAtracao.setDisable(false);
        cbAtracao.setValue(null);
        txtNome.setText("");
        txtCpf.setText("");
        txtUsuario.setText("");
        txtSenha.setText("");
        Aviso.limpar(lblStatus);
    }

    @FXML
    private void onSalvar() {
        Atracao atracao = cbAtracao.getValue();
        if (atracao == null) {
            Aviso.erro(lblStatus, "Escolha a atração do operador.");
            return;
        }
        String nome = txtNome.getText().trim();
        String cpf = txtCpf.getText().replaceAll("\\D", "");
        String usuario = txtUsuario.getText().trim();
        String senha = txtSenha.getText();
        if (nome.isBlank()) {
            Aviso.erro(lblStatus, "Informe o nome do operador.");
            return;
        }
        if (!cpf.isEmpty() && cpf.length() != 11) {
            Aviso.erro(lblStatus, "O CPF deve ter 11 dígitos (ou ficar em branco).");
            return;
        }
        // o nome identifica o operador dentro da atração (é como o arquivo de dados o encontra)
        Operador mesmoNome = RepositorioAtracaoController.buscarOperadorPorNome(nome, atracao.getNome());
        if (mesmoNome != null && mesmoNome != emEdicao) {
            Aviso.erro(lblStatus, "Já existe um operador chamado \"" + nome + "\" em " + atracao.getNome() + ".");
            return;
        }

        boolean novo = emEdicao == null;
        // senha em branco só vale ao editar um operador que já tem login (mantém a senha atual)
        boolean manterSenha = !novo && senha.isEmpty() && emEdicao.temCredenciais();
        String problema = AutenticacaoController.validarCredenciais(usuario,
                manterSenha ? "x".repeat(AutenticacaoController.TAMANHO_MINIMO_SENHA) : senha);
        if (problema != null) {
            Aviso.erro(lblStatus, problema);
            return;
        }
        if (!AutenticacaoController.usuarioDisponivel(usuario, emEdicao)) {
            Aviso.erro(lblStatus, "O usuário \"" + usuario + "\" já está em uso.");
            return;
        }

        Operador operador = novo ? new Operador(nome, "01/01/1990", null, atracao.getNome()) : emEdicao;
        operador.setNome(nome);
        operador.setCpf(cpf.isEmpty() ? null : cpf);
        operador.setCargo(atracao.getNome());
        operador.setUsuario(usuario);
        if (!manterSenha) {
            operador.definirSenha(senha);
        }
        if (novo) {
            RepositorioAtracaoController.cadastrarOperador(atracao.getNome(), operador);
            GeradorEscala.gerarProximosDias();   // o novo operador já entra no rodízio dos próximos dias
        }
        Persistencia.salvar();
        atualizarTabela();
        Aviso.sucesso(lblStatus, "Operador " + nome + " salvo em " + atracao.getNome() + " (usuário: " + usuario + ").");
        onNovoSemMensagem();
    }

    @FXML
    private void onRemover() {
        if (emEdicao == null) {
            Aviso.erro(lblStatus, "Selecione na tabela o operador que será removido.");
            return;
        }
        Atracao atracao = RepositorioAtracaoController.buscarAtracaoPorOperador(emEdicao);
        if (atracao == null) {
            return;
        }
        if (atracao.getOperadores().size() <= GeradorEscala.MIN_OPERADORES_POR_ATRACAO) {
            Aviso.erro(lblStatus, atracao.getNome() + " precisa de pelo menos " + GeradorEscala.MIN_OPERADORES_POR_ATRACAO
                    + " operadores para cobrir manhã e tarde todos os dias. Cadastre outro antes de remover este.");
            return;
        }
        Alert confirmacao = new Alert(AlertType.CONFIRMATION,
                "Remover " + emEdicao.getNome() + " de " + atracao.getNome() + "? Os turnos dele serão redistribuídos.");
        if (confirmacao.showAndWait().filter(botao -> botao == ButtonType.OK).isEmpty()) {
            return;
        }
        String nome = emEdicao.getNome();
        atracao.removerOperador(emEdicao);
        RepositorioEscalaDeTrabalhoController.removerDoOperador(emEdicao);
        GeradorEscala.gerarProximosDias();   // redistribui os turnos que ficaram vagos
        Persistencia.salvar();
        atualizarTabela();
        onNovoSemMensagem();
        Aviso.sucesso(lblStatus, "Operador " + nome + " removido.");
    }

    // limpa o formulário sem apagar a mensagem de sucesso/erro
    private void onNovoSemMensagem() {
        emEdicao = null;
        tabela.getSelectionModel().clearSelection();
        cbAtracao.setDisable(false);
        cbAtracao.setValue(null);
        txtNome.setText("");
        txtCpf.setText("");
        txtUsuario.setText("");
        txtSenha.setText("");
    }

    @FXML
    private void onVoltar() {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String texto(String valor) {
        return valor == null ? "" : valor;
    }
}
