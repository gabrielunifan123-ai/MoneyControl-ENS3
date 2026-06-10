package com.moneycontrol;

import com.moneycontrol.dao.DatabaseConnection;
import com.moneycontrol.ui.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setTitle("MoneyControl");
        stage.setResizable(false);
        stage.setWidth(420);
        stage.setHeight(700);

        showLogin();
        stage.show();
    }

    public static void showLogin() {
        LoginView loginView = new LoginView();
        Scene scene = new Scene(loginView.getRoot(), 420, 700);
        scene.getStylesheets().add(MainApp.class.getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    @Override
    public void stop() {
        DatabaseConnection.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
