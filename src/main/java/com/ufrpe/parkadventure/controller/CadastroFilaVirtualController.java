package com.ufrpe.parkadventure.controller;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.atracao.FilaVirtual;
import com.ufrpe.parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.view.Aviso;
import com.ufrpe.parkadventure.view.Formatos;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Reserva do fura-fila: atração + dia + horário. No dia, o visitante se apresenta ao operador dentro da janela.
public class CadastroFilaVirtualController {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    @FXML private Label lblVisitante, lblDataVisita, lblDetalhe, lblResultado, lblPosicao;
    @FXML private ComboBox<Atracao> cbAtracao;
    @FXML private ComboBox<LocalDate> cbData;
    @FXML private ComboBox<String> cbHorario;
    @FXML private Button btnEntrar, btnVoltar;

    private Visitante visitante;

    @FXML
    private void initialize() {
        visitante = ContextoController.SESSAO.getVisitanteAtual();

        cbAtracao.setItems(FXCollections.observableArrayList(RepositorioAtracaoController.listarTodas()));
        cbAtracao.setConverter(new StringConverter<Atracao>() {
            @Override public String toString(Atracao a) { return a == null ? "" : a.getNome(); }
            @Override public Atracao fromString(String s) { return null; }
        });
        cbData.setConverter(new StringConverter<LocalDate>() {
            @Override public String toString(LocalDate d) {
                return d == null ? "" : Formatos.data(d) + " (" + Formatos.diaSemana(d) + ")";
            }
            @Override public LocalDate fromString(String s) { return null; }
        });
        cbAtracao.valueProperty().addListener((obs, antiga, nova) -> aoMudarEscolha());
        cbData.valueProperty().addListener((obs, antiga, nova) -> aoMudarEscolha());

        btnEntrar.disableProperty().bind(cbAtracao.valueProperty().isNull()
                .or(cbData.valueProperty().isNull())
                .or(cbHorario.valueProperty().isNull()));

        if (visitante == null) {
            lblVisitante.setText("Faça login para reservar");
            Aviso.erro(lblResultado, "Entre na sua conta para reservar o fura-fila.");
            bloquear();
            return;
        }
        lblVisitante.setText(visitante.getNome());
        List<LocalDate> datas = ContextoController.CONTROLE_ACESSO.datasDisponiveis(visitante);
        if (datas.isEmpty()) {
            lblDataVisita.setText("Sem ingresso válido");
            Aviso.erro(lblResultado, "Você não tem ingresso válido para os próximos dias. Compre um ingresso para reservar.");
            bloquear();
            return;
        }
        cbData.setItems(FXCollections.observableArrayList(datas));
        lblDataVisita.setText(datas.size() == 1
                ? "Ingresso válido em " + Formatos.data(datas.get(0))
                : "Reservas liberadas de " + Formatos.data(datas.get(0))
                + " a " + Formatos.data(datas.get(datas.size() - 1)));
        if (datas.size() == 1) {
            cbData.setValue(datas.get(0));
        }
    }

    private void bloquear() {
        cbAtracao.setDisable(true);
        cbData.setDisable(true);
        cbHorario.setDisable(true);
        btnEntrar.disableProperty().unbind();
        btnEntrar.setDisable(true);
    }

    private void aoMudarEscolha() {
        Aviso.limpar(lblResultado);
        lblPosicao.setText("");
        Atracao atracao = cbAtracao.getValue();
        LocalDate data = cbData.getValue();
        if (atracao != null) {
            lblDetalhe.setText("Altura mínima: " + atracao.getAlturaMinima() + " cm  •  Idade mínima: "
                    + atracao.getIdadeMinima() + " anos  •  Vagas por horário: " + atracao.vagasFuraFilaPorHorario()
                    + "\nApresente-se ao operador entre o horário escolhido e " + (int) FilaVirtual.TOLERANCIA.toMinutes()
                    + " minutos depois. Quem não aparecer perde a vaga.");
        } else {
            lblDetalhe.setText("");
        }
        atualizarHorarios(atracao, data);
    }

