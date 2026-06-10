package com.moneycontrol.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Gasto {
    private int id;
    private String descricao;
    private double valor;
    private LocalDate data;
    private Categoria categoria;
    private int usuarioId;

    private static final DateTimeFormatter FMT_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Gasto() {}

    public Gasto(String descricao, double valor, LocalDate data, Categoria categoria, int usuarioId) {
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
        this.categoria = categoria;
        this.usuarioId = usuarioId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String getDataFormatada() {
        return data != null ? data.format(FMT_BR) : "";
    }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getValorFormatado() {
        return String.format("R$ %,.2f", valor).replace(",", "X").replace(".", ",").replace("X", ".");
    }

    @Override
    public String toString() {
        return descricao + " - " + getValorFormatado() + " (" + categoria + ")";
    }
}
