package com.espoch.inflexpoint.test;

import com.espoch.inflexpoint.modelos.calculos.AnalizadorFuncion;
import com.espoch.inflexpoint.modelos.calculos.ResultadoAnalisis;

public class TestIrracional {
    public static void main(String[] args) {
        try {
            AnalizadorFuncion analizador = new AnalizadorFuncion();

            String[] funciones = { "sqrt(x)", "x^0.5", "x^(1/3)", "sqrt(-x^2+1)" };

            for (String func : funciones) {
                System.out.println("\n--- Analizando: " + func + " ---");
                ResultadoAnalisis resultado = analizador.analizarEnRango(
                        func, -10, 10, 0.1, true, true, true, true, true);

                if (resultado.getMensajesAccesibilidad() != null) {
                    for (String msg : resultado.getMensajesAccesibilidad()) {
                        System.out.println("MSG: " + msg);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
