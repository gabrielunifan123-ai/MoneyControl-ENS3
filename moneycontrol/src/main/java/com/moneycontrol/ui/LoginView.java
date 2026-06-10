package com.moneycontrol.ui;

import com.moneycontrol.MainApp;
import com.moneycontrol.model.Usuario;
import com.moneycontrol.service.ServiceException;
import com.moneycontrol.service.UsuarioService;
import com.moneycontrol.util.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class LoginView {

    private final VBox root;
    private final UsuarioService usuarioService = new UsuarioService();

    public LoginView() {
        root = new VBox();
        root.setStyle("-fx-background-color: #f5f5f0;");
        root.setPrefSize(420, 700);

        // ── Hero ──
        VBox hero = new VBox(8);
        hero.setAlignment(Pos.CENTER);
        hero.setPadding(new Insets(36, 16, 24, 16));
        hero.setStyle("-fx-background-color: #1D9E75;");

        Label icon = new Label("💰");
        icon.setStyle("-fx-font-size:44px;");

        Label title = new Label("MoneyControl");
        title.getStyleClass().add("login-hero-title");

        Label sub = new Label("Controle seus gastos com facilidade");
        sub.getStyleClass().add("login-hero-sub");

        hero.getChildren().addAll(icon, title, sub);

        // ── Form card ──
        VBox content = new VBox(16);
        content.setPadding(new Insets(20, 20, 20, 20));

        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        Label secTitle = new Label("ENTRAR NA CONTA");
        secTitle.getStyleClass().add("section-title");

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
        senhaField.setPromptText("••••••••");
        senhaField.setMaxWidth(Double.MAX_VALUE);

        Label errLabel = new Label();
        errLabel.getStyleClass().add("error-label");
        errLabel.setWrapText(true);
        errLabel.setVisible(false);
        errLabel.setManaged(false);

        Button btnEntrar = new Button("Entrar");
        btnEntrar.getStyleClass().add("btn-primary");
        btnEntrar.setMaxWidth(Double.MAX_VALUE);

        Button btnCriar = new Button("Criar conta");
        btnCriar.getStyleClass().add("btn");
        btnCriar.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().addAll(secTitle, emailLbl, emailField, senhaLbl, senhaField, errLabel, btnEntrar, btnCriar);

        content.getChildren().add(card);

        // ── Actions ──
        btnEntrar.setOnAction(e -> {
            String email = emailField.getText();
            String senha = senhaField.getText();
            try {
                Usuario u = usuarioService.autenticar(email, senha);
                Session.getInstance().setUsuarioAtual(u);
                showDashboard();
            } catch (ServiceException ex) {
                mostrarErro(errLabel, ex.getMessage());
            }
        });

        senhaField.setOnAction(e -> btnEntrar.fire());

        btnCriar.setOnAction(e -> {
            CadastroView cadastroView = new CadastroView();
            MainApp.primaryStage.getScene().setRoot(cadastroView.getRoot());
        });

        VBox.setVgrow(content, Priority.ALWAYS);
        root.getChildren().addAll(hero, content);
    }

    private void showDashboard() {
        DashboardView dashView = new DashboardView();
        MainApp.primaryStage.getScene().setRoot(dashView.getRoot());
    }

    private void mostrarErro(Label label, String msg) {
        label.setText(msg);
        label.setVisible(true);
        label.setManaged(true);
    }

    public VBox getRoot() { return root; }
}
