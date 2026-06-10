package com.moneycontrol.ui;

import com.moneycontrol.MainApp;
import com.moneycontrol.model.Categoria;
import com.moneycontrol.model.Gasto;
import com.moneycontrol.service.GastoService;
import com.moneycontrol.service.ServiceException;
import com.moneycontrol.util.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class GastosView {

    private final VBox root;
    private final GastoService gastoService = new GastoService();
    private final int uid = Session.getInstance().getUserId();
    private VBox listaBox;

    public GastosView() {
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

        Label title = new Label("Meus Gastos");
        title.getStyleClass().add("topbar-title");
        HBox.setHgrow(title, Priority.ALWAYS);

        topbar.getChildren().addAll(btnBack, title);

        // ── Content ──
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        scroll.setStyle("-fx-background-color: #f5f5f0;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        VBox content = new VBox(12);
        content.setPadding(new Insets(16));

        // Filter
        VBox filterCard = new VBox(8);
        filterCard.getStyleClass().add("card");
        filterCard.setPadding(new Insets(12, 16, 12, 16));

        Label filtroLbl = new Label("FILTRAR POR CATEGORIA");
        filtroLbl.getStyleClass().add("section-title");

        ComboBox<String> filtroCat = new ComboBox<>();
        filtroCat.getStyleClass().add("inp");
        filtroCat.setMaxWidth(Double.MAX_VALUE);
        filtroCat.getItems().add("Todas as categorias");
        for (Categoria c : Categoria.values()) filtroCat.getItems().add(c.getLabel());
        filtroCat.getSelectionModel().selectFirst();

        filterCard.getChildren().addAll(filtroLbl, filtroCat);

        // Lista card
        VBox listaCard = new VBox(0);
        listaCard.getStyleClass().add("card");
        listaCard.setPadding(new Insets(16));

        Label secLbl = new Label("LANÇAMENTOS");
        secLbl.getStyleClass().add("section-title");
        VBox.setMargin(secLbl, new Insets(0, 0, 10, 0));

        listaBox = new VBox(0);
        listaCard.getChildren().addAll(secLbl, listaBox);

        content.getChildren().addAll(filterCard, listaCard);
        scroll.setContent(content);

        // ── Actions ──
        filtroCat.setOnAction(e -> {
            String sel = filtroCat.getValue();
            renderLista(sel.equals("Todas as categorias") ? null : sel);
        });

        renderLista(null);

        // ── Navbar ──
        HBox navbar = Navbar.criar(Navbar.Tab.GASTOS);
        root.getChildren().addAll(topbar, scroll, navbar);
    }

    private void renderLista(String categoria) {
        listaBox.getChildren().clear();
        try {
            List<Gasto> lista = categoria == null
                ? gastoService.listar(uid)
                : gastoService.listarPorCategoria(uid, categoria);

            if (lista.isEmpty()) {
                Label empty = new Label("Nenhum gasto encontrado.");
                empty.getStyleClass().add("empty-label");
                empty.setMaxWidth(Double.MAX_VALUE);
                listaBox.getChildren().add(empty);
            } else {
                for (Gasto g : lista) {
                    listaBox.getChildren().add(GastoItemCard.criar(g, true, gasto -> {
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Remover o gasto \"" + gasto.getDescricao() + "\"?",
                            ButtonType.YES, ButtonType.NO);
                        confirm.setTitle("Confirmar remoção");
                        confirm.setHeaderText(null);
                        confirm.showAndWait().ifPresent(btn -> {
                            if (btn == ButtonType.YES) {
                                try {
                                    gastoService.remover(gasto.getId());
                                    renderLista(categoria);
                                } catch (ServiceException ex) {
                                    new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                                }
                            }
                        });
                    }));
                }
            }
        } catch (ServiceException e) {
            Label err = new Label("Erro: " + e.getMessage());
            err.getStyleClass().add("error-label");
            listaBox.getChildren().add(err);
        }
    }

    public VBox getRoot() { return root; }
}
