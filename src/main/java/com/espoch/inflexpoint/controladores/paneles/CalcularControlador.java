package com.espoch.inflexpoint.controladores.paneles;

import com.espoch.inflexpoint.modelos.calculos.AnalizadorFuncion;
import com.espoch.inflexpoint.modelos.calculos.ResultadoAnalisis;
import com.espoch.inflexpoint.modelos.excepciones.CalculoNumericoException;
import com.espoch.inflexpoint.modelos.excepciones.ExpresionInvalidaException;
import com.espoch.inflexpoint.util.GraficadorCanvas;
import com.espoch.inflexpoint.util.TecladoVirtual;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para la vista de cálculo.
 * 
 * Responsabilidades:
 * - Capturar eventos de UI (clics, entrada de texto)
 * - Validar entrada básica (campos no vacíos)
 * - Llamar servicios de cálculo
 * - Actualizar componentes visuales con resultados
 * - Mostrar mensajes al usuario
 * 
 * NO contiene lógica de negocio ni cálculos matemáticos.
 */
public class CalcularControlador implements Initializable {

    // ===== Componentes FXML =====

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

    // ===== Servicios y Utilidades =====

    private AnalizadorFuncion analizador;
    private GraficadorCanvas graficadorCanvas;
    private TecladoVirtual tecladoVirtual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar servicios
        analizador = new AnalizadorFuncion();

        // Inicializar graficador con Canvas
        graficadorCanvas = new GraficadorCanvas(800, 600);
        contenedorGrafica.getChildren().add(graficadorCanvas.getCanvas());

        // Hacer el canvas responsivo escuchando el contenedor (NO bind() + setSize())
        contenedorGrafica.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                graficadorCanvas.setSize(newVal.doubleValue(), contenedorGrafica.getHeight());
            }
        });
        contenedorGrafica.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                graficadorCanvas.setSize(contenedorGrafica.getWidth(), newVal.doubleValue());
            }
        });

        // Inicializar ComboBox (solo para información, no se usa en cálculos)
        cbTipoFuncion.getItems().addAll(
                "Lineal",
                "Cuadrática",
                "Trigonométrica",
                "Logarítmica",
                "Exponencial",
                "Polinómica",
                "Racional");
    }

    /**
     * Maneja el evento de cálculo.
     * Valida entrada, llama servicios, y actualiza UI.
     */
    @FXML
    private void calcular(ActionEvent event) {
        // 1. Obtener datos de entrada
        String expresion = txtFuncion.getText();

        // 2. Validar entrada básica
        if (!validarEntrada(expresion)) {
            return;
        }

        // 3. Limpiar resultados previos
        limpiarResultados();

        try {
            // 4. Llamar servicio de análisis
            ResultadoAnalisis resultado = analizador.analizar(
                    expresion,
                    chkPuntosCriticos.isSelected(),
                    chkIntervalos.isSelected(),
                    chkMaxMin.isSelected(),
                    chkPuntoInflexion.isSelected(),
                    chkConcavidad.isSelected());

            // 5. Mostrar resultados textuales
            mostrarResultadosTextuales(resultado);

            // 6. Graficar usando Canvas interactivo
            try {
                graficadorCanvas.graficar(expresion, resultado);
            } catch (Exception e) {
                mostrarAlerta("Error en Gráfica",
                        "No se pudo graficar la función: " + e.getMessage());
            }

        } catch (ExpresionInvalidaException e) {
            mostrarAlerta("Expresión Inválida",
                    "La expresión ingresada no es válida:\n" + e.getMessage());
        } catch (CalculoNumericoException e) {
            mostrarAlerta("Error de Cálculo",
                    "Ocurrió un error durante el análisis:\n" + e.getMessage());
        } catch (Exception e) {
            mostrarAlerta("Error Inesperado",
                    "Ocurrió un error inesperado:\n" + e.getMessage());
        }
    }

    /**
     * Valida que la entrada del usuario sea correcta.
     */
    private boolean validarEntrada(String expresion) {
        // Validar que la expresión no esté vacía
        if (expresion == null || expresion.trim().isEmpty()) {
            mostrarAlerta("Campo Vacío", "Debe ingresar una función.");
            return false;
        }

        // Validar que al menos una opción esté seleccionada
        if (!chkPuntosCriticos.isSelected() &&
                !chkIntervalos.isSelected() &&
                !chkMaxMin.isSelected() &&
                !chkPuntoInflexion.isSelected() &&
                !chkConcavidad.isSelected()) {

            mostrarAlerta("Sin Opciones",
                    "Debe seleccionar al menos una opción de cálculo.");
            return false;
        }

        return true;
    }

    /**
     * Muestra los resultados textuales en el área correspondiente.
     */
    private void mostrarResultadosTextuales(ResultadoAnalisis resultado) {
        // Generar resumen usando el método de ResultadoAnalisis
        String resumen = resultado.generarResumen();

        // Crear label con el resumen
        Label lblResumen = new Label(resumen);
        lblResumen.setWrapText(true);
        lblResumen.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 13px;");

        // Añadir al contenedor
        vboxResultadosTexto.getChildren().add(lblResumen);
    }

    /**
     * Limpia los resultados anteriores.
     */
    private void limpiarResultados() {
        vboxResultadosTexto.getChildren().clear();
        // El canvas ya está en el contenedor, solo se redibuja
    }

    /**
     * Maneja el evento de borrar.
     * Limpia todos los campos y resultados.
     */
    @FXML
    private void borrar(ActionEvent event) {
        txtFuncion.clear();
        cbTipoFuncion.getSelectionModel().clearSelection();
        chkPuntosCriticos.setSelected(false);
        chkIntervalos.setSelected(false);
        chkMaxMin.setSelected(false);
        chkPuntoInflexion.setSelected(false);
        chkConcavidad.setSelected(false);
        limpiarResultados();
    }

    /**
     * Muestra el teclado virtual.
     */
    @FXML
    private void mostrarTeclado(ActionEvent event) {
        if (tecladoVirtual == null) {
            tecladoVirtual = new TecladoVirtual();
        }
        tecladoVirtual.mostrar(txtFuncion, btnTeclado);
    }

    /**
     * Muestra un cuadro de diálogo de alerta al usuario.
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
