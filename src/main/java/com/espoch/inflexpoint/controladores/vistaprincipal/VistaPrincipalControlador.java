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
    public HBox hBoxLogo;
    @FXML
    private TextField txtBuscar;
    @FXML
    public Button btnSalir;

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

    // Rutas de las vistas
    private final String inicio = "/com/espoch/inflexpoint/paneles/inicio-inflex.fxml";
    private final String calcular = "/com/espoch/inflexpoint/paneles/calcular-inflex.fxml";
    private final String ayuda = "/com/espoch/inflexpoint/paneles/ayuda-inflex.fxml";

    // Inicializa los recursos de la vista principal
    @FXML
    public void initialize() {
        cargarVista(inicio);
        iniciarBotonesToggle();
        aplicarMascaraCircular();
    }

    // Aplicar máscara circular al logo
    private void aplicarMascaraCircular() {
        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/espoch/inflexpoint/imagenes/Logo/inflex-point-logo.jpeg")));
        circleLogo.setFill(new ImagePattern(img));
    }

    // Iniciar botones del menú en un toggleGroup
    private void iniciarBotonesToggle() {
        ToggleGroup menuToggleGroup = new ToggleGroup();
        btnInicio.setToggleGroup(menuToggleGroup);
        btnCalcular.setToggleGroup(menuToggleGroup);
        btnAyuda.setToggleGroup(menuToggleGroup);

        menuToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Toggle selectedToggle = menuToggleGroup.getSelectedToggle();
                if (selectedToggle != null) {
                    if (selectedToggle.equals(btnInicio)) {
                        // Cargar vista de inicio
                    } else if (selectedToggle.equals(btnCalcular)) {
                        // Cargar vista de calcular
                    } else if (selectedToggle.equals(btnAyuda)) {
                        // Cargar vista de ayuda
                    }
                }
            }
        });
    }

    // Cargador de vistas
    private FXMLLoader cargarVista(String rutaVista) {
        // Lógica para cargar la vista desde la ruta proporcionada
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaVista));
            AnchorPane vista = loader.load();
            panelCarga.getChildren().setAll(vista);
            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Métodos de botones de acción
    // Inicio
    public void onBtnInicio(ActionEvent actionEvent) {
        cargarVista(inicio);
    }

    // Calcular
    public void onBtnCalcular(ActionEvent actionEvent) {
        cargarVista(calcular);
    }

    // Ayuda
    public void onBtnAyuda(ActionEvent actionEvent) {
        cargarVista(ayuda);
    }

    // Salir
    public void onBtnSalir(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void onBuscar(ActionEvent event) {
        String expresion = txtBuscar.getText();
        if (expresion != null && !expresion.trim().isEmpty()) {
            try {
                FXMLLoader loader = cargarVista(calcular);

                // Obtener el controlador
                CalcularControlador controlador = loader.getController();
                controlador.cargarYCalcular(expresion);

                // Actualizar visualmente los botones del menú
                btnCalcular.setSelected(true);
                // Asegurarse de que los otros botones no estén seleccionados
                btnInicio.setSelected(false);
                btnAyuda.setSelected(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
