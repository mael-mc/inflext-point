package com.espoch.inflexpoint.controladores.vistaprincipal;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class VistaPrincipalControlador {

    @FXML
    public HBox hBoxLogo;
    public Button btnSalir;
    // Toggle Buttons del menú
    @FXML
    private ToggleButton btnInicio;
    @FXML
    private ToggleButton btnCalcular;
    @FXML
    private ToggleButton btnAyuda;

    @FXML
    private javafx.scene.control.TextField txtBuscar;

    // Panel donde se cargan las vistas
    @FXML
    private AnchorPane panelCarga;

    @FXML
    private Circle circleLogo;

    // Rutas de las vistas
    private String inicio = "/com/espoch/inflexpoint/paneles/inicio-inflex.fxml";
    private String calcular = "/com/espoch/inflexpoint/paneles/calcular-inflex.fxml";
    private String ayuda = "/com/espoch/inflexpoint/paneles/ayuda-inflex.fxml";
    // private String configuracion =
    // "/com/espoch/inflexpoint/paneles/calcular-inflex.fxml";

    // Inicializa los recursos de la vista principal
    @FXML
    public void initialize() {
        cargarVista(inicio);
        iniciarBotonesToggle();
        aplicarMascaraCircular();
    }

    // Aplicar máscara circular al logo
    private void aplicarMascaraCircular() {
        Image img = new Image(
                getClass().getResourceAsStream("/com/espoch/inflexpoint/imagenes/Logo/inflex-point-logo.jpeg"));
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
    private void cargarVista(String rutaVista) {
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource(calcular));
                AnchorPane vista = loader.load();
                panelCarga.getChildren().setAll(vista);
                AnchorPane.setTopAnchor(vista, 0.0);
                AnchorPane.setBottomAnchor(vista, 0.0);
                AnchorPane.setLeftAnchor(vista, 0.0);
                AnchorPane.setRightAnchor(vista, 0.0);

                // Obtener el controlador
                com.espoch.inflexpoint.controladores.paneles.CalcularControlador controlador = loader.getController();
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
