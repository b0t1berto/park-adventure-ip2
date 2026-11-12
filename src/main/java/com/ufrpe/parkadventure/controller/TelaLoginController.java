package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.model.pessoa.Visitante;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class TelaLoginController {

    @FXML
    Label lblNome, lblCpf, lblCargo, lblParkAdventure, lblAcesseConta;

    @FXML
    TextField txtFieldNome, txtFieldCpf, txtFieldCargo;

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
        String nome = txtFieldNome.getText().trim();
        String cpf = txtFieldCpf.getText().trim();
        String cargo = txtFieldCargo.getText().trim();

        if (nome.isEmpty() || cpf.isEmpty()) {
            avisar("Suas informações estão inválidas!", "Digite informações válidas para entrar na conta.");
            limparCampos();
            return;
        }

        try {
            if (nome.equalsIgnoreCase("leandro")) {
                ContextoController.SESSAO.limpar();
                ParkAdventure.proximaCena("view/menu-adm.fxml");
            } else if (cargo.isEmpty()) {
                // sem cargo: é um visitante, e ele precisa existir
                Visitante visitante = RepositorioVisitanteController.buscarVisitante(nome);
                if (visitante == null) {
                    avisar("Visitante não encontrado!", "O visitante não está cadastrado.");
                    limparCampos();
                    return;
                }
                if (!cpfConfere(visitante.getCpf(), cpf)) {
                    avisar("CPF incorreto!", "O CPF informado não confere com o cadastro.");
                    limparCampos();
                    return;
                }
                ContextoController.SESSAO.limpar();
                ContextoController.SESSAO.setVisitanteAtual(visitante);
                ParkAdventure.proximaCena("view/menu-visitante.fxml");
            } else {
                // com cargo: é um operador; o cargo é o nome da atração em que ele trabalha
                Operador operador = RepositorioAtracaoController.buscarOperadorPorNome(nome, cargo);
                if (operador == null) {
                    avisar("Operador não encontrado!",
                            "Não há operador com esse nome nessa atração. No campo cargo, digite o nome da atração.");
                    limparCampos();
                    return;
                }
                if (!cpfConfere(operador.getCpf(), cpf)) {
                    avisar("CPF incorreto!", "O CPF informado não confere com o cadastro.");
                    limparCampos();
                    return;
                }
                ContextoController.SESSAO.limpar();
                ContextoController.SESSAO.setOperadorAtual(operador);
                ParkAdventure.proximaCena("view/menu-operador.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Compara só os dígitos (com ou sem pontos e traço). Contas de exemplo, criadas sem CPF, aceitam qualquer um.
    private boolean cpfConfere(String cpfCadastrado, String cpfDigitado) {
        String cadastrado = cpfCadastrado == null ? "" : cpfCadastrado.replaceAll("\\D", "");
        if (cadastrado.isEmpty()) {
            return true;
        }
        return cadastrado.equals(cpfDigitado.replaceAll("\\D", ""));
    }

    private void avisar(String titulo, String mensagem) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("AVISO");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void limparCampos() {
        txtFieldNome.setText("");
        txtFieldCpf.setText("");
        txtFieldCargo.setText("");
    }
}
