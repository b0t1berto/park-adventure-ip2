package com.ufrpe.parkadventure.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.pessoa.*;
import com.ufrpe.parkadventure.repository.Persistencia;
import com.ufrpe.parkadventure.ParkAdventure;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class TelaCriarContaController {

    @FXML
    private Label lblNome, lblCpf, lblCargo, lblMensagem, lblDivertir, lblParkAdventure;

    @FXML
    private TextField txtNome, txtCpf, txtCargo;

    @FXML
    private Button bntVoltar, bntConfirmar;

    @FXML
    void onVoltar(ActionEvent event) {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onConfirmar (ActionEvent event) {
        String nome = txtNome.getText().trim();
        String cpf = txtCpf.getText().replaceAll("[^0-9]", "").trim(); // Remove pontos e traços do CPF
        String cargo = txtCargo.getText().trim();

        if (nome.isBlank() || cpf.length() != 11) {
            erro("Suas informações estão inválidas!", "Informe o nome e um CPF com 11 dígitos.");
            return;
        }

        // antes a confirmação era só informativa: mesmo se o usuário fechasse a janela a conta era criada
        Alert confirmacao = new Alert(AlertType.CONFIRMATION);
        confirmacao.setTitle("CONFIRMAÇÃO");
        confirmacao.setHeaderText("Confirme os dados da sua conta");
        confirmacao.setContentText("Seu nome é: " + nome + "\nSeu CPF é: " + cpf + " ?");
        if (confirmacao.showAndWait().filter(botao -> botao == ButtonType.OK).isEmpty()) {
            return;
        }

        if (cargo.isBlank()) {
            Visitante visitante = new Visitante(nome, "01/01/2000", cpf, 0);
            if (!RepositorioVisitanteController.cadastrarVisitante(visitante)) {
                erro("Conta não criada", "Já existe um visitante com esse nome.");
                return;
            }
        } else {
            // o cargo do operador é o nome da atração em que ele trabalha
            Atracao atracao = RepositorioAtracaoController.buscar(cargo);
            if (atracao == null) {
                erro("Conta não criada", "Não existe atração com o nome \"" + cargo + "\". "
                        + "No campo cargo, digite o nome exato da atração.");
                return;
            }
            // antes o operador novo SUBSTITUÍA o que já trabalhava na atração, sem aviso
            if (atracao.temOperador()) {
                erro("Conta não criada", "A atração " + atracao.getNome() + " já tem um operador responsável.");
                return;
            }
            Operador operador = new Operador(nome, "01/01/2000", cpf, atracao.getNome());
            RepositorioAtracaoController.cadastrarOperador(atracao.getNome(), operador);
        }

        Persistencia.salvar();

        Alert pronto = new Alert(AlertType.INFORMATION);
        pronto.setTitle("CONTA CRIADA");
        pronto.setHeaderText("Conta criada com sucesso!");
        pronto.setContentText(cargo.isBlank()
                ? "Sua altura e data de nascimento serão pedidas na compra do ingresso."
                : "Agora você já pode entrar como operador.");
        pronto.showAndWait();

        txtNome.setText("");
        txtCpf.setText("");
        txtCargo.setText("");
    }

    private void erro(String titulo, String mensagem) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("ERRO");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
