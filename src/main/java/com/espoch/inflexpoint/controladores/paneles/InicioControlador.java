package com.espoch.inflexpoint.controladores.paneles;

import com.espoch.inflexpoint.controladores.vistaprincipal.VistaPrincipalControlador;
import javafx.fxml.FXML;

public class InicioControlador {

    @FXML
    private void onComenzarAnalisis() {
        VistaPrincipalControlador principal = VistaPrincipalControlador.getInstancia();
        if (principal != null) {
            principal.navegarACalcular();
        }
    }
}
