package com.espoch.inflexpoint.test;

import com.espoch.inflexpoint.modelos.calculos.AnalizadorFuncion;
import com.espoch.inflexpoint.modelos.calculos.ResultadoAnalisis;

public class ReproduceBug {
    public static void main(String[] args) {
        AnalizadorFuncion analizador = new AnalizadorFuncion();
        String[] exprs = {
                "5", // Constante
                "2*x + 3", // Lineal
                "x^2 - 4", // Cuadrática
                "x^3 - 3*x", // Polinómica grado 3
                "1/x", // Racional
                "sin(x)" // Otra
        };
        for (String expr : exprs) {
            System.out.println("Testing: " + expr);
            try {
                ResultadoAnalisis res = analizador.analizarEnRango(expr, -5, 5, 0.1, true, true, true, true, true);
                System.out.println("  Expr: " + expr);
                for (var msg : res.getMensajesAccesibilidad()) {
                    System.out.println("  MSG: " + msg);
                }
            } catch (Exception e) {
                System.out.println("  FAILED: " + e.getMessage());
            }
            System.out.println("--------------------");
        }
    }
}
