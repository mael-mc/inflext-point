package com.espoch.inflexpoint.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/espoch/inflexpoint/vistaprincipal/vista-principal.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("InflexPoint");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/espoch/inflexpoint/imagenes/Logo/inflex-point-logo.jpeg")));
        stage.setWidth(1000);
        stage.setHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}