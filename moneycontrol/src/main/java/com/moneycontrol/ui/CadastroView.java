package com.moneycontrol.ui;

import com.moneycontrol.MainApp;
import com.moneycontrol.service.ServiceException;
import com.moneycontrol.service.UsuarioService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CadastroView {

    private final VBox root;
    private final UsuarioService usuarioService = new UsuarioService();

    public CadastroView() {
        root = new VBox();
        root.setStyle("-fx-background-color: #f5f5f0;");
        root.setPrefSize(420, 700);

        // ── TopBar ──
        HBox topbar = new HBox(10);
        topbar.getStyleClass().add("topbar");
        topbar.setPadding(new Insets(14, 16, 14, 16));
        topbar.setAlignment(Pos.CENTER_LEFT);

        Button btnBack = new Button("←");
        btnBack.getStyleClass().add("topbar-icon");
        btnBack.setStyle("-fx-font-size:20px; -fx-background-color:transparent; -fx-text-fill:white; -fx-cursor:hand;");

        Label title = new Label("Criar conta");
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
        content.setPadding(new Insets(20));
        scroll.setContent(content);

        // Success alert (hidden initially)
        HBox alertSuccess = new HBox(8);
        alertSuccess.getStyleClass().add("alert-success");
        alertSuccess.setVisible(false);
        alertSuccess.setManaged(false);
        alertSuccess.setPadding(new Insets(10, 14, 10, 14));
        Label alertIcon = new Label("✓");
        Label alertMsg = new Label("Conta criada com sucesso!");
        Hyperlink alertLink = new Hyperlink("Fazer login");
        alertSuccess.getChildren().addAll(alertIcon, alertMsg, alertLink);

        // Form card
        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        Label nomeLbl = new Label("Nome completo");
        nomeLbl.getStyleClass().add("field-label");
        TextField nomeField = new TextField();
        nomeField.getStyleClass().add("inp");
        nomeField.setPromptText("Gabriel Henrique");
        nomeField.setMaxWidth(Double.MAX_VALUE);

        Label emailLbl = new Label("E-mail");
        emailLbl.getStyleClass().add("field-label");
        TextField emailField = new TextField();
        emailField.getStyleClass().add("inp");
        emailField.setPromptText("seu@email.com");
        emailField.setMaxWidth(Double.MAX_VALUE);

        Label senhaLbl = new Label("Senha");
        senhaLbl.getStyleClass().add("field-label");
        PasswordField senhaField = new PasswordField();
        senhaField.getStyleClass().add("inp");
        senhaField.setPromptText("Mínimo 8 caracteres");
        senhaField.setMaxWidth(Double.MAX_VALUE);

        Label confLbl = new Label("Confirmar senha");
        confLbl.getStyleClass().add("field-label");
        PasswordField confField = new PasswordField();
        confField.getStyleClass().add("inp");
        confField.setPromptText("Repita a senha");
        confField.setMaxWidth(Double.MAX_VALUE);

        Label errLabel = new Label();
        errLabel.getStyleClass().add("error-label");
        errLabel.setWrapText(true);
        errLabel.setVisible(false);
        errLabel.setManaged(false);

        Button btnCadastrar = new Button("Cadastrar");
        btnCadastrar.getStyleClass().add("btn-primary");
        btnCadastrar.setMaxWidth(Double.MAX_VALUE);

        Button btnJaTenho = new Button("Já tenho conta");
        btnJaTenho.getStyleClass().add("btn");
        btnJaTenho.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().addAll(
                nomeLbl, nomeField, emailLbl, emailField,
                senhaLbl, senhaField, confLbl, confField,
                errLabel, btnCadastrar, btnJaTenho
        );

        content.getChildren().addAll(alertSuccess, card);

        // ── Actions ──
        btnBack.setOnAction(e -> MainApp.showLogin());
        btnJaTenho.setOnAction(e -> MainApp.showLogin());

        alertLink.setOnAction(e -> MainApp.showLogin());

        btnCadastrar.setOnAction(e -> {
            String nome = nomeField.getText();
            String email = emailField.getText();
            String senha = senhaField.getText();
            String conf = confField.getText();

            errLabel.setVisible(false);
            errLabel.setManaged(false);

            if (!senha.equals(conf)) {
                mostrarErro(errLabel, "As senhas não coincidem.");
                return;
            }

            try {
                usuarioService.cadastrar(nome, email, senha);
                card.setVisible(false);
                card.setManaged(false);
                alertSuccess.setVisible(true);
                alertSuccess.setManaged(true);
            } catch (ServiceException ex) {
                mostrarErro(errLabel, ex.getMessage());
            }
        });

        root.getChildren().addAll(topbar, scroll);
    }

    private void mostrarErro(Label label, String msg) {
        label.setText(msg);
        label.setVisible(true);
        label.setManaged(true);
    }

    public VBox getRoot() { return root; }
}
