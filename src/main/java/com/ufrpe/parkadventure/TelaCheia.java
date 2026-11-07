package com.ufrpe.parkadventure;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public final class TelaCheia {
    private TelaCheia() { }

    public static void configurar(Stage palco) {
        palco.setResizable(true);
        palco.setMaximized(true);
        palco.setFullScreenExitHint("Aperte F11 ou Esc para sair da tela cheia");
        palco.addEventFilter(KeyEvent.KEY_PRESSED, evento -> {
            if (evento.getCode() == KeyCode.F11) {
                palco.setFullScreen(!palco.isFullScreen());
                evento.consume();
            }
        });
    }
}