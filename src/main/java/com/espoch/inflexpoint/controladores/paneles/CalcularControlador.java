package com.espoch.inflexpoint.controladores.paneles;

import com.espoch.inflexpoint.modelos.calculos.AnalizadorFuncion;
import com.espoch.inflexpoint.modelos.calculos.DerivadorSimbolico;
import com.espoch.inflexpoint.modelos.calculos.GestorHistorial;
import com.espoch.inflexpoint.modelos.calculos.ResultadoAnalisis;
import com.espoch.inflexpoint.modelos.excepciones.CalculoNumericoException;
import com.espoch.inflexpoint.modelos.excepciones.ExpresionInvalidaException;
import com.espoch.inflexpoint.util.FormulaRenderer;
import com.espoch.inflexpoint.util.GraficadorCanvas;
import com.espoch.inflexpoint.util.TecladoVirtual;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import com.espoch.inflexpoint.util.VentanaUtil;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para la vista de cálculo.
 * Responsabilidades:
 * - Capturar eventos de UI (clics, entrada de texto)
 * - Validar entrada básica (campos no vacíos)
 * - Llamar servicios de cálculo
 * - Actualizar componentes visuales con resultados
 * - Mostrar mensajes al usuario
 * NO contiene lógica de negocio ni cálculos matemáticos.
 */
public class CalcularControlador implements Initializable {
    public VBox vboxResultado;

    // ===== Componentes FXML =====

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
        graficadorCanvas = new GraficadorCanvas(400, 300);
        contenedorGrafica.getChildren().add(graficadorCanvas.getCanvas());

