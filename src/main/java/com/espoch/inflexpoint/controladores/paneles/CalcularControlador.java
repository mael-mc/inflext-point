package com.espoch.inflexpoint.controladores.paneles;

import com.espoch.inflexpoint.modelos.calculos.AnalizadorFuncion;
import com.espoch.inflexpoint.modelos.calculos.Evaluador;
import com.espoch.inflexpoint.modelos.calculos.ResultadoAnalisis;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador responsable por manejar la vista de cálculo.
 */
public class CalcularControlador implements Initializable {

    @FXML
    private ComboBox<String> cbTipoFuncion;

    @FXML
    private RadioButton chkPuntosCriticos;
    @FXML
    private RadioButton chkIntervalos;
    @FXML
    private RadioButton chkMaxMin;
    @FXML
    private RadioButton chkPuntoInflexion;
    @FXML
    private RadioButton chkConcavidad;

    @FXML
    private TextField txtFuncion;

    @FXML
    private Button btnTeclado;
    @FXML
    private Button btnCalcular;
    @FXML
    private Button btnBorrar;

    @FXML
    private VBox vboxResultadosTexto;
    @FXML
    private HBox contenedorGrafica;

    // Gráfica
    private LineChart<Number, Number> lineChart;
    private NumberAxis xAxis;
    private NumberAxis yAxis;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar ComboBox
        cbTipoFuncion.getItems().addAll(
                "Lineal",
                "Cuadrática",
                "Trigonométrica",
                "Logarítmica",
                "Exponencial");

        // Inicializar Gráfica
        xAxis = new NumberAxis();
        xAxis.setLabel("X");
        yAxis = new NumberAxis();
        yAxis.setLabel("f(x)");
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setPrefWidth(578);
        lineChart.setPrefHeight(350);
        lineChart.setCreateSymbols(false); // No mostrar puntos individuales

        contenedorGrafica.getChildren().add(lineChart);
    }

    @FXML
    private void calcular(ActionEvent event) {
        String funcion = txtFuncion.getText();
        String tipoFuncion = cbTipoFuncion.getValue();

        if (funcion == null || funcion.trim().isEmpty()) {
            mostrarAlerta("Error", "Debe ingresar una función.");
            return;
        }

        if (!chkPuntosCriticos.isSelected() && !chkIntervalos.isSelected() &&
                !chkMaxMin.isSelected() && !chkPuntoInflexion.isSelected() &&
                !chkConcavidad.isSelected()) {
            mostrarAlerta("Error", "Debe seleccionar al menos una opción de cálculo.");
            return;
        }

        // Limpiar resultados previos
        vboxResultadosTexto.getChildren().clear();
        lineChart.getData().clear();

        try {
            // 1. Análisis Analítico
            AnalizadorFuncion analizador = new AnalizadorFuncion();
            ResultadoAnalisis resultado = analizador.analizar(
                    funcion,
                    chkPuntosCriticos.isSelected(),
                    chkIntervalos.isSelected(),
                    chkMaxMin.isSelected(),
                    chkPuntoInflexion.isSelected(),
                    chkConcavidad.isSelected());

            // Mostrar Texto
            Label lblResumen = new Label(resultado.getResumen());
            lblResumen.setWrapText(true);
            lblResumen.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 14px;");
            vboxResultadosTexto.getChildren().add(lblResumen);

            // 2. Gráfica
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(funcion);

            Evaluador evaluador = new Evaluador(funcion);
            for (double x = -10.0; x <= 10.0; x += 0.1) {
                try {
                    double y = evaluador.evaluar(x);
                    // Evitar valores infinitos o NaN en la gráfica
                    if (!Double.isNaN(y) && !Double.isInfinite(y) && Math.abs(y) < 100) {
                        series.getData().add(new XYChart.Data<>(x, y));
                    }
                } catch (Exception e) {
                    // Ignorar puntos no evaluables
                }
            }
            lineChart.getData().add(series);

        } catch (Exception e) {
            mostrarAlerta("Error de Cálculo", "No se pudo procesar la función: " + e.getMessage());
        }
    }

    @FXML
    private void borrar(ActionEvent event) {
        txtFuncion.clear();
        cbTipoFuncion.getSelectionModel().clearSelection();
        chkPuntosCriticos.setSelected(false);
        chkIntervalos.setSelected(false);
        chkMaxMin.setSelected(false);
        chkPuntoInflexion.setSelected(false);
        chkConcavidad.setSelected(false);
        vboxResultadosTexto.getChildren().clear();
        lineChart.getData().clear();
    }

    @FXML
    private void mostrarTeclado(ActionEvent event) {
        mostrarAlerta("Información", "Teclado virtual en desarrollo.");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
