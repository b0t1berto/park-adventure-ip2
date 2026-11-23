package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.app.Contexto;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.repository.Persistencia;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

// Criação de conta de VISITANTE: só usuário e senha (nome completo e CPF são informados na primeira compra).
// Operadores não são cadastrados aqui: só o administrador cadastra operadores.
public class TelaCriarContaController {

    @FXML
    private Label lblUsuario, lblSenha, lblMensagem, lblDivertir, lblParkAdventure;

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private Button bntVoltar, bntConfirmar;

    @FXML
    private StackPane areaPersonagem;

    @FXML
    private ImageView imgPersonagem;

    @FXML
    private void initialize() {
        var tamanho = Bindings.createDoubleBinding(
                () -> Math.max(0, Math.min(
                        areaPersonagem.getWidth() * 0.98,
                        areaPersonagem.getHeight() * 0.78)),
                areaPersonagem.widthProperty(), areaPersonagem.heightProperty());
        imgPersonagem.fitWidthProperty().bind(tamanho);
        imgPersonagem.fitHeightProperty().bind(tamanho);
        lblDivertir.prefWidthProperty().bind(tamanho);
        lblDivertir.maxWidthProperty().bind(tamanho);
    }

    @FXML
    void onVoltar(ActionEvent event) {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onConfirmar(ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        String senha = txtSenha.getText();

        String problema = Contexto.SERVICO_AUTENTICACAO.validarCredenciais(usuario, senha);
        if (problema != null) {
            erro("Suas informações estão inválidas!", problema);
            return;
        }
        // administrador, visitantes e operadores entram pelo mesmo login: o usuário é único no sistema
        if (!Contexto.SERVICO_AUTENTICACAO.usuarioDisponivel(usuario, null)) {
            erro("Conta não criada", "Esse usuário não está disponível. Escolha outro.");
            return;
        }

        DialogoParque.mostrar(Alert.AlertType.CONFIRMATION, txtUsuario.getScene(),
                "Confirme os dados da sua conta", "Seu usuário será: " + usuario + " ?",
                resposta -> {
                    if (resposta == ButtonType.OK) {
                        cadastrarConta(usuario, senha);
                    }
                });
    }

    private void cadastrarConta(String usuario, String senha) {
        // o nome da conta é o usuário. Nascimento e altura são só marcadores: a pessoa informa os dados de
        // verdade (nome completo, CPF...) na compra do ingresso.
        Visitante visitante = new Visitante(usuario, "01/01/2000", null, 0);
        visitante.setUsuario(usuario);
        visitante.definirSenha(senha);   // só o hash da senha é guardado (ver Senha)
        if (!Contexto.SERVICO_VISITANTE.cadastrar(visitante)) {
            erro("Conta não criada", "Esse usuário não está disponível. Escolha outro.");
            return;
        }
        Persistencia.salvar();

        DialogoParque.mostrar(Alert.AlertType.INFORMATION, txtUsuario.getScene(),
                "Conta criada com sucesso!",
                "Seu nome completo, CPF, altura e data de nascimento serão pedidos na compra do ingresso.",
                resposta -> {
                    txtUsuario.clear();
                    txtSenha.clear();
                });
    }

    private void erro(String titulo, String mensagem) {
        DialogoParque.mostrarErro(txtUsuario.getScene(), titulo, mensagem);
    }
}
