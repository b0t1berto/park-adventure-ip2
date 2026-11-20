package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.app.Contexto;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.service.ServicoAutenticacao;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

// Login por usuário e senha: o sistema descobre sozinho se é administrador, operador ou visitante.
public class TelaLoginController {

    @FXML
    Label lblUsuario, lblSenha, lblParkAdventure, lblAcesseConta, lblDivertir;

    @FXML
    private StackPane areaPersonagem;

    @FXML
    private ImageView imgPersonagem;

    @FXML
    private void initialize() {
        // A área do personagem não pode "empurrar" o tamanho da tela: o tamanho dela
        // vem só do GridPane.
        // Sem isso, a imagem (muito grande) fazia a janela crescer ao
        // abrir/redimensionar.
        areaPersonagem.setMinSize(0, 0);
        areaPersonagem.setPrefSize(0, 0);

        // Mínimo 1: ImageView com fitWidth/fitHeight <= 0 usa o tamanho ORIGINAL da
        // imagem (era o bug,
        // pois no carregamento a área ainda tem tamanho 0).
        var tamanho = Bindings.createDoubleBinding(
                () -> Math.max(1, Math.min(
                        areaPersonagem.getWidth() * 0.98,
                        Math.min(areaPersonagem.getHeight() * 0.78, areaPersonagem.getHeight() - 120))),
                areaPersonagem.widthProperty(), areaPersonagem.heightProperty());
        imgPersonagem.fitWidthProperty().bind(tamanho);
        imgPersonagem.fitHeightProperty().bind(tamanho);
        lblDivertir.prefWidthProperty().bind(tamanho);
        lblDivertir.maxWidthProperty().bind(tamanho);
    }

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

    @FXML
    public void onConfirmar(ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        String senha = txtSenha.getText();

        if (usuario.isEmpty() || senha.isEmpty()) {
            avisar("Suas informações estão inválidas!", "Digite o usuário e a senha para entrar.");
            txtSenha.setText("");
            return;
        }

        ServicoAutenticacao.Resultado resultado = Contexto.SERVICO_AUTENTICACAO.entrar(usuario, senha);
        if (resultado == null) {
            avisar("Usuário ou senha incorretos!", "Confira os dados e tente novamente.");
            txtSenha.setText("");
            return;
        }

        try {
            Contexto.SESSAO.limpar();
            switch (resultado.papel()) {
                case ADMIN -> ParkAdventure.proximaCena("view/menu-adm.fxml");
                case ServicoAutenticacao.Papel.OPERADOR -> {
                    Contexto.SESSAO.setOperadorAtual((Operador) resultado.pessoa());
                    ParkAdventure.proximaCena("view/menu-operador.fxml");
                }
                case ServicoAutenticacao.Papel.VISITANTE -> {
                    Contexto.SESSAO.setVisitanteAtual((Visitante) resultado.pessoa());
                    ParkAdventure.proximaCena("view/menu-visitante.fxml");
                }
            }
            txtSenha.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void avisar(String titulo, String mensagem) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.initOwner(txtUsuario.getScene().getWindow());
        alert.setTitle("AVISO");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
