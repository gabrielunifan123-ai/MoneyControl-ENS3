package com.moneycontrol.model;

public enum Categoria {
    ALIMENTACAO("Alimentação"),
    TRANSPORTE("Transporte"),
    LAZER("Lazer"),
    SAUDE("Saúde"),
    OUTROS("Outros");

    private final String label;

    Categoria(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }

    public static Categoria fromLabel(String label) {
        for (Categoria c : values()) {
            if (c.label.equalsIgnoreCase(label)) return c;
        }
        return OUTROS;
    }

    @Override
    public String toString() { return label; }
}
