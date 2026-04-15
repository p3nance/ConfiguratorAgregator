package com.example.authapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("TechStore");
            primaryStage.setScene(new Scene(root, 1200, 800));
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            primaryStage.setResizable(true);
            primaryStage.show();
        } catch (Exception e) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Ошибка загрузки");
            alert.setHeaderText("Не удалось загрузить интерфейс");
            alert.setContentText("Проверьте наличие файлов в папке resources/views/");
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
