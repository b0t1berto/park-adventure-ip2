package com.ufrpe.parkadventure.javafx_parkadventure.view;

import com.ufrpe.parkadventure.javafx_parkadventure.model.ingresso.TipoIngresso;

import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public final class Formatos {
    private Formatos() { }

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String moeda(double valor) {
        return NumberFormat.getCurrencyInstance(PT_BR).format(valor); 
    }

    public static String data(LocalDate data) {
        return data.format(DATA);
    }

    public static String diaSemana(LocalDate data) {
        return maiuscula(data.getDayOfWeek().getDisplayName(TextStyle.FULL, PT_BR));
    }

    public static String nomeCurto(DayOfWeek dia) {
        return maiuscula(dia.getDisplayName(TextStyle.SHORT, PT_BR)); 
    }

    public static String mesAno(YearMonth mes) {
        return maiuscula(mes.getMonth().getDisplayName(TextStyle.FULL, PT_BR)) + " de " + mes.getYear();
    }

    public static String tipoIngresso(TipoIngresso tipo) {
        return tipo == TipoIngresso.PASSE_ANUAL ? "Passe anual" : "Diária";
    }

    private static String maiuscula(String texto) {
        return texto.isEmpty() ? texto : Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }
}