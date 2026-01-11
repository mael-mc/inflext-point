package com.espoch.inflexpoint.controladores.vistaprincipal;

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
    private static VistaPrincipalControlador instancia;

    public static VistaPrincipalControlador getInstancia() {
        return instancia;
    }

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
        instancia = this;
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
    private void cargarVista(String rutaVista) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaVista));
            AnchorPane vista = loader.load();

            panelCarga.getChildren().setAll(vista);
            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);

        } catch (Exception e) {
            System.err.println("Error al cargar la vista [" + rutaVista + "]: " + e.getMessage());
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
        String query = txtBuscar.getText();
        if (query == null || query.trim().isEmpty())
            return;

        String normalizedQuery = normalizar(query);

        // Mapeo de Palabras Clave de Navegación Textual
        if (contienePalabras(normalizedQuery, "ayuda", "manual", "guia", "instruccion", "sintaxis", "operadores")) {
            btnAyuda.setSelected(true);
        } else if (contienePalabras(normalizedQuery, "integrante", "desarrollador", "equipo", "espoch", "juan", "karla",
                "glenda", "jojanci", "andrea")) {
            btnAyuda.setSelected(true);
        } else if (contienePalabras(normalizedQuery, "historial", "reciente", "anteriores", "pasada")) {
            btnCalcular.setSelected(true);
            // El historial se encuentra en el panel de cálculo
        } else if (contienePalabras(normalizedQuery, "inicio", "bienvenida", "portada")) {
            btnInicio.setSelected(true);
        } else if (contienePalabras(normalizedQuery, "analizar", "graficar", "calculo", "derivada", "punto",
                "monotonia",
                "concavidad")) {
            btnCalcular.setSelected(true);
        }
    }

    private boolean contienePalabras(String query, String... palabras) {
        for (String palabra : palabras) {
            if (query.contains(palabra))
                return true;
        }
        return false;
    }

    private String normalizar(String texto) {
        return texto.toLowerCase()
                .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
                .trim();
    }

    // Método público para navegar al panel de cálculo desde otros controladores
    public void navegarACalcular() {
        btnCalcular.setSelected(true);
    }
}
