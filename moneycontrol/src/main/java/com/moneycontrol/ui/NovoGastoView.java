package com.moneycontrol.ui;

import com.moneycontrol.MainApp;
import com.moneycontrol.model.Categoria;
import com.moneycontrol.service.GastoService;
import com.moneycontrol.service.ServiceException;
import com.moneycontrol.util.Formatador;
import com.moneycontrol.util.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;

public class NovoGastoView {

    private final VBox root;
    private final GastoService gastoService = new GastoService();
    private final int uid = Session.getInstance().getUserId();

    public NovoGastoView() {
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

        Label title = new Label("Novo Gasto");
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

        // ── Summary card ──
        HBox sumCard = new HBox(12);
        sumCard.getStyleClass().add("card");
        sumCard.setPadding(new Insets(14));

        try {
            double total = gastoService.totalGasto(uid);
            long qtd   = gastoService.contarGastos(uid);

            VBox s1 = summaryItem("Total atual", Formatador.moeda(total), true);
            VBox s2 = summaryItem("Registros", qtd + " gastos", false);
            HBox.setHgrow(s1, Priority.ALWAYS);
            HBox.setHgrow(s2, Priority.ALWAYS);
            sumCard.getChildren().addAll(s1, s2);
        } catch (ServiceException e) {
            Label err = new Label("Erro ao carregar resumo.");
            err.getStyleClass().add("error-label");
            sumCard.getChildren().add(err);
        }

        // ── Alert success (hidden) ──
        HBox alertOk = new HBox(8);
        alertOk.setStyle("-fx-background-color:#E1F5EE; -fx-background-radius:8; -fx-padding: 10 14 10 14;");
        alertOk.setAlignment(Pos.CENTER_LEFT);
        alertOk.setVisible(false);
        alertOk.setManaged(false);
        Label alertLbl = new Label("✓  Gasto registrado com sucesso!");
        alertLbl.setStyle("-fx-text-fill:#085041; -fx-font-size:13px;");
        alertOk.getChildren().add(alertLbl);

        // ── Form card ──
        VBox formCard = new VBox(8);
        formCard.getStyleClass().add("card");
        formCard.setPadding(new Insets(20));

        Label secLbl = new Label("INFORMAÇÕES DO GASTO");
        secLbl.getStyleClass().add("section-title");

        Label descLbl = new Label("Descrição");
        descLbl.getStyleClass().add("field-label");
        TextField descField = new TextField();
        descField.getStyleClass().add("inp");
        descField.setPromptText("Ex: Supermercado, Uber, Cinema…");
        descField.setMaxWidth(Double.MAX_VALUE);

        Label valorLbl = new Label("Valor (R$)");
        valorLbl.getStyleClass().add("field-label");
        TextField valorField = new TextField();
        valorField.getStyleClass().add("inp");
        valorField.setPromptText("0,00");
        valorField.setMaxWidth(Double.MAX_VALUE);
        // Allow only numbers and comma/dot
        valorField.textProperty().addListener((obs, o, n) -> {
            if (!n.matches("[0-9]*[.,]?[0-9]*")) valorField.setText(o);
        });

        // Date + Category row
        HBox row = new HBox(10);

        VBox dateCol = new VBox(4);
        HBox.setHgrow(dateCol, Priority.ALWAYS);
        Label dataLbl = new Label("Data");
        dataLbl.getStyleClass().add("field-label");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setStyle("-fx-font-size:14px;");
        dateCol.getChildren().addAll(dataLbl, datePicker);

        VBox catCol = new VBox(4);
        HBox.setHgrow(catCol, Priority.ALWAYS);
        Label catLbl = new Label("Categoria");
        catLbl.getStyleClass().add("field-label");
        ComboBox<Categoria> catCombo = new ComboBox<>();
        catCombo.getItems().addAll(Categoria.values());
        catCombo.getSelectionModel().select(Categoria.OUTROS);
        catCombo.setMaxWidth(Double.MAX_VALUE);
        catCombo.setStyle("-fx-font-size:14px;");
        catCol.getChildren().addAll(catLbl, catCombo);

        row.getChildren().addAll(dateCol, catCol);

        Label errLabel = new Label();
        errLabel.getStyleClass().add("error-label");
        errLabel.setWrapText(true);
        errLabel.setVisible(false);
        errLabel.setManaged(false);

        Button btnSalvar = new Button("Salvar Gasto");
        btnSalvar.getStyleClass().add("btn-primary");
        btnSalvar.setMaxWidth(Double.MAX_VALUE);

        formCard.getChildren().addAll(secLbl, descLbl, descField, valorLbl, valorField, row, errLabel, btnSalvar);

        content.getChildren().addAll(sumCard, alertOk, formCard);
        scroll.setContent(content);

        // ── Actions ──
        btnSalvar.setOnAction(e -> {
            errLabel.setVisible(false);
            errLabel.setManaged(false);
            alertOk.setVisible(false);
            alertOk.setManaged(false);

            String desc = descField.getText();
            String valorStr = valorField.getText().replace(",", ".");
            LocalDate data = datePicker.getValue();
            Categoria cat = catCombo.getValue();

            double valor;
            try {
                valor = Double.parseDouble(valorStr);
            } catch (NumberFormatException ex) {
                mostrarErro(errLabel, "Informe um valor numérico válido.");
                return;
            }

            try {
                gastoService.registrar(desc, valor, data, cat, uid);
                descField.clear();
                valorField.clear();
                datePicker.setValue(LocalDate.now());
                catCombo.getSelectionModel().select(Categoria.OUTROS);
                alertOk.setVisible(true);
                alertOk.setManaged(true);

                // Refresh summary
                try {
                    double novoTotal = gastoService.totalGasto(uid);
                    long novaQtd = gastoService.contarGastos(uid);
                    if (!sumCard.getChildren().isEmpty()) {
                        sumCard.getChildren().clear();
                        VBox s1 = summaryItem("Total atual", Formatador.moeda(novoTotal), true);
                        VBox s2 = summaryItem("Registros", novaQtd + " gastos", false);
                        HBox.setHgrow(s1, Priority.ALWAYS);
                        HBox.setHgrow(s2, Priority.ALWAYS);
                        sumCard.getChildren().addAll(s1, s2);
                    }
                } catch (ServiceException ignored) {}

            } catch (ServiceException ex) {
                mostrarErro(errLabel, ex.getMessage());
            }
        });

        // ── Navbar ──
        HBox navbar = Navbar.criar(Navbar.Tab.NOVO_GASTO);
        root.getChildren().addAll(topbar, scroll, navbar);
    }

    private VBox summaryItem(String label, String value, boolean danger) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color:#f5f5f0; -fx-background-radius:8; -fx-padding:10;");

        Label lbl = new Label(label);
        lbl.getStyleClass().add("metric-label");

        Label val = new Label(value);
        val.setStyle("-fx-font-size:16px; -fx-font-weight:500; -fx-text-fill:" + (danger ? "#a32d2d" : "#222") + ";");

        box.getChildren().addAll(lbl, val);
        return box;
    }

    private void mostrarErro(Label label, String msg) {
        label.setText(msg);
        label.setVisible(true);
        label.setManaged(true);
    }

    public VBox getRoot() { return root; }
}
