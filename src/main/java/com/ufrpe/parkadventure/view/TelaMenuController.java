package com.ufrpe.parkadventure.view;

import com.ufrpe.parkadventure.ParkAdventure;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class TelaMenuController {

    @FXML private Label lblDesejaReservar, lblReservar, lblParkAdventure;

    @FXML private Button bntComeceAqui, bntAcesseAqui;

    @FXML private StackPane cabecalhoImagem;
    @FXML private ImageView imgFundo;

    @FXML
    private void initialize() {
        cabecalhoImagem.widthProperty().addListener((observable, anterior, atual) -> ajustarImagemFundo());
        cabecalhoImagem.heightProperty().addListener((observable, anterior, atual) -> ajustarImagemFundo());
        ajustarImagemFundo();
    }

    private void ajustarImagemFundo() {
        Image imagem = imgFundo.getImage();
        double largura = cabecalhoImagem.getWidth();
        double altura = cabecalhoImagem.getHeight();
        if (imagem == null || imagem.getWidth() <= 0 || imagem.getHeight() <= 0 || largura <= 0 || altura <= 0) {
            return;
        }

        double proporcaoDestino = largura / altura;
        double proporcaoImagem = imagem.getWidth() / imagem.getHeight();
        double viewportLargura = imagem.getWidth();
        double viewportAltura = imagem.getHeight();
        if (proporcaoImagem > proporcaoDestino) {
            viewportLargura = imagem.getHeight() * proporcaoDestino;
        } else {
            viewportAltura = imagem.getWidth() / proporcaoDestino;
        }

        imgFundo.setViewport(new Rectangle2D(
                (imagem.getWidth() - viewportLargura) / 2,
                (imagem.getHeight() - viewportAltura) / 2,
                viewportLargura,
                viewportAltura));
        imgFundo.setFitWidth(largura);
        imgFundo.setFitHeight(altura);
    }

    @FXML
    void onCriarConta(ActionEvent event) {
        try {
            ParkAdventure.proximaCena("view/tela-criar-conta.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onFazerLogin(ActionEvent event) {
        try {
            ParkAdventure.proximaCena("view/tela-de-login.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}