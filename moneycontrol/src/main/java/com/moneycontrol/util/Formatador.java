package com.moneycontrol.util;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Formatador {

    private static final Locale BRASIL = new Locale("pt", "BR");
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(BRASIL);
    private static final DateTimeFormatter FMT_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String moeda(double valor) {
        return MOEDA.format(valor);
    }

    public static String data(LocalDate d) {
        return d != null ? d.format(FMT_BR) : "";
    }

    public static String primeiroNome(String nomeCompleto) {
        if (nomeCompleto == null) return "";
        return nomeCompleto.trim().split("\\s+")[0];
    }
}
