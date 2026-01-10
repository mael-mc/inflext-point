package com.espoch.inflexpoint.controladores.paneles;

import com.espoch.inflexpoint.modelos.calculos.HistoryManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HistorialFuncionesControlador implements Initializable {

    @FXML
    private VBox vboxHistorial;

    @FXML
    private Button btnLimpiar;

    private CalcularControlador calcularControlador;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarHistorial();
    }

    public void setCalcularControlador(CalcularControlador calcularControlador) {
        this.calcularControlador = calcularControlador;
    }

    @FXML
    private void onLimpiarHistorial(ActionEvent event) {
        HistoryManager.getInstance().clearHistory();
        vboxHistorial.getChildren().clear();
    }

    private void cargarHistorial() {
        vboxHistorial.getChildren().clear();
        List<String> history = HistoryManager.getInstance().getHistory();

        for (String expression : history) {
            HBox itemContainer = new HBox(5);
            itemContainer.setAlignment(Pos.CENTER_LEFT);
            itemContainer.setStyle("-fx-padding: 5; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;");

            Button btnFunc = new Button(expression);
            btnFunc.setMaxWidth(Double.MAX_VALUE);
            btnFunc.setStyle(
                    "-fx-background-color: transparent; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");
            HBox.setHgrow(btnFunc, Priority.ALWAYS);

            btnFunc.setOnAction(event -> {
                if (calcularControlador != null) {
                    calcularControlador.cargarYCalcular(expression);
                    // Cerrar la ventana del historial
                    Stage stage = (Stage) btnFunc.getScene().getWindow();
                    stage.close();
                }
            });

            Button btnDelete = new Button("X");
            btnDelete.setStyle(
                    "-fx-background-color: #ff5252; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 2 8; -fx-cursor: hand;");

            btnDelete.setOnAction(event -> {
                HistoryManager.getInstance().removeExpression(expression);
                cargarHistorial();
            });

            itemContainer.getChildren().addAll(btnFunc, btnDelete);
            vboxHistorial.getChildren().add(itemContainer);
        }
    }
}