        // Hacer el canvas responsivo escuchando el contenedor (NO bind() + setSize())
        contenedorGrafica.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                graficadorCanvas.establecerTamanio(newVal.doubleValue(), contenedorGrafica.getHeight());
            }
        });
        contenedorGrafica.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                graficadorCanvas.establecerTamanio(contenedorGrafica.getWidth(), newVal.doubleValue());
            }
        });

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

            // 5.1 Guardar en el historial
            GestorHistorial.getInstancia().agregarExpresion(expresion);

            // 6. Graficar usando Canvas interactivo
            System.out.println("Intentando graficar: " + expresion);
            try {
                graficadorCanvas.graficar(expresion, resultado);
                System.out.println("Gráfica completada exitosamente");
            } catch (Exception e) {
                System.out.println("Excepción al graficar: " + e.getMessage());
                e.printStackTrace();
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

    // Válida que la entrada del usuario sea correcta.

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

    // Muestra los resultados textuales en el área correspondiente.
    private void mostrarResultadosTextuales(ResultadoAnalisis resultado) {
        vboxResultadosTexto.getChildren().clear();

        // 1. Derivadas
        if (resultado.getPrimeraDerivada() != null && !resultado.getPrimeraDerivada().isEmpty()) {
            VBox section = crearSeccion("𝑓'(𝑥) DERIVADAS");

            // Renderizar primera derivada
            String latex1 = "f'(x) = " + DerivadorSimbolico.toLaTeX(resultado.getPrimeraDerivada());
            section.getChildren().add(FormulaRenderer.render(latex1));

            if (resultado.getSegundaDerivada() != null && !resultado.getSegundaDerivada().isEmpty()) {
                // Renderizar segunda derivada
                String latex2 = "f''(x) = " + DerivadorSimbolico.toLaTeX(resultado.getSegundaDerivada());
                section.getChildren().add(FormulaRenderer.render(latex2));
            }
            vboxResultadosTexto.getChildren().add(section);
        }

        // 2. Puntos Críticos
        if (resultado.getPuntosCriticos() != null && resultado.getPuntosCriticos().length > 0) {
            VBox section = crearSeccion("PUNTOS CRÍTICOS");
            for (com.espoch.inflexpoint.modelos.entidades.PuntoCritico pc : resultado.getPuntosCriticos()) {
                section.getChildren().add(crearEtiquetaDual(pc.getTipoPuntoCritico() + ":",
                        String.format("(%.4f, %.4f)", pc.getX(), pc.getY())));
            }
            vboxResultadosTexto.getChildren().add(section);
        }

        // 3. Puntos de Inflexión
        if (resultado.getPuntosInflexion() != null && resultado.getPuntosInflexion().length > 0) {
            VBox section = crearSeccion("PUNTOS DE INFLEXIÓN");
            for (com.espoch.inflexpoint.modelos.entidades.PuntoCritico pi : resultado.getPuntosInflexion()) {
                section.getChildren().add(crearEtiquetaDual("Inflexión en:",
                        String.format("(%.4f, %.4f)", pi.getX(), pi.getY())));
            }
            vboxResultadosTexto.getChildren().add(section);
        }

        // 4. Intervalos de Crecimiento/Decrecimiento
        boolean hasCrecimiento = resultado.getIntervalosCrecimiento() != null
                && resultado.getIntervalosCrecimiento().length > 0;
        boolean hasDecr = resultado.getIntervalosDecrecimiento() != null
                && resultado.getIntervalosDecrecimiento().length > 0;

        if (hasCrecimiento || hasDecr) {
            VBox section = crearSeccion("INTERVALOS");
            if (hasCrecimiento) {
                for (com.espoch.inflexpoint.modelos.entidades.Intervalo inter : resultado.getIntervalosCrecimiento()) {
                    section.getChildren().add(crearEtiquetaDual("Creciente:", formatearIntervalo(inter)));
                }
            }
            if (hasDecr) {
                for (com.espoch.inflexpoint.modelos.entidades.Intervalo inter : resultado
                        .getIntervalosDecrecimiento()) {
                    section.getChildren().add(crearEtiquetaDual("Decreciente:", formatearIntervalo(inter)));
                }
            }
            vboxResultadosTexto.getChildren().add(section);
        }

        // 5. Concavidad
        if (resultado.intervalosConcavidad() != null && resultado.intervalosConcavidad().length > 0) {
            VBox section = crearSeccion("CONCAVIDAD");
            for (com.espoch.inflexpoint.modelos.entidades.Intervalo inter : resultado.intervalosConcavidad()) {
                String label = inter.getTipoIntervalo().toString().contains("POSITIVA") ? "Cóncava (∪):"
                        : "Convexa (∩):";
                section.getChildren().add(crearEtiquetaDual(label, formatearIntervalo(inter)));
            }
            vboxResultadosTexto.getChildren().add(section);
        }

        // Caso sin resultados
        if (vboxResultadosTexto.getChildren().isEmpty()) {
            VBox emptyBox = new VBox(10);
            emptyBox.setAlignment(javafx.geometry.Pos.CENTER);
            emptyBox.setPadding(new javafx.geometry.Insets(20));

            Label lblEmpty = new Label("No se encontraron resultados para la función o las opciones seleccionadas.");
            lblEmpty.setWrapText(true);
            lblEmpty.setStyle("-fx-text-fill: #999; -fx-font-style: italic; -fx-text-alignment: center;");

            emptyBox.getChildren().add(lblEmpty);
            vboxResultadosTexto.getChildren().add(emptyBox);
        }
    }

    private VBox crearSeccion(String title) {
        VBox card = new VBox(8);
        card.getStyleClass().add("results-card");

        Label lblTitle = new Label(title);
        lblTitle.getStyleClass().add("results-section-title");
        lblTitle.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().add(lblTitle);
        return card;
    }

    private HBox crearEtiquetaDual(String key, String value) {
        HBox hbox = new HBox(5);
        hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label lblKey = new Label(key);
        lblKey.getStyleClass().add("result-label-key");

        Label lblValue = new Label(value);
        lblValue.getStyleClass().add("result-label-value");
        if (value.startsWith("(")) {
            lblValue.getStyleClass().add("coordinate-badge");
        }

        hbox.getChildren().addAll(lblKey, lblValue);
        return hbox;
    }

    private Label crearEtiquetaFormula(String formula) {
        Label label = new Label(formula);
        label.getStyleClass().add("derivative-formula");
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    private String formatearIntervalo(com.espoch.inflexpoint.modelos.entidades.Intervalo intervalo) {
        String inicio = intervalo.getInicio() == null ? "-∞" : String.format("%.2f", intervalo.getInicio());
        String fin = intervalo.getFin() == null ? "∞" : String.format("%.2f", intervalo.getFin());
        return String.format("(%s, %s)", inicio, fin);
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
        VentanaUtil.aplicarIcono(alert);
        alert.showAndWait();
    }

    /**
     * Carga una función y ejecuta el cálculo automáticamente.
     */
    public void cargarYCalcular(String expresion) {
        txtFuncion.setText(expresion);
        // Seleccionamos todas las opciones por defecto para un análisis completo
        chkPuntosCriticos.setSelected(true);
        chkIntervalos.setSelected(true);
        chkMaxMin.setSelected(true);
        chkPuntoInflexion.setSelected(true);
        chkConcavidad.setSelected(true);

        calcular(null);
    }

    @FXML
    private void verHistorial(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/espoch/inflexpoint/paneles/historial-funciones-inflex.fxml"));
            Parent root = loader.load();

            HistorialFuncionesControlador controller = loader.getController();
            controller.setCalcularControlador(this);

            Stage stage = new Stage();
            stage.setTitle("Historial de Funciones");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            VentanaUtil.aplicarIcono(stage);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el historial: " + e.getMessage());
        }
    }
}
