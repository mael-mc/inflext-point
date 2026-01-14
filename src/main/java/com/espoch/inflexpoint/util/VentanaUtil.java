package com.espoch.inflexpoint.util;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.util.Objects;

/**
 * Utilidad para la gestión de ventanas y elementos visuales comunes.
 */
public class VentanaUtil {

    private static final String LOGO_PATH = "/com/espoch/inflexpoint/imagenes/Logo/inflex-point-logo.jpeg";

    /**
     * Aplica el ícono redondeado de la aplicación a un Stage.
     * 
     * @param stage El Stage al que se le aplicará el ícono.
     */
    public static void aplicarIcono(Stage stage) {
        if (stage != null) {
            try {
                stage.getIcons()
                        .add(new Image(Objects.requireNonNull(VentanaUtil.class.getResourceAsStream(LOGO_PATH))));
            } catch (Exception e) {
                System.err.println("No se pudo cargar el logo de la aplicación: " + e.getMessage());
            }
        }
    }

    /**
     * Aplica el ícono redondeado de la aplicación a una Alerta.
     * 
     * @param alert La Alerta a la que se le aplicará el ícono.
     */
    public static void aplicarIcono(Alert alert) {
        if (alert != null && alert.getDialogPane().getScene().getWindow() instanceof Stage) {
            aplicarIcono((Stage) alert.getDialogPane().getScene().getWindow());
        }
    }
}
