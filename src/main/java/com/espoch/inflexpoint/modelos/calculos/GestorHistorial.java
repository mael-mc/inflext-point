package com.espoch.inflexpoint.modelos.calculos;

import java.util.ArrayList;
import java.util.List;

public class GestorHistorial {
    private static GestorHistorial instancia;
    private final List<String> historial;

    private GestorHistorial() {
        historial = new ArrayList<>();
    }

    public static synchronized GestorHistorial getInstancia() {
        if (instancia == null) {
            instancia = new GestorHistorial();
        }
        return instancia;
    }

    public void agregarExpresion(String expresion) {
        if (expresion != null && !expresion.trim().isEmpty()) {
            // No duplicar si es la última ingresada
            if (historial.isEmpty() || !historial.getLast().equals(expresion)) {
                historial.add(expresion);
            }
        }
    }

    public List<String> getHistorial() {
        return new ArrayList<>(historial);
    }

    public void eliminarExpresion(String expresion) {
        historial.remove(expresion);
    }

    public void limpiarHistorial() {
        historial.clear();
    }
}
