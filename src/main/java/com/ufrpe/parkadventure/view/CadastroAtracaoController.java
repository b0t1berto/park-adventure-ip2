package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.app.Contexto;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.atracao.Aquatico;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.atracao.Infantil;
import com.ufrpe.parkadventure.model.atracao.Radical;
import com.ufrpe.parkadventure.repository.Persistencia;
import com.ufrpe.parkadventure.service.ServicoAutenticacao;
import com.ufrpe.parkadventure.service.ServicoGeradorEscala;
import com.ufrpe.parkadventure.view.Aviso;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

// Tela do administrador: cadastra atrações novas (o operador é cadastrado na outra tela)
public class CadastroAtracaoController {

    private static final String AQUATICA = "Aquática";
    private static final String RADICAL = "Radical";
    private static final String INFANTIL = "Infantil";

    @FXML private TextField txtBusca, txtNome, txtCapacidade, txtAltura, txtIdade;
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
                c.getValue().temOperador()
                        ? c.getValue().getOperadores().stream().map(o -> o.getNome()).collect(java.util.stream.Collectors.joining(", "))
                        : "(sem operador)"));
        tabela.setPlaceholder(new Label("Nenhuma atração encontrada."));

        // clicar numa linha carrega a atração no formulário para editar
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
            if (novo != null) {
                preencher(novo);
            }
        });
        txtBusca.textProperty().addListener((obs, antigo, novo) -> atualizarTabela());
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

        // os operadores são criados logo abaixo (ver garantirOperadoresDasAtracoes)
        Atracao atracao;
        if (tipo.equals(INFANTIL)) {
            atracao = new Infantil(nome, capacidade, null, altura, idade);
        } else if (tipo.equals(AQUATICA)) {
            atracao = new Aquatico(nome, capacidade, null, altura, idade);
        } else {
            atracao = new Radical(nome, capacidade, null, altura, idade);
        }

        if (!Contexto.SERVICO_ATRACAO.cadastrar(atracao)) {
            Aviso.erro(lblResultado, "Já existe uma atração com esse nome.");
            return;
        }
        // toda atração nasce com operadores (mínimo de 3, com usuário e senha inicial) e já entra na escala
        Contexto.SERVICO_AUTENTICACAO.garantirOperadoresDasAtracoes(ServicoGeradorEscala.MIN_OPERADORES_POR_ATRACAO);
        Contexto.SERVICO_GERADOR_ESCALA.gerarProximosDias();
        Persistencia.salvar();
        atualizarTabela();
        txtNome.clear();
        txtCapacidade.clear();
        txtAltura.clear();
        txtIdade.clear();
        cbTipo.setValue(null);
        Aviso.sucesso(lblResultado, "Atração \"" + nome + "\" cadastrada com " + ServicoGeradorEscala.MIN_OPERADORES_POR_ATRACAO
                + " operadores (senha inicial: " + ServicoAutenticacao.SENHA_PADRAO_OPERADOR
                + "). Ajuste-os em \"Cadastro de Operadores\".");
    }

    @FXML
    private void onSalvar() {
        Atracao selecionada = tabela.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            Aviso.erro(lblResultado, "Selecione uma atração na tabela para editar.");
            return;
        }
        Integer capacidade = lerNumero(txtCapacidade);
        Integer altura = lerNumero(txtAltura);
        Integer idade = lerNumero(txtIdade);
        if (capacidade == null || altura == null || idade == null) {
            Aviso.erro(lblResultado, "Capacidade, altura e idade devem ser números.");
            return;
        }
        String erro = Contexto.SERVICO_GESTAO_ATRACAO.editarAtracao(selecionada, cbTipo.getValue(), txtNome.getText(),
                capacidade, altura, idade);
        if (erro != null) {
            Aviso.erro(lblResultado, erro);
            return;
        }
        limparFormulario();
        atualizarTabela();
        Aviso.sucesso(lblResultado, "Atração atualizada. Operadores, ocupação e reservas foram mantidos.");
    }

    @FXML
    private void onRemover() {
        Atracao selecionada = tabela.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            Aviso.erro(lblResultado, "Selecione uma atração na tabela para remover.");
            return;
        }
        Alert pergunta = new Alert(AlertType.CONFIRMATION);
        pergunta.setTitle("CONFIRMAÇÃO");
        pergunta.setHeaderText("Remover \"" + selecionada.getNome() + "\"?");
        pergunta.setContentText("Os operadores dela (" + selecionada.getOperadores().size() + "), as reservas de fura-fila "
                + "e os turnos deles na escala também serão apagados. Isso não pode ser desfeito.");
        if (Aviso.mostrar(pergunta).filter(botao -> botao == ButtonType.OK).isEmpty()) {
            return;
        }
        String nome = selecionada.getNome();
        String erro = Contexto.SERVICO_GESTAO_ATRACAO.removerAtracao(selecionada);
        if (erro != null) {
            Aviso.erro(lblResultado, erro);
            return;
        }
        limparFormulario();
        atualizarTabela();
        Aviso.sucesso(lblResultado, "Atração \"" + nome + "\" removida.");
    }

    @FXML
    private void onLimpar() {
        tabela.getSelectionModel().clearSelection();
        limparFormulario();
        Aviso.limpar(lblResultado);
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
        tabela.setItems(FXCollections.observableArrayList(Contexto.SERVICO_GESTAO_ATRACAO.buscarPorTrecho(txtBusca.getText())));
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

    private void preencher(Atracao atracao) {
        txtNome.setText(atracao.getNome());
        cbTipo.setValue(nomeDoTipo(atracao));
        txtCapacidade.setText(String.valueOf(atracao.getCapacidadeMaxima()));
        if (atracao instanceof Infantil infantil) {
            txtAltura.setText(String.valueOf(infantil.getAlturaMaxima()));
            txtIdade.setText(String.valueOf(infantil.getIdadeMaxima()));
        } else {
            txtAltura.setText(String.valueOf(atracao.getAlturaMinima()));
            txtIdade.setText(String.valueOf(atracao.getIdadeMinima()));
        }
        Aviso.limpar(lblResultado);
    }

    private void limparFormulario() {
        txtNome.clear();
        txtCapacidade.clear();
        txtAltura.clear();
        txtIdade.clear();
        cbTipo.setValue(null);
    }
}
