package com.ufrpe.parkadventure.controller.controles;

import com.ufrpe.parkadventure.view.Formatos;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

// Liga as colunas (criadas no Scene Builder) aos dados. Colunas numéricas ordenam como número,
// não como texto ("10" depois de "2").
public final class ColunasRelatorio {
    private ColunasRelatorio() { }

    private static final String DIREITA = "-fx-alignment: CENTER-RIGHT;";

    public static <S> void texto(TableColumn<S, String> coluna, Function<S, String> valor) {
        coluna.setCellValueFactory(c -> new SimpleStringProperty(valor.apply(c.getValue())));
    }

    public static <S> void inteiro(TableColumn<S, Number> coluna, ToIntFunction<S> valor) {
        coluna.setCellValueFactory(c -> new SimpleIntegerProperty(valor.applyAsInt(c.getValue())));
        coluna.setStyle(DIREITA);
    }

    // R$ 1.234,56
    public static <S> void moeda(TableColumn<S, Number> coluna, ToDoubleFunction<S> valor) {
        numero(coluna, valor, Formatos::moeda);
    }

    // 12,5%
    public static <S> void percentual(TableColumn<S, Number> coluna, ToDoubleFunction<S> valor) {
        numero(coluna, valor, v -> String.format(java.util.Locale.forLanguageTag("pt-BR"), "%.1f%%", v));
    }

    private static <S> void numero(TableColumn<S, Number> coluna, ToDoubleFunction<S> valor,
                                   Function<Double, String> formato) {
        coluna.setCellValueFactory(c -> new SimpleDoubleProperty(valor.applyAsDouble(c.getValue())));
        coluna.setStyle(DIREITA);
        coluna.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean vazio) {
                super.updateItem(item, vazio);
                setText(vazio || item == null ? null : formato.apply(item.doubleValue()));
            }
        });
    }
}
