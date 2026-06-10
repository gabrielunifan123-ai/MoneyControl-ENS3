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

import java.util.List;
import java.util.Map;

public class RelatorioView {

    private final VBox root;
    private final GastoService gastoService = new GastoService();
    private final int uid = Session.getInstance().getUserId();

    private static final Map<Categoria, String> TAG_STYLE = Map.of(
        Categoria.ALIMENTACAO, "-fx-background-color:#E1F5EE; -fx-text-fill:#085041;",
        Categoria.TRANSPORTE,  "-fx-background-color:#E6F1FB; -fx-text-fill:#0C447C;",
        Categoria.LAZER,       "-fx-background-color:#FAEEDA; -fx-text-fill:#633806;",
        Categoria.SAUDE,       "-fx-background-color:#FCEBEB; -fx-text-fill:#791F1F;",
        Categoria.OUTROS,      "-fx-background-color:#F1EFE8; -fx-text-fill:#444441;"
    );

    public RelatorioView() {
        root = new VBox();
        root.setStyle("-fx-background-color: #f5f5f0;");
        root.setPrefSize(420, 700);

        // ── TopBar ──
        HBox topbar = new HBox(10);
        topbar.getStyleClass().add("topbar");
        topbar.setPadding(new Insets(14, 16, 14, 16));
        topbar.setAlignment(Pos.CENTER_LEFT);

        Button btnBack = new Button("←");
        btnBack.setStyle("-fx-font-size:20px; -fx-background-color:transparent; -fx-text-fill:white; -fx-cursor:hand; -fx-border-color:transparent;");
        btnBack.setOnAction(e -> MainApp.primaryStage.getScene().setRoot(new DashboardView().getRoot()));

        Label title = new Label("Relatório de Gastos");
        title.getStyleClass().add("topbar-title");
        HBox.setHgrow(title, Priority.ALWAYS);
        topbar.getChildren().addAll(btnBack, title);

        // ── Scroll ──
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        scroll.setStyle("-fx-background-color: #f5f5f0;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        VBox content = new VBox(12);
        content.setPadding(new Insets(16));

        try {
            double total  = gastoService.totalGasto(uid);
            double maior  = gastoService.maiorGasto(uid);
            Map<Categoria, Double> porCat = gastoService.totalPorCategoria(uid);
            List<Gasto> todos = gastoService.listar(uid);

            // ── Metrics ──
            HBox metrics = new HBox(12);

            VBox m1 = metricBox("Total gasto", Formatador.moeda(total), true);
            VBox m2 = metricBox("Maior gasto", Formatador.moeda(maior), true);
            HBox.setHgrow(m1, Priority.ALWAYS);
            HBox.setHgrow(m2, Priority.ALWAYS);
            metrics.getChildren().addAll(m1, m2);

            // ── Por categoria ──
            VBox catCard = new VBox(10);
            catCard.getStyleClass().add("card");
            catCard.setPadding(new Insets(16));

            Label catSec = new Label("POR CATEGORIA");
            catSec.getStyleClass().add("section-title");
            catCard.getChildren().add(catSec);

            if (porCat.isEmpty()) {
                Label empty = new Label("Nenhum dado ainda.");
                empty.getStyleClass().add("empty-label");
                empty.setMaxWidth(Double.MAX_VALUE);
                catCard.getChildren().add(empty);
            } else {
                for (Map.Entry<Categoria, Double> entry : porCat.entrySet()) {
                    double pct = total > 0 ? entry.getValue() / total : 0;
                    catCard.getChildren().add(barraCategoria(entry.getKey(), entry.getValue(), pct));
                }
            }

            // ── Todos lançamentos ──
            VBox listaCard = new VBox(0);
            listaCard.getStyleClass().add("card");
            listaCard.setPadding(new Insets(16));

            Label listaSec = new Label("TODOS OS LANÇAMENTOS");
            listaSec.getStyleClass().add("section-title");
            VBox.setMargin(listaSec, new Insets(0, 0, 10, 0));
            listaCard.getChildren().add(listaSec);

            if (todos.isEmpty()) {
                Label empty = new Label("Nenhum lançamento ainda.");
                empty.getStyleClass().add("empty-label");
                empty.setMaxWidth(Double.MAX_VALUE);
                listaCard.getChildren().add(empty);
            } else {
                for (Gasto g : todos) {
                    listaCard.getChildren().add(GastoItemCard.criar(g, false, null));
                }
            }

            content.getChildren().addAll(metrics, catCard, listaCard);

        } catch (ServiceException e) {
            Label err = new Label("Erro: " + e.getMessage());
            err.getStyleClass().add("error-label");
            content.getChildren().add(err);
        }

        scroll.setContent(content);

        // ── Navbar ──
        HBox navbar = Navbar.criar(Navbar.Tab.RELATORIO);
        root.getChildren().addAll(topbar, scroll, navbar);
    }

    private VBox metricBox(String label, String value, boolean danger) {
        VBox box = new VBox(4);
        box.getStyleClass().add("metric-box");
        box.setAlignment(Pos.CENTER);

        Label lbl = new Label(label);
        lbl.getStyleClass().add("metric-label");

        Label val = new Label(value);
        val.getStyleClass().add(danger ? "metric-value-danger" : "metric-value");
        val.setStyle("-fx-font-size:18px; -fx-font-weight:500;" + (danger ? "-fx-text-fill:#a32d2d;" : "-fx-text-fill:#222;"));

        box.getChildren().addAll(lbl, val);
        return box;
    }

    private VBox barraCategoria(Categoria cat, double valor, double pct) {
        VBox row = new VBox(6);

        HBox labelRow = new HBox(6);
        labelRow.setAlignment(Pos.CENTER_LEFT);

        String tagStyle = TAG_STYLE.getOrDefault(cat, "-fx-background-color:#F1EFE8; -fx-text-fill:#444;");
        Label catTag = new Label(cat.getLabel());
        catTag.setStyle(tagStyle + " -fx-font-size:11px; -fx-font-weight:500; -fx-padding:2 8 2 8; -fx-background-radius:20;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label valLbl = new Label(Formatador.moeda(valor) + " · " + Math.round(pct * 100) + "%");
        valLbl.setStyle("-fx-font-size:13px; -fx-text-fill:#444;");

        labelRow.getChildren().addAll(catTag, spacer, valLbl);

        StackPane track = new StackPane();
        track.setStyle("-fx-background-color:#f0f0ec; -fx-background-radius:4; -fx-pref-height:8; -fx-max-height:8;");
        track.setMaxWidth(Double.MAX_VALUE);

        String color = catColor(cat);
        Region fill = new Region();
        fill.setStyle("-fx-background-color:" + color + "; -fx-background-radius:4;");
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);

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
