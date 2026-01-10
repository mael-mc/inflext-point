package com.espoch.inflexpoint.controladores.paneles;

import com.espoch.inflexpoint.modelos.calculos.HistoryManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HistorialFuncionesControlador implements Initializable {

    @FXML
    private VBox vboxHistorial;

    private CalcularControlador calcularControlador;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarHistorial();
    }

    public void setCalcularControlador(CalcularControlador calcularControlador) {
        this.calcularControlador = calcularControlador;
    }

    private void cargarHistorial() {
        List<String> history = HistoryManager.getInstance().getHistory();

        for (String expression : history) {
            Button btn = new Button(expression);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle(
                    "-fx-background-color: #f4f4f4; -fx-border-color: #ddd; -fx-alignment: CENTER_LEFT; -fx-padding: 8 15;");

            btn.setOnAction(event -> {
                if (calcularControlador != null) {
                    calcularControlador.cargarYCalcular(expression);
                    // Cerrar la ventana del historial
                    Stage stage = (Stage) btn.getScene().getWindow();
                    stage.close();
                }
            });

            vboxHistorial.getChildren().add(btn);
        }
    }
}
