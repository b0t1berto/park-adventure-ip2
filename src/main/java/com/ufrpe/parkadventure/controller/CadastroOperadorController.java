package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.repository.Persistencia;
import com.ufrpe.parkadventure.view.Aviso;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

// Tela do administrador: cadastra o operador (usuário + senha) de uma atração que ainda não tem um
public class CadastroOperadorController {

    private static final int TAMANHO_MINIMO_SENHA = 4;

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtSenha;
    @FXML private ComboBox<Atracao> cbAtracao;
    @FXML private Label lblResultado;
    @FXML private TableView<Atracao> tabela;
    @FXML private TableColumn<Atracao, String> colUsuario, colAtracao;

    @FXML
    private void initialize() {
        cbAtracao.setConverter(new StringConverter<>() {
            @Override public String toString(Atracao atracao) {
                return atracao == null ? "" : atracao.getNome();
            }
            @Override public Atracao fromString(String texto) {
                return null;
            }
        });
        colUsuario.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOperador().getNome()));
        colAtracao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        tabela.setPlaceholder(new Label("Nenhum operador cadastrado."));
        atualizar();
    }

    @FXML
    private void onCadastrar() {
        String usuario = txtUsuario.getText().trim();
        String senha = txtSenha.getText();
        Atracao atracao = cbAtracao.getValue();

        if (usuario.isEmpty() || senha.length() < TAMANHO_MINIMO_SENHA) {
            Aviso.erro(lblResultado, "Informe o usuário e uma senha com pelo menos "
                    + TAMANHO_MINIMO_SENHA + " caracteres.");
            return;
        }
        if (atracao == null) {
            Aviso.erro(lblResultado, "Escolha a atração do operador.");
            return;
        }
        // o usuário é único no sistema: administrador, visitantes e operadores entram pelo mesmo login
        if (usuario.equalsIgnoreCase(TelaLoginController.USUARIO_ADM)
                || RepositorioVisitanteController.buscarVisitante(usuario) != null
                || RepositorioAtracaoController.buscarOperadorPorUsuario(usuario) != null) {
            Aviso.erro(lblResultado, "Esse usuário já está em uso. Escolha outro.");
            return;
        }

        Operador operador = new Operador(usuario, "01/01/1990", null, atracao.getNome());
        operador.setSenha(senha);
        if (!RepositorioAtracaoController.cadastrarOperador(atracao.getNome(), operador)) {
            Aviso.erro(lblResultado, "Não foi possível cadastrar o operador.");
            return;
        }
        Persistencia.salvar();
        atualizar();
        txtUsuario.clear();
        txtSenha.clear();
        Aviso.sucesso(lblResultado, "Operador \"" + usuario + "\" cadastrado em \"" + atracao.getNome()
                + "\". A atração já está aberta aos visitantes.");
    }

    @FXML
    private void onVoltar() {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Só atrações SEM operador aparecem para escolher; a tabela mostra as que já têm
    private void atualizar() {
        List<Atracao> semOperador = new ArrayList<>();
        List<Atracao> comOperador = new ArrayList<>();
        for (Atracao atracao : RepositorioAtracaoController.listarTodas()) {
            if (atracao.temOperador()) {
                comOperador.add(atracao);
            } else {
                semOperador.add(atracao);
            }
        }
        cbAtracao.setItems(FXCollections.observableArrayList(semOperador));
        cbAtracao.setValue(null);
        cbAtracao.setPromptText(semOperador.isEmpty() ? "Todas as atrações já têm operador"
                : "Escolha a atração");
        tabela.setItems(FXCollections.observableArrayList(comOperador));
    }
}
