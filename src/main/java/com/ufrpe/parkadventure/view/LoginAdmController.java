package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.ParkAdventure;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import com.ufrpe.parkadventure.app.Contexto;
import com.ufrpe.parkadventure.service.ServicoDemonstracao;
import com.ufrpe.parkadventure.view.controles.DadosExemplo;

// Menu do administrador: leva para relatórios, cadastro de atrações e cadastro de operadores
public class LoginAdmController {

    @FXML
    MenuItem menuVoltar;

    @FXML
    Label lblBoasVindas;

    @FXML
    void onRelatorios(ActionEvent event) {
        abrir("view/relatorios.fxml");
    }

    @FXML
    void onCadastroAtracao(ActionEvent event) {
        abrir("view/cadastro-atracao.fxml");
    }

    @FXML
    void onCadastroOperador(ActionEvent event) {
        abrir("view/operadores.fxml");   // vários operadores por atração, com usuário e senha
    }

    @FXML
    void onEscala(ActionEvent event) {
        abrir("view/escala-trabalho.fxml");
    }

    @FXML
    void onVisitantes(ActionEvent event) {
        abrir("view/visitantes.fxml");
    }

    // Item "Configurações" do menu: carrega visitantes, ingressos, reservas e escala de exemplo
    @FXML
    void onConfiguracoes(ActionEvent event) {
        DialogoParque.mostrar(AlertType.CONFIRMATION, lblBoasVindas.getScene(),
                "Carregar os dados de demonstração?",
                "Cria visitantes (senha \"" + ServicoDemonstracao.SENHA_DEMONSTRACAO
                + "\"), ingressos, uma atração lotada, reservas de fura-fila e a escala dos próximos dias. "
                + "Os dados atuais são mantidos.",
                resposta -> {
                    if (resposta == ButtonType.OK) {
                        carregarDadosDemonstracao();
                    }
                });
    }

    private void carregarDadosDemonstracao() {
        String erro = Contexto.SERVICO_DEMONSTRACAO.verificarPodeCarregar();
        if (erro == null) {
            DadosExemplo.carregar();                                  // a carga de exemplo que já existia no projeto
            Contexto.SERVICO_DEMONSTRACAO.liberarAcessoDosVisitantes();
            Contexto.SESSAO.limpar();                                 // a carga deixa a Carla "logada"; aqui quem entrou é o administrador
        }
        DialogoParque.mostrar(erro == null ? AlertType.INFORMATION : AlertType.ERROR,
                lblBoasVindas.getScene(),
                erro == null ? "Dados de demonstração carregados." : "Nada foi carregado.",
                erro == null ? "Veja em Visitantes Cadastrados e em Relatórios do Parque." : erro);
    }

    @FXML
    void onVoltar(ActionEvent event) {
        try {
            ParkAdventure.voltar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrir(String fxml) {
        try {
            ParkAdventure.proximaCena(fxml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
