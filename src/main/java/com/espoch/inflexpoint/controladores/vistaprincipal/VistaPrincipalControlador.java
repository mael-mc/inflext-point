package com.espoch.inflexpoint.controladores.vistaprincipal;

import com.espoch.inflexpoint.controladores.paneles.CalcularControlador;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.util.Objects;

public class VistaPrincipalControlador {

    @FXML
    private HBox hBoxLogo;
    @FXML
    private TextField txtBuscar;
    @FXML
    private Button btnSalir;

    // Toggle Buttons del menú
    @FXML
    private ToggleButton btnInicio;
    @FXML
    private ToggleButton btnCalcular;
    @FXML
    private ToggleButton btnAyuda;

    // Panel donde se cargan las vistas
    @FXML
    private AnchorPane panelCarga;
    @FXML
    private Circle circleLogo;

    // Rutas de las vistas - Constantes para evitar hardcoding
    private static final String RUTA_INICIO = "/com/espoch/inflexpoint/paneles/inicio-inflex.fxml";
    private static final String RUTA_CALCULAR = "/com/espoch/inflexpoint/paneles/calcular-inflex.fxml";
    private static final String RUTA_AYUDA = "/com/espoch/inflexpoint/paneles/ayuda-inflex.fxml";
    private static final String RUTA_LOGO = "/com/espoch/inflexpoint/imagenes/Logo/inflex-point-logo.jpeg";

    // Inicializa los recursos de la vista principal
    @FXML
    public void initialize() {
        cargarVista(RUTA_INICIO);
        iniciarBotonesToggle();
        aplicarMascaraCircular();
    }

    // Aplicar máscara circular al logo
    private void aplicarMascaraCircular() {
        try {
            Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RUTA_LOGO)));
            circleLogo.setFill(new ImagePattern(img));
        } catch (Exception e) {
            System.err.println("Error al cargar el logo: " + e.getMessage());
        }
    }

    // Iniciar botones del menú en un toggleGroup
    private void iniciarBotonesToggle() {
        ToggleGroup menuToggleGroup = new ToggleGroup();
        btnInicio.setToggleGroup(menuToggleGroup);
        btnCalcular.setToggleGroup(menuToggleGroup);
        btnAyuda.setToggleGroup(menuToggleGroup);

        // Selección por defecto
        btnInicio.setSelected(true);

        menuToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.equals(btnInicio)) {
                    cargarVista(RUTA_INICIO);
                } else if (newValue.equals(btnCalcular)) {
                    cargarVista(RUTA_CALCULAR);
                } else if (newValue.equals(btnAyuda)) {
                    cargarVista(RUTA_AYUDA);
                }
            }
        });
    }

    // Cargador de vistas
    private FXMLLoader cargarVista(String rutaVista) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaVista));
            AnchorPane vista = loader.load();

            panelCarga.getChildren().setAll(vista);
            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);

            return loader;
        } catch (Exception e) {
            System.err.println("Error al cargar la vista [" + rutaVista + "]: " + e.getMessage());
            return null;
        }
    }

    // Métodos de botones de acción
    @FXML
    private void onBtnInicio(ActionEvent actionEvent) {
        btnInicio.setSelected(true);
    }

    @FXML
    private void onBtnCalcular(ActionEvent actionEvent) {
        btnCalcular.setSelected(true);
    }

    @FXML
    private void onBtnAyuda(ActionEvent actionEvent) {
        btnAyuda.setSelected(true);
    }

    @FXML
    private void onBtnSalir(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void onBuscar(ActionEvent event) {
        String expresion = txtBuscar.getText();
        if (expresion != null && !expresion.trim().isEmpty()) {
            FXMLLoader loader = cargarVista(RUTA_CALCULAR);

            if (loader != null) {
                // Obtener el controlador
                CalcularControlador controlador = loader.getController();
                if (controlador != null) {
                    controlador.cargarYCalcular(expresion);
                }
                // Actualizar visualmente los botones del menú (el ToggleGroup manejará el
                // resto)
                btnCalcular.setSelected(true);
            }
        }
    }
}