    // Lista os horários do dia com as vagas que ainda restam
    private void atualizarHorarios(Atracao atracao, LocalDate data) {
        cbHorario.getSelectionModel().clearSelection();
        if (atracao == null || data == null) {
            cbHorario.setItems(FXCollections.observableArrayList());
            return;
        }
        LocalDateTime agora = LocalDateTime.now();
        List<String> opcoes = new ArrayList<>();
        for (LocalTime horario : FilaVirtual.horarios()) {
            // hoje: some o horário cuja janela já terminou
            if (data.equals(agora.toLocalDate())
                    && LocalDateTime.of(data, horario).plus(FilaVirtual.TOLERANCIA).isBefore(agora)) {
                continue;
            }
            int vagas = atracao.getFilaVirtual().vagasRestantes(data, horario);
            opcoes.add(horario.format(HORA) + (vagas > 0 ? "  •  " + vagas + " vaga(s)" : "  •  esgotado"));
        }
        cbHorario.setItems(FXCollections.observableArrayList(opcoes));
    }

    @FXML
    private void aoEntrarNaFila() {
        Atracao atracao = cbAtracao.getValue();
        LocalDate data = cbData.getValue();
        String escolhido = cbHorario.getValue();
        if (atracao == null || data == null || escolhido == null) {
            Aviso.erro(lblResultado, "Escolha a atração, o dia e o horário.");
            return;
        }
        LocalTime horario = LocalTime.parse(escolhido.substring(0, 5), HORA);
        // usa o ingresso que vale NAQUELE dia (o visitante pode ter uma diária e um passe)
        Ingresso ingressoDoDia = ContextoController.CONTROLE_ACESSO.ingressoParaData(visitante, data);
        ResultadoAcesso resultado = ContextoController.CONTROLE_ACESSO
                .reservarFuraFila(visitante, ingressoDoDia, atracao, data, horario);

        if (resultado == ResultadoAcesso.RESERVADO) {
            int posicao = atracao.getFilaVirtual().posicaoNoHorario(visitante, data);
            Aviso.sucesso(lblResultado, mensagem(resultado, atracao, data, horario));
            atualizarHorarios(atracao, data);   // as vagas mudaram
            lblPosicao.setText(posicao + "º neste horário");
        } else {
            Aviso.erro(lblResultado, mensagem(resultado, atracao, data, horario));
            lblPosicao.setText("");
        }
    }

    private String mensagem(ResultadoAcesso resultado, Atracao atracao, LocalDate data, LocalTime horario) {
        String nome = visitante.getNome();
        return switch (resultado) {
            case RESERVADO -> "Reserva confirmada: " + atracao.getNome() + ", " + Formatos.data(data) + " às "
                    + horario.format(HORA) + ". Apresente-se ao operador até "
                    + horario.plus(FilaVirtual.TOLERANCIA).format(HORA) + ".";
            case FILA_CHEIA -> "Os passes de " + atracao.getNome() + " nesse horário acabaram. Escolha outro.";
            case HORARIO_INVALIDO -> "Horário inválido. Use de 11:00 a 18:00, de 30 em 30 minutos, e que ainda não tenha passado.";
            case INGRESSO_DE_OUTRO_VISITANTE -> "O ingresso não pertence a " + nome + ".";
            case INGRESSO_EXPIRADO -> "O ingresso de " + nome + " não vale para o dia escolhido.";
            case SEM_INGRESSO -> "Compre um ingresso para reservar o fura-fila.";
            case FORA_DOS_REQUISITOS -> nome + " não atinge os requisitos de altura/idade de " + atracao.getNome() + ".";
            case SEM_RESPONSAVEL -> nome + " é menor de idade e precisa de um responsável.";
            case SEM_OPERADOR -> atracao.getNome() + " está fechada: não há operador vinculado.";
            case JA_NA_FILA -> nome + " já tem reserva em " + atracao.getNome() + " nesse dia.";
            case CONFLITO_DE_HORARIO -> nome + " já tem reserva em outra atração nesse mesmo horário.";
            default -> "Não foi possível reservar.";
        };
    }

    @FXML
    private void aoVoltar() {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
