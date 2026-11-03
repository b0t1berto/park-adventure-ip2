package com.ufrpe.parkadventure.javafx_parkadventure.view;

import javafx.scene.control.Label;

public final class Aviso {
    private Aviso() { }

    public static void sucesso(Label label, String texto) { mostrar(label, texto, "ok"); }

    public static void erro(Label label, String texto) { mostrar(label, texto, "erro"); }

    public static void info(Label label, String texto) { mostrar(label, texto, null); }

    public static void limpar(Label label) { mostrar(label, "", null); }

    private static void mostrar(Label label, String texto, String classeCss) {
        label.getStyleClass().removeAll("ok", "erro");
        if (classeCss != null) {
            label.getStyleClass().add(classeCss);
        }
        label.setWrapText(true);
        label.setText(texto);
    }
}