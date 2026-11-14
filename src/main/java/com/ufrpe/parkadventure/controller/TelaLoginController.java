package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.model.pessoa.Pessoa;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.repository.Persistencia;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class TelaLoginController {

    // Acesso do administrador. TROQUE a senha aqui se quiser outra.
    static final String USUARIO_ADM = "leandro";
    private static final String SENHA_ADM = "admin123";

    @FXML
    Label lblUsuario, lblSenha, lblParkAdventure, lblAcesseConta;

    @FXML
    TextField txtUsuario;

    @FXML
    PasswordField txtSenha;

    @FXML
    Button btOk;

    @FXML
    void onVoltar(ActionEvent event) {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // O tipo de conta vem do próprio usuário: administrador, visitante ou operador (não há mais campo "cargo").
    @FXML
    public void onConfirmar(ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        String senha = txtSenha.getText();

        if (usuario.isEmpty() || senha.isEmpty()) {
            avisar("Suas informações estão inválidas!", "Digite o usuário e a senha para entrar na conta.");
            txtSenha.clear();
            return;
        }

        try {
            if (usuario.equalsIgnoreCase(USUARIO_ADM)) {
                if (!senha.equals(SENHA_ADM)) {
                    falhou();
                    return;
                }
                ContextoController.SESSAO.limpar();
                ParkAdventure.proximaCena("view/menu-adm.fxml");
                return;
            }

            Visitante visitante = RepositorioVisitanteController.buscarVisitante(usuario);
            if (visitante != null) {
                if (!autenticar(visitante, senha)) {
                    falhou();
                    return;
                }
                ContextoController.SESSAO.limpar();
                ContextoController.SESSAO.setVisitanteAtual(visitante);
                ParkAdventure.proximaCena("view/menu-visitante.fxml");
                return;
            }

            Operador operador = RepositorioAtracaoController.buscarOperadorPorUsuario(usuario);
            if (operador != null && autenticar(operador, senha)) {
                ContextoController.SESSAO.limpar();
                ContextoController.SESSAO.setOperadorAtual(operador);
                ParkAdventure.proximaCena("view/menu-operador.fxml");
                return;
            }

            falhou();   // usuário inexistente ou senha errada: a mesma mensagem, para não revelar qual dos dois
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Confere a senha digitada com a da conta
    private boolean autenticar(Pessoa pessoa, String senha) {
        if (pessoa.temSenha()) {
            return pessoa.senhaConfere(senha);
        } else {
            // conta antiga (criada antes de existir senha): a primeira senha digitada passa a ser a dela
            pessoa.setSenha(senha);
            Persistencia.salvar();
            return true;
        }
    }

    private void falhou() {
        avisar("Usuário ou senha incorretos!", "Confira os dados e tente de novo.");
        txtSenha.clear();
    }

    private void avisar(String titulo, String mensagem) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("AVISO");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
