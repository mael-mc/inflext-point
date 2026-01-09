package com.espoch.inflexpoint.controladores.vistaprincipal;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

public class VistaPrincipalControlador {
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

    // Rutas de las vistas
    private String inicio = "/com/espoch/inflexpoint/paneles/inicio-inflex.fxml";
    private String calcular = "/com/espoch/inflexpoint/paneles/calcular-inflex.fxml";
    private String ayuda = "/com/espoch/inflexpoint/paneles/ayuda-inflex.fxml";
    // private String configuracion = "/com/espoch/inflexpoint/paneles/calcular-inflex.fxml";


    // Inicializa los recursos de la vista principal
    @FXML
    public void initialize() {
        cargarVista(inicio);
        iniciarBotonesToggle();
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
}
