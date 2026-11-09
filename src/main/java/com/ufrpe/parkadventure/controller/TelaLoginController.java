package com.ufrpe.parkadventure.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.model.pessoa.Visitante;

public class TelaLoginController {

    @FXML Label lblNome, lblCpf, lblCargo, lblParkAdventure, lblAcesseConta;
    @FXML TextField txtFieldNome, txtFieldCpf, txtFieldCargo;
    @FXML Button btOk;
    
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
    String cpf = txtFieldCpf.getText().replaceAll("[^0-9]", "").trim(); // Remove pontos e traços
    String cargo = txtFieldCargo.getText().trim();

    if (nome.isBlank() || cpf.isBlank()) {
        exibirAlerta(AlertType.ERROR, "AVISO", "Informações incompletas!", "Preencha o Nome e o CPF para realizar o login.");
        limparCampos();
        return;
    }

    // Acesso administrativo direto
    if (nome.equalsIgnoreCase("leandro")) {
        navegarPara("view/menu-adm.fxml");
        return;
    }

    // Fluxo 1: Login de Visitante (Cargo em branco)
    if (cargo.isBlank()) {
        Visitante visitante = RepositorioVisitanteController.buscarVisitante(nome);
        System.out.println("Visitante encontrado: " + (visitante != null ? visitante.getNome() : "NULL"));
        
        // Verifica se o visitante existe e se o CPF coincide
        if (visitante == null || !cpf.equals(visitante.getCpf().replaceAll("[^0-9]", "").trim())) {
            exibirAlerta(AlertType.ERROR, "FALHA NO LOGIN", "Visitante não encontrado!", "Nome ou CPF incorretos/não cadastrados.");
            System.out.println("CPF digitado: [" + cpf + "] vs CPF cadastrado: [" + visitante.getCpf() + visitante.getNome() + "]");
            limparCampos();
            return;
        }
        
        navegarPara("view/menu-visitante.fxml");
    } 
    // Fluxo 2: Login de Operador (Cargo preenchido)
    else {
        Atracao atracao = RepositorioAtracaoController.buscar(cargo);
        Operador operador = (atracao != null) ? atracao.getOperador() : null;

        if (operador == null || !operador.getNome().equalsIgnoreCase(nome) || !cpf.equals(operador.getCpf().replaceAll("[^0-9]", "").trim())) {
            exibirAlerta(AlertType.ERROR, "FALHA NO LOGIN", "Operador não encontrado!", "Dados incorretos ou não há operador cadastrado para esta atração.");
            limparCampos();
            return;
        }

        navegarPara("view/menu-operador.fxml");
    }
}

    private void navegarPara(String fxml) {
        try {
            ParkAdventure.proximaCena(fxml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void limparCampos() {
        txtFieldNome.setText("");
        txtFieldCpf.setText("");
        txtFieldCargo.setText("");
    }

    private void exibirAlerta(AlertType tipo, String titulo, String cabecalho, String conteudo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecalho);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }
}