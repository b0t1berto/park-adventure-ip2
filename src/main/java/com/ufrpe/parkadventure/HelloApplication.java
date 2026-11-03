package com.ufrpe.parkadventure;

import com.ufrpe.parkadventure.controller.controles.DadosExemplo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage palco) throws IOException {
        DadosExemplo.carregar();   // preenche o sistema antes de qualquer tela abrir

        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("compra-ingressos.fxml"));
        Scene cena = new Scene(loader.load(), 1100, 680);

        cena.getStylesheets().add(HelloApplication.class.getResource("estilo.css").toExternalForm());
        palco.setTitle("ParkAdventure");
        palco.setScene(cena);
        palco.setMinWidth(980);
        palco.setMinHeight(640);
        palco.show();
    }
}
