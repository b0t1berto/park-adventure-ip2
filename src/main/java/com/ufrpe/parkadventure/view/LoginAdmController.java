package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.ParkAdventure;
import com.ufrpe.parkadventure.app.Contexto;
import com.ufrpe.parkadventure.service.ServicoAutenticacao.Papel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

// Menu do administrador: leva para relatórios, cadastro de atrações e cadastro de operadores
public class LoginAdmController {

    @FXML
    MenuItem menuVoltar;

    @FXML
    Label lblBoasVindas;

    @FXML
    Button btCadastroOperador;

    @FXML
    private void initialize() {
        // Apenas o administrador pode cadastrar operadores
        boolean isAdmin = Contexto.SESSAO.getPapel() == Papel.ADMIN;
        if (btCadastroOperador != null) {
            btCadastroOperador.setVisible(isAdmin);
            btCadastroOperador.setManaged(isAdmin);
        }
    }

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
    void onCalendario(ActionEvent event) {
        abrir("view/calendario.fxml");
    }

    @FXML
    void onVisitantes(ActionEvent event) {
        abrir("view/visitantes.fxml");
    }

    @FXML
    void onMeuPerfil(ActionEvent event) {
        abrir("view/perfil-admin.fxml");
    }

    // Item "Configurações" do menu: abre a tela de configurações da aplicação
    @FXML
    void onConfiguracoes(ActionEvent event) {
        abrir("view/configuracoes.fxml");
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
