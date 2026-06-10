package com.moneycontrol.ui;

import com.moneycontrol.MainApp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class Navbar {

    public enum Tab { DASHBOARD, GASTOS, NOVO_GASTO, RELATORIO }

    public static HBox criar(Tab activeTab) {
        HBox navbar = new HBox();
        navbar.getStyleClass().add("navbar");
        navbar.setAlignment(Pos.CENTER);

        Button btnHome = navBtn("🏠", "Início", activeTab == Tab.DASHBOARD);
        Button btnGastos = navBtn("📋", "Gastos", activeTab == Tab.GASTOS);
        Button btnNovo = navBtn("➕", "Novo", activeTab == Tab.NOVO_GASTO);
        Button btnRel = navBtn("📊", "Relatório", activeTab == Tab.RELATORIO);

        HBox.setHgrow(btnHome, Priority.ALWAYS);
        HBox.setHgrow(btnGastos, Priority.ALWAYS);
        HBox.setHgrow(btnNovo, Priority.ALWAYS);
        HBox.setHgrow(btnRel, Priority.ALWAYS);

        btnHome.setOnAction(e -> {
            if (activeTab != Tab.DASHBOARD) {
                MainApp.primaryStage.getScene().setRoot(new DashboardView().getRoot());
            }
        });

        btnGastos.setOnAction(e -> {
            if (activeTab != Tab.GASTOS) {
                MainApp.primaryStage.getScene().setRoot(new GastosView().getRoot());
            }
        });

        btnNovo.setOnAction(e -> {
            if (activeTab != Tab.NOVO_GASTO) {
                MainApp.primaryStage.getScene().setRoot(new NovoGastoView().getRoot());
            }
        });

        btnRel.setOnAction(e -> {
            if (activeTab != Tab.RELATORIO) {
                MainApp.primaryStage.getScene().setRoot(new RelatorioView().getRoot());
            }
        });

        navbar.getChildren().addAll(btnHome, btnGastos, btnNovo, btnRel);
        return navbar;
    }

    private static Button navBtn(String icon, String label, boolean active) {
        VBox box = new VBox(2);
        box.setAlignment(Pos.CENTER);

        Button btn = new Button(icon + "\n" + label);
        btn.getStyleClass().add("nav-item");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setMaxHeight(Double.MAX_VALUE);
        btn.setStyle(
            "-fx-alignment: center; -fx-font-size: 11px; -fx-background-color: transparent;" +
            (active ? " -fx-text-fill: #1D9E75;" : " -fx-text-fill: #aaaaaa;")
        );
        return btn;
    }
}
