package com.moneycontrol.ui;

import com.moneycontrol.MainApp;
import com.moneycontrol.model.Categoria;
import com.moneycontrol.model.Gasto;
import com.moneycontrol.service.GastoService;
import com.moneycontrol.service.ServiceException;
import com.moneycontrol.util.Formatador;
import com.moneycontrol.util.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Map;

public class DashboardView {

    private final VBox root;
    private final GastoService gastoService = new GastoService();

    public DashboardView() {
        root = new VBox();
        root.setStyle("-fx-background-color: #f5f5f0;");
        root.setPrefSize(420, 700);

        // ── TopBar ──
        HBox topbar = new HBox(10);
        topbar.getStyleClass().add("topbar");
        topbar.setPadding(new Insets(14, 16, 14, 16));
        topbar.setAlignment(Pos.CENTER_LEFT);

        String nome = Formatador.primeiroNome(Session.getInstance().getUsuarioAtual().getNome());
        Label title = new Label("Olá, " + nome);
        title.getStyleClass().add("topbar-title");
        HBox.setHgrow(title, Priority.ALWAYS);

        Button btnLogout = new Button("⏻");
        btnLogout.setStyle("-fx-font-size:18px; -fx-background-color:transparent; -fx-text-fill:white; -fx-cursor:hand; -fx-border-color:transparent;");
        btnLogout.setOnAction(e -> {
            Session.getInstance().logout();
            MainApp.showLogin();
        });

        topbar.getChildren().addAll(title, btnLogout);

        // ── Scroll content ──
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        scroll.setStyle("-fx-background-color: #f5f5f0;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        VBox content = new VBox(12);
        content.setPadding(new Insets(16));

        int uid = Session.getInstance().getUserId();

        try {
            double total = gastoService.totalGasto(uid);
            long qtd = gastoService.contarGastos(uid);
            List<Gasto> recentes = gastoService.listarRecentes(uid, 3);
            Map<Categoria, Double> porCat = gastoService.totalPorCategoria(uid);

            // ── Metrics row ──
            HBox metrics = new HBox(12);
            metrics.setFillHeight(true);

            VBox m1 = metricBox("Total gasto (mês)", Formatador.moeda(total), true);
            VBox m2 = metricBox("Qtd. de gastos", String.valueOf(qtd), false);
            HBox.setHgrow(m1, Priority.ALWAYS);
            HBox.setHgrow(m2, Priority.ALWAYS);
            metrics.getChildren().addAll(m1, m2);

            // ── Gastos recentes ──
            VBox cardRecentes = new VBox(0);
            cardRecentes.getStyleClass().add("card");
            cardRecentes.setPadding(new Insets(16));

            Label secRecentes = new Label("GASTOS RECENTES");
            secRecentes.getStyleClass().add("section-title");
            VBox.setMargin(secRecentes, new Insets(0, 0, 10, 0));

            cardRecentes.getChildren().add(secRecentes);

            if (recentes.isEmpty()) {
                Label empty = new Label("Nenhum gasto ainda.");
                empty.getStyleClass().add("empty-label");
                empty.setMaxWidth(Double.MAX_VALUE);
                cardRecentes.getChildren().add(empty);
            } else {
                for (Gasto g : recentes) {
                    cardRecentes.getChildren().add(GastoItemCard.criar(g, false, null));
                }
            }

            Button btnVerTodos = new Button("📋  Ver todos os gastos");
            btnVerTodos.getStyleClass().add("btn");
            btnVerTodos.setMaxWidth(Double.MAX_VALUE);
            VBox.setMargin(btnVerTodos, new Insets(10, 0, 0, 0));
            btnVerTodos.setOnAction(e ->
                MainApp.primaryStage.getScene().setRoot(new GastosView().getRoot()));
            cardRecentes.getChildren().add(btnVerTodos);

            // ── Por categoria ──
            VBox cardCats = new VBox(8);
            cardCats.getStyleClass().add("card");
            cardCats.setPadding(new Insets(16));

            Label secCats = new Label("GASTOS POR CATEGORIA");
            secCats.getStyleClass().add("section-title");

            cardCats.getChildren().add(secCats);

            if (porCat.isEmpty()) {
                Label empty = new Label("Nenhum dado ainda.");
                empty.getStyleClass().add("empty-label");
                empty.setMaxWidth(Double.MAX_VALUE);
                cardCats.getChildren().add(empty);
            } else {
                for (Map.Entry<Categoria, Double> entry : porCat.entrySet()) {
                    double pct = total > 0 ? entry.getValue() / total : 0;
                    cardCats.getChildren().add(barraCategoria(entry.getKey(), entry.getValue(), pct, total));
                }
            }

            content.getChildren().addAll(metrics, cardRecentes, cardCats);

        } catch (ServiceException e) {
            Label err = new Label("Erro ao carregar dados: " + e.getMessage());
            err.getStyleClass().add("error-label");
            content.getChildren().add(err);
        }

        scroll.setContent(content);

        // ── Navbar ──
        HBox navbar = Navbar.criar(Navbar.Tab.DASHBOARD);

        root.getChildren().addAll(topbar, scroll, navbar);
    }

    private VBox metricBox(String label, String value, boolean danger) {
        VBox box = new VBox(4);
        box.getStyleClass().add("metric-box");
        box.setAlignment(Pos.CENTER);

        Label lbl = new Label(label);
        lbl.getStyleClass().add("metric-label");
        lbl.setWrapText(true);
        lbl.setAlignment(Pos.CENTER);

        Label val = new Label(value);
        val.getStyleClass().add(danger ? "metric-value-danger" : "metric-value");

        box.getChildren().addAll(lbl, val);
        return box;
    }

    private VBox barraCategoria(Categoria cat, double valor, double pct, double total) {
        VBox row = new VBox(6);

        HBox labelRow = new HBox();
        labelRow.setAlignment(Pos.CENTER_LEFT);

        Label catLbl = new Label(cat.getLabel());
        catLbl.setStyle("-fx-font-size:13px; -fx-text-fill:#444;");
        HBox.setHgrow(catLbl, Priority.ALWAYS);

        Label valLbl = new Label(Formatador.moeda(valor) + " (" + Math.round(pct * 100) + "%)");
        valLbl.setStyle("-fx-font-size:13px; -fx-text-fill:#444;");

        labelRow.getChildren().addAll(catLbl, valLbl);

        // Progress track
        StackPane track = new StackPane();
        track.setStyle("-fx-background-color:#f0f0ec; -fx-background-radius:4; -fx-pref-height:8; -fx-max-height:8;");
        track.setMaxWidth(Double.MAX_VALUE);

        String color = catColor(cat);
        Region fill = new Region();
        fill.setStyle("-fx-background-color:" + color + "; -fx-background-radius:4;");

        StackPane.setAlignment(fill, Pos.CENTER_LEFT);

        // We'll bind width via percentage
        track.widthProperty().addListener((obs, o, n) ->
            fill.setPrefWidth(n.doubleValue() * pct)
        );
        track.getChildren().add(fill);

        row.getChildren().addAll(labelRow, track);
        return row;
    }

    private String catColor(Categoria c) {
        return switch (c) {
            case ALIMENTACAO -> "#1D9E75";
            case TRANSPORTE  -> "#378ADD";
            case LAZER       -> "#BA7517";
            case SAUDE       -> "#E24B4A";
            default          -> "#888780";
        };
    }

    public VBox getRoot() { return root; }
}
