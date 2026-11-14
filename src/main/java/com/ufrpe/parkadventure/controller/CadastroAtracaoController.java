package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.atracao.Aquatico;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.atracao.Infantil;
import com.ufrpe.parkadventure.model.atracao.Radical;
import com.ufrpe.parkadventure.repository.Persistencia;
import com.ufrpe.parkadventure.view.Aviso;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

// Tela do administrador: cadastra atrações novas (o operador é cadastrado na outra tela)
public class CadastroAtracaoController {

    private static final String AQUATICA = "Aquática";
    private static final String RADICAL = "Radical";
    private static final String INFANTIL = "Infantil";

    @FXML private TextField txtNome, txtCapacidade, txtAltura, txtIdade;
    @FXML private ComboBox<String> cbTipo;
    @FXML private Label lblAltura, lblIdade, lblResultado;
    @FXML private TableView<Atracao> tabela;
    @FXML private TableColumn<Atracao, String> colNome, colTipo, colCapacidade, colRequisitos, colOperador;

    @FXML
    private void initialize() {
        cbTipo.setItems(FXCollections.observableArrayList(AQUATICA, RADICAL, INFANTIL));
        // na atração infantil o limite é MÁXIMO; nas outras é MÍNIMO
        cbTipo.valueProperty().addListener((obs, antigo, novo) -> {
            boolean infantil = INFANTIL.equals(novo);
            lblAltura.setText(infantil ? "Altura máxima (cm)" : "Altura mínima (cm)");
            lblIdade.setText(infantil ? "Idade máxima" : "Idade mínima");
        });

        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(nomeDoTipo(c.getValue())));
        colCapacidade.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCapacidadeMaxima())));
        colRequisitos.setCellValueFactory(c -> new SimpleStringProperty(requisitos(c.getValue())));
        colOperador.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().temOperador() ? c.getValue().getOperador().getNome() : "(sem operador)"));
        tabela.setPlaceholder(new Label("Nenhuma atração cadastrada."));
        atualizarTabela();
    }

    @FXML
    private void onCadastrar() {
        String nome = txtNome.getText().trim();
        String tipo = cbTipo.getValue();
        if (nome.isEmpty()) {
            Aviso.erro(lblResultado, "Informe o nome da atração.");
            return;
        }
        if (tipo == null) {
            Aviso.erro(lblResultado, "Escolha o tipo da atração.");
            return;
        }
        Integer capacidade = lerNumero(txtCapacidade);
        if (capacidade == null || capacidade <= 0) {
            Aviso.erro(lblResultado, "A capacidade deve ser um número maior que zero.");
            return;
        }
        Integer altura = lerNumero(txtAltura);
        Integer idade = lerNumero(txtIdade);
        if (altura == null || altura < 0 || idade == null || idade < 0) {
            Aviso.erro(lblResultado, "Altura e idade devem ser números (use 0 para \"sem limite\").");
            return;
        }
        if (tipo.equals(INFANTIL) && altura == 0) {
            Aviso.erro(lblResultado, "Na atração infantil, informe a altura máxima em centímetros.");
            return;
        }

        // sem operador por enquanto: a atração fica "fechada" até o operador ser cadastrado
        Atracao atracao;
        if (tipo.equals(INFANTIL)) {
            atracao = new Infantil(nome, capacidade, null, altura, idade);
        } else if (tipo.equals(AQUATICA)) {
            atracao = new Aquatico(nome, capacidade, null, altura, idade);
        } else {
            atracao = new Radical(nome, capacidade, null, altura, idade);
        }

        if (!RepositorioAtracaoController.cadastrar(atracao)) {
            Aviso.erro(lblResultado, "Já existe uma atração com esse nome.");
            return;
        }
        Persistencia.salvar();
        atualizarTabela();
        txtNome.clear();
        txtCapacidade.clear();
        txtAltura.clear();
        txtIdade.clear();
        cbTipo.setValue(null);
        Aviso.sucesso(lblResultado, "Atração \"" + nome + "\" cadastrada. Agora cadastre o operador dela em "
                + "\"Cadastro de Operadores\" para abri-la aos visitantes.");
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
        tabela.setItems(FXCollections.observableArrayList(RepositorioAtracaoController.listarTodas()));
    }

    private Integer lerNumero(TextField campo) {
        try {
            return Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String nomeDoTipo(Atracao atracao) {
        if (atracao instanceof Infantil) return INFANTIL;
        if (atracao instanceof Aquatico) return AQUATICA;
        return RADICAL;
    }

    private String requisitos(Atracao atracao) {
        if (atracao instanceof Infantil infantil) {
            return "Até " + infantil.getAlturaMaxima() + " cm e " + infantil.getIdadeMaxima() + " anos";
        }
        return "A partir de " + atracao.getAlturaMinima() + " cm e " + atracao.getIdadeMinima() + " anos";
    }
}
